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
import br.ufmt.importador.periscope.model.Country;
import br.ufmt.importador.periscope.model.Priority;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class PriorityJpaController implements Serializable {

    public PriorityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Priority priority) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patent idPatent = priority.getIdPatent();
            if (idPatent != null) {
                idPatent = em.getReference(idPatent.getClass(), idPatent.getIdPatent());
                priority.setIdPatent(idPatent);
            }
            Country idCountry = priority.getIdCountry();
            if (idCountry != null) {
                idCountry = em.getReference(idCountry.getClass(), idCountry.getIdCountry());
                priority.setIdCountry(idCountry);
            }
            em.persist(priority);
            if (idPatent != null) {
                idPatent.getPriorityCollection().add(priority);
                idPatent = em.merge(idPatent);
            }
            if (idCountry != null) {
                idCountry.getPriorityCollection().add(priority);
                idCountry = em.merge(idCountry);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Priority priority) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Priority persistentPriority = em.find(Priority.class, priority.getIdPriority());
            Patent idPatentOld = persistentPriority.getIdPatent();
            Patent idPatentNew = priority.getIdPatent();
            Country idCountryOld = persistentPriority.getIdCountry();
            Country idCountryNew = priority.getIdCountry();
            if (idPatentNew != null) {
                idPatentNew = em.getReference(idPatentNew.getClass(), idPatentNew.getIdPatent());
                priority.setIdPatent(idPatentNew);
            }
            if (idCountryNew != null) {
                idCountryNew = em.getReference(idCountryNew.getClass(), idCountryNew.getIdCountry());
                priority.setIdCountry(idCountryNew);
            }
            priority = em.merge(priority);
            if (idPatentOld != null && !idPatentOld.equals(idPatentNew)) {
                idPatentOld.getPriorityCollection().remove(priority);
                idPatentOld = em.merge(idPatentOld);
            }
            if (idPatentNew != null && !idPatentNew.equals(idPatentOld)) {
                idPatentNew.getPriorityCollection().add(priority);
                idPatentNew = em.merge(idPatentNew);
            }
            if (idCountryOld != null && !idCountryOld.equals(idCountryNew)) {
                idCountryOld.getPriorityCollection().remove(priority);
                idCountryOld = em.merge(idCountryOld);
            }
            if (idCountryNew != null && !idCountryNew.equals(idCountryOld)) {
                idCountryNew.getPriorityCollection().add(priority);
                idCountryNew = em.merge(idCountryNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = priority.getIdPriority();
                if (findPriority(id) == null) {
                    throw new NonexistentEntityException("The priority with id " + id + " no longer exists.");
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
            Priority priority;
            try {
                priority = em.getReference(Priority.class, id);
                priority.getIdPriority();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The priority with id " + id + " no longer exists.", enfe);
            }
            Patent idPatent = priority.getIdPatent();
            if (idPatent != null) {
                idPatent.getPriorityCollection().remove(priority);
                idPatent = em.merge(idPatent);
            }
            Country idCountry = priority.getIdCountry();
            if (idCountry != null) {
                idCountry.getPriorityCollection().remove(priority);
                idCountry = em.merge(idCountry);
            }
            em.remove(priority);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Priority> findPriorityEntities() {
        return findPriorityEntities(true, -1, -1);
    }

    public List<Priority> findPriorityEntities(int maxResults, int firstResult) {
        return findPriorityEntities(false, maxResults, firstResult);
    }

    private List<Priority> findPriorityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Priority.class));
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

    public Priority findPriority(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Priority.class, id);
        } finally {
            em.close();
        }
    }

    public int getPriorityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Priority> rt = cq.from(Priority.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
