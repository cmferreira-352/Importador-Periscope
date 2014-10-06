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
import br.ufmt.importador.periscope.model.Project;
import br.ufmt.importador.periscope.model.Classification;
import br.ufmt.importador.periscope.model.Applicant;
import java.util.ArrayList;
import java.util.Collection;
import br.ufmt.importador.periscope.model.Inventor;
import br.ufmt.importador.periscope.model.Patent;
import br.ufmt.importador.periscope.model.Priority;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class PatentJpaController implements Serializable {

    public PatentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Patent patent) {
        if (patent.getApplicantCollection() == null) {
            patent.setApplicantCollection(new ArrayList<Applicant>());
        }
        if (patent.getInventorCollection() == null) {
            patent.setInventorCollection(new ArrayList<Inventor>());
        }
        if (patent.getClassificationCollection() == null) {
            patent.setClassificationCollection(new ArrayList<Classification>());
        }
        if (patent.getPriorityCollection() == null) {
            patent.setPriorityCollection(new ArrayList<Priority>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project idProject = patent.getIdProject();
            if (idProject != null) {
                idProject = em.getReference(idProject.getClass(), idProject.getIdProject());
                patent.setIdProject(idProject);
            }
            Classification idMainClassification = patent.getIdMainClassification();
            if (idMainClassification != null) {
                idMainClassification = em.getReference(idMainClassification.getClass(), idMainClassification.getIdClassification());
                patent.setIdMainClassification(idMainClassification);
            }
            Collection<Applicant> attachedApplicantCollection = new ArrayList<Applicant>();
            for (Applicant applicantCollectionApplicantToAttach : patent.getApplicantCollection()) {
                applicantCollectionApplicantToAttach = em.getReference(applicantCollectionApplicantToAttach.getClass(), applicantCollectionApplicantToAttach.getIdApplicant());
                attachedApplicantCollection.add(applicantCollectionApplicantToAttach);
            }
            patent.setApplicantCollection(attachedApplicantCollection);
            Collection<Inventor> attachedInventorCollection = new ArrayList<Inventor>();
            for (Inventor inventorCollectionInventorToAttach : patent.getInventorCollection()) {
                inventorCollectionInventorToAttach = em.getReference(inventorCollectionInventorToAttach.getClass(), inventorCollectionInventorToAttach.getIdInventor());
                attachedInventorCollection.add(inventorCollectionInventorToAttach);
            }
            patent.setInventorCollection(attachedInventorCollection);
            Collection<Classification> attachedClassificationCollection = new ArrayList<Classification>();
            for (Classification classificationCollectionClassificationToAttach : patent.getClassificationCollection()) {
                classificationCollectionClassificationToAttach = em.getReference(classificationCollectionClassificationToAttach.getClass(), classificationCollectionClassificationToAttach.getIdClassification());
                attachedClassificationCollection.add(classificationCollectionClassificationToAttach);
            }
            patent.setClassificationCollection(attachedClassificationCollection);
            Collection<Priority> attachedPriorityCollection = new ArrayList<Priority>();
            for (Priority priorityCollectionPriorityToAttach : patent.getPriorityCollection()) {
                priorityCollectionPriorityToAttach = em.getReference(priorityCollectionPriorityToAttach.getClass(), priorityCollectionPriorityToAttach.getIdPriority());
                attachedPriorityCollection.add(priorityCollectionPriorityToAttach);
            }
            patent.setPriorityCollection(attachedPriorityCollection);
            em.persist(patent);
            if (idProject != null) {
                idProject.getPatentCollection().add(patent);
                idProject = em.merge(idProject);
            }
            if (idMainClassification != null) {
                idMainClassification.getPatentCollection().add(patent);
                idMainClassification = em.merge(idMainClassification);
            }
            for (Applicant applicantCollectionApplicant : patent.getApplicantCollection()) {
                applicantCollectionApplicant.getPatentCollection().add(patent);
                applicantCollectionApplicant = em.merge(applicantCollectionApplicant);
            }
            for (Inventor inventorCollectionInventor : patent.getInventorCollection()) {
                inventorCollectionInventor.getPatentCollection().add(patent);
                inventorCollectionInventor = em.merge(inventorCollectionInventor);
            }
            for (Classification classificationCollectionClassification : patent.getClassificationCollection()) {
                classificationCollectionClassification.getPatentCollection().add(patent);
                classificationCollectionClassification = em.merge(classificationCollectionClassification);
            }
            for (Priority priorityCollectionPriority : patent.getPriorityCollection()) {
                Patent oldIdPatentOfPriorityCollectionPriority = priorityCollectionPriority.getIdPatent();
                priorityCollectionPriority.setIdPatent(patent);
                priorityCollectionPriority = em.merge(priorityCollectionPriority);
                if (oldIdPatentOfPriorityCollectionPriority != null) {
                    oldIdPatentOfPriorityCollectionPriority.getPriorityCollection().remove(priorityCollectionPriority);
                    oldIdPatentOfPriorityCollectionPriority = em.merge(oldIdPatentOfPriorityCollectionPriority);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Patent patent) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patent persistentPatent = em.find(Patent.class, patent.getIdPatent());
            Project idProjectOld = persistentPatent.getIdProject();
            Project idProjectNew = patent.getIdProject();
            Classification idMainClassificationOld = persistentPatent.getIdMainClassification();
            Classification idMainClassificationNew = patent.getIdMainClassification();
            Collection<Applicant> applicantCollectionOld = persistentPatent.getApplicantCollection();
            Collection<Applicant> applicantCollectionNew = patent.getApplicantCollection();
            Collection<Inventor> inventorCollectionOld = persistentPatent.getInventorCollection();
            Collection<Inventor> inventorCollectionNew = patent.getInventorCollection();
            Collection<Classification> classificationCollectionOld = persistentPatent.getClassificationCollection();
            Collection<Classification> classificationCollectionNew = patent.getClassificationCollection();
            Collection<Priority> priorityCollectionOld = persistentPatent.getPriorityCollection();
            Collection<Priority> priorityCollectionNew = patent.getPriorityCollection();
            if (idProjectNew != null) {
                idProjectNew = em.getReference(idProjectNew.getClass(), idProjectNew.getIdProject());
                patent.setIdProject(idProjectNew);
            }
            if (idMainClassificationNew != null) {
                idMainClassificationNew = em.getReference(idMainClassificationNew.getClass(), idMainClassificationNew.getIdClassification());
                patent.setIdMainClassification(idMainClassificationNew);
            }
            Collection<Applicant> attachedApplicantCollectionNew = new ArrayList<Applicant>();
            for (Applicant applicantCollectionNewApplicantToAttach : applicantCollectionNew) {
                applicantCollectionNewApplicantToAttach = em.getReference(applicantCollectionNewApplicantToAttach.getClass(), applicantCollectionNewApplicantToAttach.getIdApplicant());
                attachedApplicantCollectionNew.add(applicantCollectionNewApplicantToAttach);
            }
            applicantCollectionNew = attachedApplicantCollectionNew;
            patent.setApplicantCollection(applicantCollectionNew);
            Collection<Inventor> attachedInventorCollectionNew = new ArrayList<Inventor>();
            for (Inventor inventorCollectionNewInventorToAttach : inventorCollectionNew) {
                inventorCollectionNewInventorToAttach = em.getReference(inventorCollectionNewInventorToAttach.getClass(), inventorCollectionNewInventorToAttach.getIdInventor());
                attachedInventorCollectionNew.add(inventorCollectionNewInventorToAttach);
            }
            inventorCollectionNew = attachedInventorCollectionNew;
            patent.setInventorCollection(inventorCollectionNew);
            Collection<Classification> attachedClassificationCollectionNew = new ArrayList<Classification>();
            for (Classification classificationCollectionNewClassificationToAttach : classificationCollectionNew) {
                classificationCollectionNewClassificationToAttach = em.getReference(classificationCollectionNewClassificationToAttach.getClass(), classificationCollectionNewClassificationToAttach.getIdClassification());
                attachedClassificationCollectionNew.add(classificationCollectionNewClassificationToAttach);
            }
            classificationCollectionNew = attachedClassificationCollectionNew;
            patent.setClassificationCollection(classificationCollectionNew);
            Collection<Priority> attachedPriorityCollectionNew = new ArrayList<Priority>();
            for (Priority priorityCollectionNewPriorityToAttach : priorityCollectionNew) {
                priorityCollectionNewPriorityToAttach = em.getReference(priorityCollectionNewPriorityToAttach.getClass(), priorityCollectionNewPriorityToAttach.getIdPriority());
                attachedPriorityCollectionNew.add(priorityCollectionNewPriorityToAttach);
            }
            priorityCollectionNew = attachedPriorityCollectionNew;
            patent.setPriorityCollection(priorityCollectionNew);
            patent = em.merge(patent);
            if (idProjectOld != null && !idProjectOld.equals(idProjectNew)) {
                idProjectOld.getPatentCollection().remove(patent);
                idProjectOld = em.merge(idProjectOld);
            }
            if (idProjectNew != null && !idProjectNew.equals(idProjectOld)) {
                idProjectNew.getPatentCollection().add(patent);
                idProjectNew = em.merge(idProjectNew);
            }
            if (idMainClassificationOld != null && !idMainClassificationOld.equals(idMainClassificationNew)) {
                idMainClassificationOld.getPatentCollection().remove(patent);
                idMainClassificationOld = em.merge(idMainClassificationOld);
            }
            if (idMainClassificationNew != null && !idMainClassificationNew.equals(idMainClassificationOld)) {
                idMainClassificationNew.getPatentCollection().add(patent);
                idMainClassificationNew = em.merge(idMainClassificationNew);
            }
            for (Applicant applicantCollectionOldApplicant : applicantCollectionOld) {
                if (!applicantCollectionNew.contains(applicantCollectionOldApplicant)) {
                    applicantCollectionOldApplicant.getPatentCollection().remove(patent);
                    applicantCollectionOldApplicant = em.merge(applicantCollectionOldApplicant);
                }
            }
            for (Applicant applicantCollectionNewApplicant : applicantCollectionNew) {
                if (!applicantCollectionOld.contains(applicantCollectionNewApplicant)) {
                    applicantCollectionNewApplicant.getPatentCollection().add(patent);
                    applicantCollectionNewApplicant = em.merge(applicantCollectionNewApplicant);
                }
            }
            for (Inventor inventorCollectionOldInventor : inventorCollectionOld) {
                if (!inventorCollectionNew.contains(inventorCollectionOldInventor)) {
                    inventorCollectionOldInventor.getPatentCollection().remove(patent);
                    inventorCollectionOldInventor = em.merge(inventorCollectionOldInventor);
                }
            }
            for (Inventor inventorCollectionNewInventor : inventorCollectionNew) {
                if (!inventorCollectionOld.contains(inventorCollectionNewInventor)) {
                    inventorCollectionNewInventor.getPatentCollection().add(patent);
                    inventorCollectionNewInventor = em.merge(inventorCollectionNewInventor);
                }
            }
            for (Classification classificationCollectionOldClassification : classificationCollectionOld) {
                if (!classificationCollectionNew.contains(classificationCollectionOldClassification)) {
                    classificationCollectionOldClassification.getPatentCollection().remove(patent);
                    classificationCollectionOldClassification = em.merge(classificationCollectionOldClassification);
                }
            }
            for (Classification classificationCollectionNewClassification : classificationCollectionNew) {
                if (!classificationCollectionOld.contains(classificationCollectionNewClassification)) {
                    classificationCollectionNewClassification.getPatentCollection().add(patent);
                    classificationCollectionNewClassification = em.merge(classificationCollectionNewClassification);
                }
            }
            for (Priority priorityCollectionOldPriority : priorityCollectionOld) {
                if (!priorityCollectionNew.contains(priorityCollectionOldPriority)) {
                    priorityCollectionOldPriority.setIdPatent(null);
                    priorityCollectionOldPriority = em.merge(priorityCollectionOldPriority);
                }
            }
            for (Priority priorityCollectionNewPriority : priorityCollectionNew) {
                if (!priorityCollectionOld.contains(priorityCollectionNewPriority)) {
                    Patent oldIdPatentOfPriorityCollectionNewPriority = priorityCollectionNewPriority.getIdPatent();
                    priorityCollectionNewPriority.setIdPatent(patent);
                    priorityCollectionNewPriority = em.merge(priorityCollectionNewPriority);
                    if (oldIdPatentOfPriorityCollectionNewPriority != null && !oldIdPatentOfPriorityCollectionNewPriority.equals(patent)) {
                        oldIdPatentOfPriorityCollectionNewPriority.getPriorityCollection().remove(priorityCollectionNewPriority);
                        oldIdPatentOfPriorityCollectionNewPriority = em.merge(oldIdPatentOfPriorityCollectionNewPriority);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = patent.getIdPatent();
                if (findPatent(id) == null) {
                    throw new NonexistentEntityException("The patent with id " + id + " no longer exists.");
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
            Patent patent;
            try {
                patent = em.getReference(Patent.class, id);
                patent.getIdPatent();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The patent with id " + id + " no longer exists.", enfe);
            }
            Project idProject = patent.getIdProject();
            if (idProject != null) {
                idProject.getPatentCollection().remove(patent);
                idProject = em.merge(idProject);
            }
            Classification idMainClassification = patent.getIdMainClassification();
            if (idMainClassification != null) {
                idMainClassification.getPatentCollection().remove(patent);
                idMainClassification = em.merge(idMainClassification);
            }
            Collection<Applicant> applicantCollection = patent.getApplicantCollection();
            for (Applicant applicantCollectionApplicant : applicantCollection) {
                applicantCollectionApplicant.getPatentCollection().remove(patent);
                applicantCollectionApplicant = em.merge(applicantCollectionApplicant);
            }
            Collection<Inventor> inventorCollection = patent.getInventorCollection();
            for (Inventor inventorCollectionInventor : inventorCollection) {
                inventorCollectionInventor.getPatentCollection().remove(patent);
                inventorCollectionInventor = em.merge(inventorCollectionInventor);
            }
            Collection<Classification> classificationCollection = patent.getClassificationCollection();
            for (Classification classificationCollectionClassification : classificationCollection) {
                classificationCollectionClassification.getPatentCollection().remove(patent);
                classificationCollectionClassification = em.merge(classificationCollectionClassification);
            }
            Collection<Priority> priorityCollection = patent.getPriorityCollection();
            for (Priority priorityCollectionPriority : priorityCollection) {
                priorityCollectionPriority.setIdPatent(null);
                priorityCollectionPriority = em.merge(priorityCollectionPriority);
            }
            em.remove(patent);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Patent> findPatentEntities() {
        return findPatentEntities(true, -1, -1);
    }

    public List<Patent> findPatentEntities(int maxResults, int firstResult) {
        return findPatentEntities(false, maxResults, firstResult);
    }

    private List<Patent> findPatentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Patent.class));
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

    public Patent findPatent(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Patent.class, id);
        } finally {
            em.close();
        }
    }

    public int getPatentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Patent> rt = cq.from(Patent.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
