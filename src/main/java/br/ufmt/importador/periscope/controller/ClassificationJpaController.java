/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufmt.importador.periscope.controller;

import br.ufmt.importador.periscope.controller.exceptions.NonexistentEntityException;
import br.ufmt.importador.periscope.model.Classification;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.ufmt.importador.periscope.model.Patent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class ClassificationJpaController implements Serializable {

    public ClassificationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
     public Classification findByName(String value) {
        EntityManager entityManager = getEntityManager();
        List classifications = entityManager.createQuery("SELECT c FROM Classification c WHERE c.value = :value")
                .setParameter("value", value).getResultList();
        try {
            if (classifications.size() > 0) {
                return (Classification) classifications.get(0);
            }
            return null;
        } finally {
            entityManager.close();
        }
    }

    public void create(Classification classification) {
        if (classification.getPatentCollection() == null) {
            classification.setPatentCollection(new ArrayList<Patent>());
        }
        if (classification.getPatentCollection1() == null) {
            classification.setPatentCollection1(new ArrayList<Patent>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Patent> attachedPatentCollection = new ArrayList<Patent>();
            for (Patent patentCollectionPatentToAttach : classification.getPatentCollection()) {
                patentCollectionPatentToAttach = em.getReference(patentCollectionPatentToAttach.getClass(), patentCollectionPatentToAttach.getIdPatent());
                attachedPatentCollection.add(patentCollectionPatentToAttach);
            }
            classification.setPatentCollection(attachedPatentCollection);
            Collection<Patent> attachedPatentCollection1 = new ArrayList<Patent>();
            for (Patent patentCollection1PatentToAttach : classification.getPatentCollection1()) {
                patentCollection1PatentToAttach = em.getReference(patentCollection1PatentToAttach.getClass(), patentCollection1PatentToAttach.getIdPatent());
                attachedPatentCollection1.add(patentCollection1PatentToAttach);
            }
            classification.setPatentCollection1(attachedPatentCollection1);
            em.persist(classification);
            for (Patent patentCollectionPatent : classification.getPatentCollection()) {
                patentCollectionPatent.getClassificationCollection().add(classification);
                patentCollectionPatent = em.merge(patentCollectionPatent);
            }
            for (Patent patentCollection1Patent : classification.getPatentCollection1()) {
                Classification oldIdMainClassificationOfPatentCollection1Patent = patentCollection1Patent.getIdMainClassification();
                patentCollection1Patent.setIdMainClassification(classification);
                patentCollection1Patent = em.merge(patentCollection1Patent);
                if (oldIdMainClassificationOfPatentCollection1Patent != null) {
                    oldIdMainClassificationOfPatentCollection1Patent.getPatentCollection1().remove(patentCollection1Patent);
                    oldIdMainClassificationOfPatentCollection1Patent = em.merge(oldIdMainClassificationOfPatentCollection1Patent);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Classification classification) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Classification persistentClassification = em.find(Classification.class, classification.getIdClassification());
            Collection<Patent> patentCollectionOld = persistentClassification.getPatentCollection();
            Collection<Patent> patentCollectionNew = classification.getPatentCollection();
            Collection<Patent> patentCollection1Old = persistentClassification.getPatentCollection1();
            Collection<Patent> patentCollection1New = classification.getPatentCollection1();
            Collection<Patent> attachedPatentCollectionNew = new ArrayList<Patent>();
            for (Patent patentCollectionNewPatentToAttach : patentCollectionNew) {
                patentCollectionNewPatentToAttach = em.getReference(patentCollectionNewPatentToAttach.getClass(), patentCollectionNewPatentToAttach.getIdPatent());
                attachedPatentCollectionNew.add(patentCollectionNewPatentToAttach);
            }
            patentCollectionNew = attachedPatentCollectionNew;
            classification.setPatentCollection(patentCollectionNew);
            Collection<Patent> attachedPatentCollection1New = new ArrayList<Patent>();
            for (Patent patentCollection1NewPatentToAttach : patentCollection1New) {
                patentCollection1NewPatentToAttach = em.getReference(patentCollection1NewPatentToAttach.getClass(), patentCollection1NewPatentToAttach.getIdPatent());
                attachedPatentCollection1New.add(patentCollection1NewPatentToAttach);
            }
            patentCollection1New = attachedPatentCollection1New;
            classification.setPatentCollection1(patentCollection1New);
            classification = em.merge(classification);
            for (Patent patentCollectionOldPatent : patentCollectionOld) {
                if (!patentCollectionNew.contains(patentCollectionOldPatent)) {
                    patentCollectionOldPatent.getClassificationCollection().remove(classification);
                    patentCollectionOldPatent = em.merge(patentCollectionOldPatent);
                }
            }
            for (Patent patentCollectionNewPatent : patentCollectionNew) {
                if (!patentCollectionOld.contains(patentCollectionNewPatent)) {
                    patentCollectionNewPatent.getClassificationCollection().add(classification);
                    patentCollectionNewPatent = em.merge(patentCollectionNewPatent);
                }
            }
            for (Patent patentCollection1OldPatent : patentCollection1Old) {
                if (!patentCollection1New.contains(patentCollection1OldPatent)) {
                    patentCollection1OldPatent.setIdMainClassification(null);
                    patentCollection1OldPatent = em.merge(patentCollection1OldPatent);
                }
            }
            for (Patent patentCollection1NewPatent : patentCollection1New) {
                if (!patentCollection1Old.contains(patentCollection1NewPatent)) {
                    Classification oldIdMainClassificationOfPatentCollection1NewPatent = patentCollection1NewPatent.getIdMainClassification();
                    patentCollection1NewPatent.setIdMainClassification(classification);
                    patentCollection1NewPatent = em.merge(patentCollection1NewPatent);
                    if (oldIdMainClassificationOfPatentCollection1NewPatent != null && !oldIdMainClassificationOfPatentCollection1NewPatent.equals(classification)) {
                        oldIdMainClassificationOfPatentCollection1NewPatent.getPatentCollection1().remove(patentCollection1NewPatent);
                        oldIdMainClassificationOfPatentCollection1NewPatent = em.merge(oldIdMainClassificationOfPatentCollection1NewPatent);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = classification.getIdClassification();
                if (findClassification(id) == null) {
                    throw new NonexistentEntityException("The classification with id " + id + " no longer exists.");
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
            Classification classification;
            try {
                classification = em.getReference(Classification.class, id);
                classification.getIdClassification();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The classification with id " + id + " no longer exists.", enfe);
            }
            Collection<Patent> patentCollection = classification.getPatentCollection();
            for (Patent patentCollectionPatent : patentCollection) {
                patentCollectionPatent.getClassificationCollection().remove(classification);
                patentCollectionPatent = em.merge(patentCollectionPatent);
            }
            Collection<Patent> patentCollection1 = classification.getPatentCollection1();
            for (Patent patentCollection1Patent : patentCollection1) {
                patentCollection1Patent.setIdMainClassification(null);
                patentCollection1Patent = em.merge(patentCollection1Patent);
            }
            em.remove(classification);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Classification> findClassificationEntities() {
        return findClassificationEntities(true, -1, -1);
    }

    public List<Classification> findClassificationEntities(int maxResults, int firstResult) {
        return findClassificationEntities(false, maxResults, firstResult);
    }

    private List<Classification> findClassificationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Classification.class));
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

    public Classification findClassification(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Classification.class, id);
        } finally {
            em.close();
        }
    }

    public int getClassificationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Classification> rt = cq.from(Classification.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
