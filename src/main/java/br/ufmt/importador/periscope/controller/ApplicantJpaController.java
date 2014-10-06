/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufmt.importador.periscope.controller;

import br.ufmt.importador.periscope.controller.exceptions.NonexistentEntityException;
import br.ufmt.importador.periscope.model.Applicant;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.ufmt.importador.periscope.model.Country;
import br.ufmt.importador.periscope.model.Patent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ApplicantJpaController implements Serializable {

    public ApplicantJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Applicant findByName(String name) {
        EntityManager entityManager = getEntityManager();
        List applicants = entityManager.createQuery("SELECT i FROM Applicant i WHERE i.name = :name")
                .setParameter("name", name).getResultList();
        try {
            if (applicants.size() > 0) {
                return (Applicant) applicants.get(0);
            }
            return null;
        } finally {
            entityManager.close();
        }
    }

    public void create(Applicant applicant) {
        if (applicant.getPatentCollection() == null) {
            applicant.setPatentCollection(new ArrayList<Patent>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country idCountry = applicant.getIdCountry();
            if (idCountry != null) {
                idCountry = em.getReference(idCountry.getClass(), idCountry.getIdCountry());
                applicant.setIdCountry(idCountry);
            }
            Collection<Patent> attachedPatentCollection = new ArrayList<Patent>();
            for (Patent patentCollectionPatentToAttach : applicant.getPatentCollection()) {
                patentCollectionPatentToAttach = em.getReference(patentCollectionPatentToAttach.getClass(), patentCollectionPatentToAttach.getIdPatent());
                attachedPatentCollection.add(patentCollectionPatentToAttach);
            }
            applicant.setPatentCollection(attachedPatentCollection);
            em.persist(applicant);
            if (idCountry != null) {
                idCountry.getApplicantCollection().add(applicant);
                idCountry = em.merge(idCountry);
            }
            for (Patent patentCollectionPatent : applicant.getPatentCollection()) {
                patentCollectionPatent.getApplicantCollection().add(applicant);
                patentCollectionPatent = em.merge(patentCollectionPatent);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Applicant applicant) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Applicant persistentApplicant = em.find(Applicant.class, applicant.getIdApplicant());
            Country idCountryOld = persistentApplicant.getIdCountry();
            Country idCountryNew = applicant.getIdCountry();
            Collection<Patent> patentCollectionOld = persistentApplicant.getPatentCollection();
            Collection<Patent> patentCollectionNew = applicant.getPatentCollection();
            if (idCountryNew != null) {
                idCountryNew = em.getReference(idCountryNew.getClass(), idCountryNew.getIdCountry());
                applicant.setIdCountry(idCountryNew);
            }
            Collection<Patent> attachedPatentCollectionNew = new ArrayList<Patent>();
            for (Patent patentCollectionNewPatentToAttach : patentCollectionNew) {
                patentCollectionNewPatentToAttach = em.getReference(patentCollectionNewPatentToAttach.getClass(), patentCollectionNewPatentToAttach.getIdPatent());
                attachedPatentCollectionNew.add(patentCollectionNewPatentToAttach);
            }
            patentCollectionNew = attachedPatentCollectionNew;
            applicant.setPatentCollection(patentCollectionNew);
            applicant = em.merge(applicant);
            if (idCountryOld != null && !idCountryOld.equals(idCountryNew)) {
                idCountryOld.getApplicantCollection().remove(applicant);
                idCountryOld = em.merge(idCountryOld);
            }
            if (idCountryNew != null && !idCountryNew.equals(idCountryOld)) {
                idCountryNew.getApplicantCollection().add(applicant);
                idCountryNew = em.merge(idCountryNew);
            }
            for (Patent patentCollectionOldPatent : patentCollectionOld) {
                if (!patentCollectionNew.contains(patentCollectionOldPatent)) {
                    patentCollectionOldPatent.getApplicantCollection().remove(applicant);
                    patentCollectionOldPatent = em.merge(patentCollectionOldPatent);
                }
            }
            for (Patent patentCollectionNewPatent : patentCollectionNew) {
                if (!patentCollectionOld.contains(patentCollectionNewPatent)) {
                    patentCollectionNewPatent.getApplicantCollection().add(applicant);
                    patentCollectionNewPatent = em.merge(patentCollectionNewPatent);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = applicant.getIdApplicant();
                if (findApplicant(id) == null) {
                    throw new NonexistentEntityException("The applicant with id " + id + " no longer exists.");
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
            Applicant applicant;
            try {
                applicant = em.getReference(Applicant.class, id);
                applicant.getIdApplicant();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The applicant with id " + id + " no longer exists.", enfe);
            }
            Country idCountry = applicant.getIdCountry();
            if (idCountry != null) {
                idCountry.getApplicantCollection().remove(applicant);
                idCountry = em.merge(idCountry);
            }
            Collection<Patent> patentCollection = applicant.getPatentCollection();
            for (Patent patentCollectionPatent : patentCollection) {
                patentCollectionPatent.getApplicantCollection().remove(applicant);
                patentCollectionPatent = em.merge(patentCollectionPatent);
            }
            em.remove(applicant);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Applicant> findApplicantEntities() {
        return findApplicantEntities(true, -1, -1);
    }

    public List<Applicant> findApplicantEntities(int maxResults, int firstResult) {
        return findApplicantEntities(false, maxResults, firstResult);
    }

    private List<Applicant> findApplicantEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Applicant.class));
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

    public Applicant findApplicant(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Applicant.class, id);
        } finally {
            em.close();
        }
    }

    public int getApplicantCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Applicant> rt = cq.from(Applicant.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
