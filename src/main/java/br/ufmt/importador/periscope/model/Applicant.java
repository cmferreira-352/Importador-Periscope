/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufmt.importador.periscope.model;

import com.github.jmkgreen.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Embedded
@Entity
@Table(name = "applicant")
@NamedQueries({
    @NamedQuery(name = "Applicant.findAll", query = "SELECT a FROM Applicant a"),
    @NamedQuery(name = "Applicant.findByIdApplicant", query = "SELECT a FROM Applicant a WHERE a.idApplicant = :idApplicant"),
    @NamedQuery(name = "Applicant.findByName", query = "SELECT a FROM Applicant a WHERE a.name = :name")})
public class Applicant implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id_applicant")
    private Integer idApplicant;
    @Column(name = "name", unique = true)
    private String name;
    @JoinTable(name = "patent_applicant", joinColumns = {
        @JoinColumn(name = "id_applicant", referencedColumnName = "id_applicant")}, inverseJoinColumns = {
        @JoinColumn(name = "id_patent", referencedColumnName = "id_patent")})
    @ManyToMany
    private Collection<Patent> patentCollection = new ArrayList<Patent>();
    @JoinColumn(name = "id_country", referencedColumnName = "id_country")
    @ManyToOne
    private Country idCountry;

    public Applicant() {
    }

    public Applicant(Integer idApplicant) {
        this.idApplicant = idApplicant;
    }

    public Integer getIdApplicant() {
        return idApplicant;
    }

    public void setIdApplicant(Integer idApplicant) {
        this.idApplicant = idApplicant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Patent> getPatentCollection() {
        return patentCollection;
    }

    public void setPatentCollection(Collection<Patent> patentCollection) {
        this.patentCollection = patentCollection;
    }

    public Country getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(Country idCountry) {
        this.idCountry = idCountry;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idApplicant != null ? idApplicant.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Applicant)) {
            return false;
        }
        Applicant other = (Applicant) object;
        if ((this.idApplicant == null && other.idApplicant != null) || (this.idApplicant != null && !this.idApplicant.equals(other.idApplicant))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
