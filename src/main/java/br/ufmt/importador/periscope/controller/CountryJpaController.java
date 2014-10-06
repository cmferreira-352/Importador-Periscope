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
import br.ufmt.importador.periscope.model.Priority;
import java.util.ArrayList;
import java.util.Collection;
import br.ufmt.importador.periscope.model.Applicant;
import br.ufmt.importador.periscope.model.Country;
import br.ufmt.importador.periscope.model.Inventor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class CountryJpaController implements Serializable {

    private EntityManagerFactory emf;

    public CountryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
//        System.out.println(emf.toString());
        return emf.createEntityManager();
    }

    public void create(Country country) {
//        if (country.getPriorityCollection() == null) {
//            country.setPriorityCollection(new ArrayList<Priority>());
//        }
//        if (country.getApplicantCollection() == null) {
//            country.setApplicantCollection(new ArrayList<Applicant>());
//        }
//        if (country.getInventorCollection() == null) {
//            country.setInventorCollection(new ArrayList<Inventor>());
//        }
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            Collection<Priority> attachedPriorityCollection = new ArrayList<Priority>();
//            for (Priority priorityCollectionPriorityToAttach : country.getPriorityCollection()) {
//                priorityCollectionPriorityToAttach = em.getReference(priorityCollectionPriorityToAttach.getClass(), priorityCollectionPriorityToAttach.getIdPriority());
//                attachedPriorityCollection.add(priorityCollectionPriorityToAttach);
//            }
//            country.setPriorityCollection(attachedPriorityCollection);
//            Collection<Applicant> attachedApplicantCollection = new ArrayList<Applicant>();
//            for (Applicant applicantCollectionApplicantToAttach : country.getApplicantCollection()) {
//                applicantCollectionApplicantToAttach = em.getReference(applicantCollectionApplicantToAttach.getClass(), applicantCollectionApplicantToAttach.getIdApplicant());
//                attachedApplicantCollection.add(applicantCollectionApplicantToAttach);
//            }
//            country.setApplicantCollection(attachedApplicantCollection);
//            Collection<Inventor> attachedInventorCollection = new ArrayList<Inventor>();
//            for (Inventor inventorCollectionInventorToAttach : country.getInventorCollection()) {
//                inventorCollectionInventorToAttach = em.getReference(inventorCollectionInventorToAttach.getClass(), inventorCollectionInventorToAttach.getIdInventor());
//                attachedInventorCollection.add(inventorCollectionInventorToAttach);
//            }
//            country.setInventorCollection(attachedInventorCollection);
//            em.persist(country);
//            for (Priority priorityCollectionPriority : country.getPriorityCollection()) {
//                Country oldIdCountryOfPriorityCollectionPriority = priorityCollectionPriority.getIdCountry();
//                priorityCollectionPriority.setIdCountry(country);
//                priorityCollectionPriority = em.merge(priorityCollectionPriority);
//                if (oldIdCountryOfPriorityCollectionPriority != null) {
//                    oldIdCountryOfPriorityCollectionPriority.getPriorityCollection().remove(priorityCollectionPriority);
//                    oldIdCountryOfPriorityCollectionPriority = em.merge(oldIdCountryOfPriorityCollectionPriority);
//                }
//            }
//            for (Applicant applicantCollectionApplicant : country.getApplicantCollection()) {
//                Country oldIdCountryOfApplicantCollectionApplicant = applicantCollectionApplicant.getIdCountry();
//                applicantCollectionApplicant.setIdCountry(country);
//                applicantCollectionApplicant = em.merge(applicantCollectionApplicant);
//                if (oldIdCountryOfApplicantCollectionApplicant != null) {
//                    oldIdCountryOfApplicantCollectionApplicant.getApplicantCollection().remove(applicantCollectionApplicant);
//                    oldIdCountryOfApplicantCollectionApplicant = em.merge(oldIdCountryOfApplicantCollectionApplicant);
//                }
//            }
//            for (Inventor inventorCollectionInventor : country.getInventorCollection()) {
//                Country oldIdCountryOfInventorCollectionInventor = inventorCollectionInventor.getIdCountry();
//                inventorCollectionInventor.setIdCountry(country);
//                inventorCollectionInventor = em.merge(inventorCollectionInventor);
//                if (oldIdCountryOfInventorCollectionInventor != null) {
//                    oldIdCountryOfInventorCollectionInventor.getInventorCollection().remove(inventorCollectionInventor);
//                    oldIdCountryOfInventorCollectionInventor = em.merge(oldIdCountryOfInventorCollectionInventor);
//                }
//            }
//            em.getTransaction().commit();
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }

        EntityManager entity = emf.createEntityManager();
        entity.getTransaction().begin();
        entity.persist(country);
        entity.getTransaction().commit();
        entity.close();

    }

    public void edit(Country country) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country persistentCountry = em.find(Country.class, country.getIdCountry());
            Collection<Priority> priorityCollectionOld = persistentCountry.getPriorityCollection();
            Collection<Priority> priorityCollectionNew = country.getPriorityCollection();
            Collection<Applicant> applicantCollectionOld = persistentCountry.getApplicantCollection();
            Collection<Applicant> applicantCollectionNew = country.getApplicantCollection();
            Collection<Inventor> inventorCollectionOld = persistentCountry.getInventorCollection();
            Collection<Inventor> inventorCollectionNew = country.getInventorCollection();
            Collection<Priority> attachedPriorityCollectionNew = new ArrayList<Priority>();
            for (Priority priorityCollectionNewPriorityToAttach : priorityCollectionNew) {
                priorityCollectionNewPriorityToAttach = em.getReference(priorityCollectionNewPriorityToAttach.getClass(), priorityCollectionNewPriorityToAttach.getIdPriority());
                attachedPriorityCollectionNew.add(priorityCollectionNewPriorityToAttach);
            }
            priorityCollectionNew = attachedPriorityCollectionNew;
            country.setPriorityCollection(priorityCollectionNew);
            Collection<Applicant> attachedApplicantCollectionNew = new ArrayList<Applicant>();
            for (Applicant applicantCollectionNewApplicantToAttach : applicantCollectionNew) {
                applicantCollectionNewApplicantToAttach = em.getReference(applicantCollectionNewApplicantToAttach.getClass(), applicantCollectionNewApplicantToAttach.getIdApplicant());
                attachedApplicantCollectionNew.add(applicantCollectionNewApplicantToAttach);
            }
            applicantCollectionNew = attachedApplicantCollectionNew;
            country.setApplicantCollection(applicantCollectionNew);
            Collection<Inventor> attachedInventorCollectionNew = new ArrayList<Inventor>();
            for (Inventor inventorCollectionNewInventorToAttach : inventorCollectionNew) {
                inventorCollectionNewInventorToAttach = em.getReference(inventorCollectionNewInventorToAttach.getClass(), inventorCollectionNewInventorToAttach.getIdInventor());
                attachedInventorCollectionNew.add(inventorCollectionNewInventorToAttach);
            }
            inventorCollectionNew = attachedInventorCollectionNew;
            country.setInventorCollection(inventorCollectionNew);
            country = em.merge(country);
            for (Priority priorityCollectionOldPriority : priorityCollectionOld) {
                if (!priorityCollectionNew.contains(priorityCollectionOldPriority)) {
                    priorityCollectionOldPriority.setIdCountry(null);
                    priorityCollectionOldPriority = em.merge(priorityCollectionOldPriority);
                }
            }
            for (Priority priorityCollectionNewPriority : priorityCollectionNew) {
                if (!priorityCollectionOld.contains(priorityCollectionNewPriority)) {
                    Country oldIdCountryOfPriorityCollectionNewPriority = priorityCollectionNewPriority.getIdCountry();
                    priorityCollectionNewPriority.setIdCountry(country);
                    priorityCollectionNewPriority = em.merge(priorityCollectionNewPriority);
                    if (oldIdCountryOfPriorityCollectionNewPriority != null && !oldIdCountryOfPriorityCollectionNewPriority.equals(country)) {
                        oldIdCountryOfPriorityCollectionNewPriority.getPriorityCollection().remove(priorityCollectionNewPriority);
                        oldIdCountryOfPriorityCollectionNewPriority = em.merge(oldIdCountryOfPriorityCollectionNewPriority);
                    }
                }
            }
            for (Applicant applicantCollectionOldApplicant : applicantCollectionOld) {
                if (!applicantCollectionNew.contains(applicantCollectionOldApplicant)) {
                    applicantCollectionOldApplicant.setIdCountry(null);
                    applicantCollectionOldApplicant = em.merge(applicantCollectionOldApplicant);
                }
            }
            for (Applicant applicantCollectionNewApplicant : applicantCollectionNew) {
                if (!applicantCollectionOld.contains(applicantCollectionNewApplicant)) {
                    Country oldIdCountryOfApplicantCollectionNewApplicant = applicantCollectionNewApplicant.getIdCountry();
                    applicantCollectionNewApplicant.setIdCountry(country);
                    applicantCollectionNewApplicant = em.merge(applicantCollectionNewApplicant);
                    if (oldIdCountryOfApplicantCollectionNewApplicant != null && !oldIdCountryOfApplicantCollectionNewApplicant.equals(country)) {
                        oldIdCountryOfApplicantCollectionNewApplicant.getApplicantCollection().remove(applicantCollectionNewApplicant);
                        oldIdCountryOfApplicantCollectionNewApplicant = em.merge(oldIdCountryOfApplicantCollectionNewApplicant);
                    }
                }
            }
            for (Inventor inventorCollectionOldInventor : inventorCollectionOld) {
                if (!inventorCollectionNew.contains(inventorCollectionOldInventor)) {
                    inventorCollectionOldInventor.setIdCountry(null);
                    inventorCollectionOldInventor = em.merge(inventorCollectionOldInventor);
                }
            }
            for (Inventor inventorCollectionNewInventor : inventorCollectionNew) {
                if (!inventorCollectionOld.contains(inventorCollectionNewInventor)) {
                    Country oldIdCountryOfInventorCollectionNewInventor = inventorCollectionNewInventor.getIdCountry();
                    inventorCollectionNewInventor.setIdCountry(country);
                    inventorCollectionNewInventor = em.merge(inventorCollectionNewInventor);
                    if (oldIdCountryOfInventorCollectionNewInventor != null && !oldIdCountryOfInventorCollectionNewInventor.equals(country)) {
                        oldIdCountryOfInventorCollectionNewInventor.getInventorCollection().remove(inventorCollectionNewInventor);
                        oldIdCountryOfInventorCollectionNewInventor = em.merge(oldIdCountryOfInventorCollectionNewInventor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = country.getIdCountry();
                if (findCountry(id) == null) {
                    throw new NonexistentEntityException("The country with id " + id + " no longer exists.");
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
            Country country;
            try {
                country = em.getReference(Country.class, id);
                country.getIdCountry();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The country with id " + id + " no longer exists.", enfe);
            }
            Collection<Priority> priorityCollection = country.getPriorityCollection();
            for (Priority priorityCollectionPriority : priorityCollection) {
                priorityCollectionPriority.setIdCountry(null);
                priorityCollectionPriority = em.merge(priorityCollectionPriority);
            }
            Collection<Applicant> applicantCollection = country.getApplicantCollection();
            for (Applicant applicantCollectionApplicant : applicantCollection) {
                applicantCollectionApplicant.setIdCountry(null);
                applicantCollectionApplicant = em.merge(applicantCollectionApplicant);
            }
            Collection<Inventor> inventorCollection = country.getInventorCollection();
            for (Inventor inventorCollectionInventor : inventorCollection) {
                inventorCollectionInventor.setIdCountry(null);
                inventorCollectionInventor = em.merge(inventorCollectionInventor);
            }
            em.remove(country);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Country> findCountryEntities() {
        return findCountryEntities(true, -1, -1);
    }

    public List<Country> findCountryEntities(int maxResults, int firstResult) {
        return findCountryEntities(false, maxResults, firstResult);
    }

    private List<Country> findCountryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Country.class));
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

    public Country findCountry(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Country.class, id);
        } finally {
            em.close();
        }
    }

    public Country findCountryByAcronym(String country) {
        EntityManager em = getEntityManager();
//        System.out.println("OI");
        List teste = em.createQuery("SELECT c FROM Country c WHERE c.acronym = :acronym")
                .setParameter("acronym", country).getResultList();
        try {
            if (teste.size() > 0) {
//                System.out.println("tem");
                return (Country) teste.get(0);
            }

            return null;
        } finally {
            em.close();
        }
    }

    public int getCountryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Country> rt = cq.from(Country.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
