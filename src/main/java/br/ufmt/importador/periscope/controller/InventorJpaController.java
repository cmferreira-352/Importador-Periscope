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
import br.ufmt.importador.periscope.model.Country;
import br.ufmt.importador.periscope.model.Inventor;
import br.ufmt.importador.periscope.model.Patent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class InventorJpaController implements Serializable {

    public InventorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Inventor findByName(String name) {
        EntityManager entityManager = getEntityManager();
        List inventors = entityManager.createQuery("SELECT i FROM Inventor i WHERE i.nome = :name")
                .setParameter("name", name).getResultList();
        try {
            if (inventors.size() > 0) {
                return (Inventor) inventors.get(0);
            }
            return null;
        } finally {
            entityManager.close();
        }
    }

    public void create(Inventor inventor) {
        if (inventor.getPatentCollection() == null) {
            inventor.setPatentCollection(new ArrayList<Patent>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country idCountry = inventor.getIdCountry();
            if (idCountry != null) {
                idCountry = em.getReference(idCountry.getClass(), idCountry.getIdCountry());
                inventor.setIdCountry(idCountry);
            }
            Collection<Patent> attachedPatentCollection = new ArrayList<Patent>();
            for (Patent patentCollectionPatentToAttach : inventor.getPatentCollection()) {
                patentCollectionPatentToAttach = em.getReference(patentCollectionPatentToAttach.getClass(), patentCollectionPatentToAttach.getIdPatent());
                attachedPatentCollection.add(patentCollectionPatentToAttach);
            }
            inventor.setPatentCollection(attachedPatentCollection);
            em.persist(inventor);
            if (idCountry != null) {
                idCountry.getInventorCollection().add(inventor);
                idCountry = em.merge(idCountry);
            }
            for (Patent patentCollectionPatent : inventor.getPatentCollection()) {
                patentCollectionPatent.getInventorCollection().add(inventor);
                patentCollectionPatent = em.merge(patentCollectionPatent);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Inventor inventor) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Inventor persistentInventor = em.find(Inventor.class, inventor.getIdInventor());
            Country idCountryOld = persistentInventor.getIdCountry();
            Country idCountryNew = inventor.getIdCountry();
            Collection<Patent> patentCollectionOld = persistentInventor.getPatentCollection();
            Collection<Patent> patentCollectionNew = inventor.getPatentCollection();
            if (idCountryNew != null) {
                idCountryNew = em.getReference(idCountryNew.getClass(), idCountryNew.getIdCountry());
                inventor.setIdCountry(idCountryNew);
            }
            Collection<Patent> attachedPatentCollectionNew = new ArrayList<Patent>();
            for (Patent patentCollectionNewPatentToAttach : patentCollectionNew) {
                patentCollectionNewPatentToAttach = em.getReference(patentCollectionNewPatentToAttach.getClass(), patentCollectionNewPatentToAttach.getIdPatent());
                attachedPatentCollectionNew.add(patentCollectionNewPatentToAttach);
            }
            patentCollectionNew = attachedPatentCollectionNew;
            inventor.setPatentCollection(patentCollectionNew);
            inventor = em.merge(inventor);
            if (idCountryOld != null && !idCountryOld.equals(idCountryNew)) {
                idCountryOld.getInventorCollection().remove(inventor);
                idCountryOld = em.merge(idCountryOld);
            }
            if (idCountryNew != null && !idCountryNew.equals(idCountryOld)) {
                idCountryNew.getInventorCollection().add(inventor);
                idCountryNew = em.merge(idCountryNew);
            }
            for (Patent patentCollectionOldPatent : patentCollectionOld) {
                if (!patentCollectionNew.contains(patentCollectionOldPatent)) {
                    patentCollectionOldPatent.getInventorCollection().remove(inventor);
                    patentCollectionOldPatent = em.merge(patentCollectionOldPatent);
                }
            }
            for (Patent patentCollectionNewPatent : patentCollectionNew) {
                if (!patentCollectionOld.contains(patentCollectionNewPatent)) {
                    patentCollectionNewPatent.getInventorCollection().add(inventor);
                    patentCollectionNewPatent = em.merge(patentCollectionNewPatent);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = inventor.getIdInventor();
                if (findInventor(id) == null) {
                    throw new NonexistentEntityException("The inventor with id " + id + " no longer exists.");
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
            Inventor inventor;
            try {
                inventor = em.getReference(Inventor.class, id);
                inventor.getIdInventor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The inventor with id " + id + " no longer exists.", enfe);
            }
            Country idCountry = inventor.getIdCountry();
            if (idCountry != null) {
                idCountry.getInventorCollection().remove(inventor);
                idCountry = em.merge(idCountry);
            }
            Collection<Patent> patentCollection = inventor.getPatentCollection();
            for (Patent patentCollectionPatent : patentCollection) {
                patentCollectionPatent.getInventorCollection().remove(inventor);
                patentCollectionPatent = em.merge(patentCollectionPatent);
            }
            em.remove(inventor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Inventor> findInventorEntities() {
        return findInventorEntities(true, -1, -1);
    }

    public List<Inventor> findInventorEntities(int maxResults, int firstResult) {
        return findInventorEntities(false, maxResults, firstResult);
    }

    private List<Inventor> findInventorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Inventor.class));
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

    public Inventor findInventor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Inventor.class, id);
        } finally {
            em.close();
        }
    }

    public int getInventorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Inventor> rt = cq.from(Inventor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
