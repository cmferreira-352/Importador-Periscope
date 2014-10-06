/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufmt.importador.periscope.model;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.bson.types.ObjectId;


@Entity
//@com.github.jmkgreen.morphia.annotations.Entity
@Table(name = "patent")
@NamedQueries({
    @NamedQuery(name = "Patent.findAll", query = "SELECT p FROM Patent p"),
    @NamedQuery(name = "Patent.findByIdPatent", query = "SELECT p FROM Patent p WHERE p.idPatent = :idPatent"),
    @NamedQuery(name = "Patent.findByTitleSelect", query = "SELECT p FROM Patent p WHERE p.titleSelect = :titleSelect"),
    @NamedQuery(name = "Patent.findByLanguage", query = "SELECT p FROM Patent p WHERE p.language = :language"),
    @NamedQuery(name = "Patent.findByPublicationNumber", query = "SELECT p FROM Patent p WHERE p.publicationNumber = :publicationNumber"),
    @NamedQuery(name = "Patent.findByPublicationDate", query = "SELECT p FROM Patent p WHERE p.publicationDate = :publicationDate"),
    @NamedQuery(name = "Patent.findByApplicationNumber", query = "SELECT p FROM Patent p WHERE p.applicationNumber = :applicationNumber"),
    @NamedQuery(name = "Patent.findByBlacklisted", query = "SELECT p FROM Patent p WHERE p.blacklisted = :blacklisted"),
    @NamedQuery(name = "Patent.findByCompleted", query = "SELECT p FROM Patent p WHERE p.completed = :completed"),
    @NamedQuery(name = "Patent.findByShared", query = "SELECT p FROM Patent p WHERE p.shared = :shared")})
public class Patent implements Serializable {
    private static final long serialVersionUID = 1L;
    @com.github.jmkgreen.morphia.annotations.Id
    @javax.persistence.Transient
    private ObjectId id;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id_patent")
    private Integer idPatent;
    @Column(name = "title_select")
    private String titleSelect;
    @Column(name = "language")
    private String language;
    @Column(name = "publication_number")
    private String publicationNumber;
    @Column(name = "publication_date")
    @Temporal(TemporalType.DATE)
    private Date publicationDate;
    @Column(name = "application_date")
    @Temporal(TemporalType.DATE)
    private Date applicantionDate;
    @Column(name = "application_number")
    private String applicationNumber;
    @Column(name = "blacklisted")
    private Boolean blacklisted;
    @Column(name = "completed")
    private Boolean completed;
    @Column(name = "shared")
    private Boolean shared;
    @Embedded
    @ManyToMany(mappedBy = "patentCollection")
    private Collection<Applicant> applicantCollection;
    @Embedded
    @ManyToMany(mappedBy = "patentCollection")
    private Collection<Inventor> inventorCollection;
    @Embedded
    @ManyToMany(mappedBy = "patentCollection")
    private Collection<Classification> classificationCollection;
    @Embedded
    @OneToMany(mappedBy = "idPatent",cascade = CascadeType.ALL)
    private Collection<Priority> priorityCollection;
    @Transient
    @JoinColumn(name = "id_project", referencedColumnName = "id_project")
    @ManyToOne
    private Project idProject;
    @Embedded
    @JoinColumn(name = "id_main_classification", referencedColumnName = "id_classification")
    @ManyToOne
    private Classification idMainClassification;

    public Patent() {
        this.applicantCollection = new ArrayList<Applicant>();
        this.classificationCollection = new ArrayList<Classification>();
        this.inventorCollection = new ArrayList<Inventor>();
        this.priorityCollection = new ArrayList<Priority>();
    }

    public Patent(Integer idPatent) {
        this.idPatent = idPatent;
    }

    public Integer getIdPatent() {
        return idPatent;
    }

    public void setIdPatent(Integer idPatent) {
        this.idPatent = idPatent;
    }

    public String getTitleSelect() {
        return titleSelect;
    }

    public void setTitleSelect(String titleSelect) {
        this.titleSelect = titleSelect;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublicationNumber() {
        return publicationNumber;
    }

    public void setPublicationNumber(String publicationNumber) {
        this.publicationNumber = publicationNumber;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }
    
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Date getApplicantionDate() {
        return applicantionDate;
    }

    public void setApplicantionDate(Date applicantiondate) {
        this.applicantionDate = applicantiondate;
    }
    
    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public Boolean getBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(Boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Collection<Applicant> getApplicantCollection() {
        return applicantCollection;
    }

    public void setApplicantCollection(Collection<Applicant> applicantCollection) {
        this.applicantCollection = applicantCollection;
    }

    public Collection<Inventor> getInventorCollection() {
        return inventorCollection;
    }

    public void setInventorCollection(Collection<Inventor> inventorCollection) {
        this.inventorCollection = inventorCollection;
    }

    public Collection<Classification> getClassificationCollection() {
        return classificationCollection;
    }

    public void setClassificationCollection(Collection<Classification> classificationCollection) {
        this.classificationCollection = classificationCollection;
    }

    public Collection<Priority> getPriorityCollection() {
        return priorityCollection;
    }

    public void setPriorityCollection(Collection<Priority> priorityCollection) {
        this.priorityCollection = priorityCollection;
    }

    public Project getIdProject() {
        return idProject;
    }

    public void setIdProject(Project idProject) {
        this.idProject = idProject;
    }

    public Classification getIdMainClassification() {
        return idMainClassification;
    }

    public void setIdMainClassification(Classification idMainClassification) {
        this.idMainClassification = idMainClassification;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
//    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPatent != null ? idPatent.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Patent)) {
            return false;
        }
        Patent other = (Patent) object;
        if ((this.idPatent == null && other.idPatent != null) || (this.idPatent != null && !this.idPatent.equals(other.idPatent))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return titleSelect;
    }

}
