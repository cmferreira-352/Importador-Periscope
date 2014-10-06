/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufmt.importador.periscope.model;

import com.github.jmkgreen.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Embedded
@Entity
@Table(name = "country")
@NamedQueries({
    @NamedQuery(name = "Country.findAll", query = "SELECT c FROM Country c"),
    @NamedQuery(name = "Country.findByIdCountry", query = "SELECT c FROM Country c WHERE c.idCountry = :idCountry"),
    @NamedQuery(name = "Country.findByAcronym", query = "SELECT c FROM Country c WHERE c.acronym = :acronym"),
    @NamedQuery(name = "Country.findByName", query = "SELECT c FROM Country c WHERE c.name = :name")})
public class Country implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id_country")
    private Integer idCountry;
    @Column(name = "acronym")
    private String acronym;
    @Column(name = "name")
    private String name;
//    @OneToMany(mappedBy = "idCountry")
//    private Collection<Priority> priorityCollection;
//    @OneToMany(mappedBy = "idCountry")
//    private Collection<Applicant> applicantCollection;
//    @OneToMany(mappedBy = "idCountry")
//    private Collection<Inventor> inventorCollection;

    public Country() {
    }

    public Country(Integer idCountry) {
        this.idCountry = idCountry;
    }

    public Integer getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(Integer idCountry) {
        this.idCountry = idCountry;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Collection<Priority> getPriorityCollection() {
//        return priorityCollection;
//    }
//
//    public void setPriorityCollection(Collection<Priority> priorityCollection) {
//        this.priorityCollection = priorityCollection;
//    }
//
//    public Collection<Applicant> getApplicantCollection() {
//        return applicantCollection;
//    }
//
//    public void setApplicantCollection(Collection<Applicant> applicantCollection) {
//        this.applicantCollection = applicantCollection;
//    }
//
//    public Collection<Inventor> getInventorCollection() {
//        return inventorCollection;
//    }
//
//    public void setInventorCollection(Collection<Inventor> inventorCollection) {
//        this.inventorCollection = inventorCollection;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCountry != null ? idCountry.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Country)) {
            return false;
        }
        Country other = (Country) object;
        if ((this.idCountry == null && other.idCountry != null) || (this.idCountry != null && !this.idCountry.equals(other.idCountry))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
