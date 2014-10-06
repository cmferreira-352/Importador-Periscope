/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufmt.importador.periscope.controller;

import br.ufmt.importador.periscope.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.ufmt.importador.periscope.model.Patent;
import br.ufmt.importador.periscope.model.Project;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class ProjectJpaController implements Serializable {

    public ProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Project project) {
        if (project.getPatentCollection() == null) {
            project.setPatentCollection(new ArrayList<Patent>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Patent> attachedPatentCollection = new ArrayList<Patent>();
            for (Patent patentCollectionPatentToAttach : project.getPatentCollection()) {
                patentCollectionPatentToAttach = em.getReference(patentCollectionPatentToAttach.getClass(), patentCollectionPatentToAttach.getIdPatent());
                attachedPatentCollection.add(patentCollectionPatentToAttach);
            }
            project.setPatentCollection(attachedPatentCollection);
            em.persist(project);
            for (Patent patentCollectionPatent : project.getPatentCollection()) {
                Project oldIdProjectOfPatentCollectionPatent = patentCollectionPatent.getIdProject();
                patentCollectionPatent.setIdProject(project);
                patentCollectionPatent = em.merge(patentCollectionPatent);
                if (oldIdProjectOfPatentCollectionPatent != null) {
                    oldIdProjectOfPatentCollectionPatent.getPatentCollection().remove(patentCollectionPatent);
                    oldIdProjectOfPatentCollectionPatent = em.merge(oldIdProjectOfPatentCollectionPatent);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Project project) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project persistentProject = em.find(Project.class, project.getIdProject());
            Collection<Patent> patentCollectionOld = persistentProject.getPatentCollection();
            Collection<Patent> patentCollectionNew = project.getPatentCollection();
            Collection<Patent> attachedPatentCollectionNew = new ArrayList<Patent>();
            for (Patent patentCollectionNewPatentToAttach : patentCollectionNew) {
                patentCollectionNewPatentToAttach = em.getReference(patentCollectionNewPatentToAttach.getClass(), patentCollectionNewPatentToAttach.getIdPatent());
                attachedPatentCollectionNew.add(patentCollectionNewPatentToAttach);
            }
            patentCollectionNew = attachedPatentCollectionNew;
            project.setPatentCollection(patentCollectionNew);
            project = em.merge(project);
            for (Patent patentCollectionOldPatent : patentCollectionOld) {
                if (!patentCollectionNew.contains(patentCollectionOldPatent)) {
                    patentCollectionOldPatent.setIdProject(null);
                    patentCollectionOldPatent = em.merge(patentCollectionOldPatent);
                }
            }
            for (Patent patentCollectionNewPatent : patentCollectionNew) {
                if (!patentCollectionOld.contains(patentCollectionNewPatent)) {
                    Project oldIdProjectOfPatentCollectionNewPatent = patentCollectionNewPatent.getIdProject();
                    patentCollectionNewPatent.setIdProject(project);
                    patentCollectionNewPatent = em.merge(patentCollectionNewPatent);
                    if (oldIdProjectOfPatentCollectionNewPatent != null && !oldIdProjectOfPatentCollectionNewPatent.equals(project)) {
                        oldIdProjectOfPatentCollectionNewPatent.getPatentCollection().remove(patentCollectionNewPatent);
                        oldIdProjectOfPatentCollectionNewPatent = em.merge(oldIdProjectOfPatentCollectionNewPatent);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = project.getIdProject();
                if (findProject(id) == null) {
                    throw new NonexistentEntityException("The project with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project project;
            try {
                project = em.getReference(Project.class, id);
                project.getIdProject();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The project with id " + id + " no longer exists.", enfe);
            }
            Collection<Patent> patentCollection = project.getPatentCollection();
            for (Patent patentCollectionPatent : patentCollection) {
                patentCollectionPatent.setIdProject(null);
                patentCollectionPatent = em.merge(patentCollectionPatent);
            }
            em.remove(project);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findProjectEntities() {
        return findProjectEntities(true, -1, -1);
    }

    public List<Project> findProjectEntities(int maxResults, int firstResult) {
        return findProjectEntities(false, maxResults, firstResult);
    }

    private List<Project> findProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Project.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Project findProject(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Project> rt = cq.from(Project.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
