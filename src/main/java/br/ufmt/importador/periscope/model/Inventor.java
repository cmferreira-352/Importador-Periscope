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
@Table(name = "inventor")
@NamedQueries({
    @NamedQuery(name = "Inventor.findAll", query = "SELECT i FROM Inventor i"),
    @NamedQuery(name = "Inventor.findByIdInventor", query = "SELECT i FROM Inventor i WHERE i.idInventor = :idInventor"),
    @NamedQuery(name = "Inventor.findByNome", query = "SELECT i FROM Inventor i WHERE i.nome = :nome")})
public class Inventor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id_inventor")
    private Integer idInventor;
    @Column(name = "nome")
    private String nome;
    @JoinTable(name = "patent_inventor", joinColumns = {
        @JoinColumn(name = "id_inventor", referencedColumnName = "id_inventor")}, inverseJoinColumns = {
        @JoinColumn(name = "id_patent", referencedColumnName = "id_patent")})
    @ManyToMany
    private Collection<Patent> patentCollection = new ArrayList<Patent>();
    @JoinColumn(name = "id_country", referencedColumnName = "id_country")
    @ManyToOne
    private Country idCountry;

    public Inventor() {
    }

    public Inventor(Integer idInventor) {
        this.idInventor = idInventor;
    }

    public Integer getIdInventor() {
        return idInventor;
    }

    public void setIdInventor(Integer idInventor) {
        this.idInventor = idInventor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
        hash += (idInventor != null ? idInventor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Inventor)) {
            return false;
        }
        Inventor other = (Inventor) object;
        if ((this.idInventor == null && other.idInventor != null) || (this.idInventor != null && !this.idInventor.equals(other.idInventor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }

}
