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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Embedded
@Entity
@Table(name = "classification")
@NamedQueries({
    @NamedQuery(name = "Classification.findAll", query = "SELECT c FROM Classification c"),
    @NamedQuery(name = "Classification.findByIdClassification", query = "SELECT c FROM Classification c WHERE c.idClassification = :idClassification"),
    @NamedQuery(name = "Classification.findByValue", query = "SELECT c FROM Classification c WHERE c.value = :value"),
    @NamedQuery(name = "Classification.findByKlass", query = "SELECT c FROM Classification c WHERE c.klass = :klass"),
    @NamedQuery(name = "Classification.findByGroup", query = "SELECT c FROM Classification c WHERE c.group = :group"),
    @NamedQuery(name = "Classification.findBySubgroup", query = "SELECT c FROM Classification c WHERE c.subgroup = :subgroup"),
    @NamedQuery(name = "Classification.findByType", query = "SELECT c FROM Classification c WHERE c.type = :type")})
public class Classification implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id_classification")
    private Integer idClassification;
    @Column(name = "value")
    private String value;
    @Column(name = "klass")
    private String klass;
    @Column(name = "grupo")
    private Short group;
    @Column(name = "subgroup")
    private Short subgroup;
    @Column(name = "type")
    private String type;
    @JoinTable(name = "patent_classification", joinColumns = {
        @JoinColumn(name = "id_classification", referencedColumnName = "id_classification")}, inverseJoinColumns = {
        @JoinColumn(name = "id_patent", referencedColumnName = "id_patent")})
    @ManyToMany
    private Collection<Patent> patentCollection = new ArrayList<Patent>();
    @OneToMany(mappedBy = "idMainClassification")
    private Collection<Patent> patentCollection1;

    public Classification() {
    }

    public Classification(Integer idClassification) {
        this.idClassification = idClassification;
    }
    
    private void updateClassGroupSubGroup(String val) {
        String vet[] = null;
        if (val != null) {
            vet = val.trim().split("/");
            if (vet.length > 0) {
                if (vet[0].length() > 4) {
                    klass = vet[0].substring(0, 4);
                    group = Short.parseShort(vet[0].substring(4).trim());
                } else {
                    klass = vet[0];
                    group = 0;
                }
                if (vet.length > 1) {
                    subgroup = Short.parseShort(vet[1].trim());
                } else {
                    subgroup = 0;
                }
            } else {
                klass = "";
                group = 0;
                subgroup = 0;
            }
        }
    }

    public Integer getIdClassification() {
        return idClassification;
    }

    public void setIdClassification(Integer idClassification) {
        this.idClassification = idClassification;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        updateClassGroupSubGroup(value);
        this.value = value;
    }

    public String getKlass() {
        return klass;
    }

    public void setKlass(String klass) {
        this.klass = klass;
    }

    public Short getGroup() {
        return group;
    }

    public void setGroup(Short group) {
        this.group = group;
    }

    public Short getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(Short subgroup) {
        this.subgroup = subgroup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<Patent> getPatentCollection() {
        return patentCollection;
    }

    public void setPatentCollection(Collection<Patent> patentCollection) {
        this.patentCollection = patentCollection;
    }

    public Collection<Patent> getPatentCollection1() {
        return patentCollection1;
    }

    public void setPatentCollection1(Collection<Patent> patentCollection1) {
        this.patentCollection1 = patentCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idClassification != null ? idClassification.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Classification)) {
            return false;
        }
        Classification other = (Classification) object;
        if ((this.idClassification == null && other.idClassification != null) || (this.idClassification != null && !this.idClassification.equals(other.idClassification))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return value;
    }

}
