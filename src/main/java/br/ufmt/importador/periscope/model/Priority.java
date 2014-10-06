/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufmt.importador.periscope.model;

import com.github.jmkgreen.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embedded
@Entity
@Table(name = "priority")
@NamedQueries({
    @NamedQuery(name = "Priority.findAll", query = "SELECT p FROM Priority p"),
    @NamedQuery(name = "Priority.findByIdPriority", query = "SELECT p FROM Priority p WHERE p.idPriority = :idPriority"),
    @NamedQuery(name = "Priority.findByValue", query = "SELECT p FROM Priority p WHERE p.value = :value"),
    @NamedQuery(name = "Priority.findByDate", query = "SELECT p FROM Priority p WHERE p.date = :date")})
public class Priority implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id_priority")
    private Integer idPriority;
    @Column(name = "value")
    private String value;
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    
    @JoinColumn(name = "id_patent", referencedColumnName = "id_patent")
    @ManyToOne
    private Patent idPatent = new Patent();
    @JoinColumn(name = "id_country", referencedColumnName = "id_country")
    @ManyToOne
    private Country idCountry;

    public Priority() {
    }

    public Priority(Integer idPriority) {
        this.idPriority = idPriority;
    }

    public Integer getIdPriority() {
        return idPriority;
    }

    public void setIdPriority(Integer idPriority) {
        this.idPriority = idPriority;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Patent getIdPatent() {
        return idPatent;
    }

    public void setIdPatent(Patent idPatent) {
        this.idPatent = idPatent;
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
        hash += (idPriority != null ? idPriority.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Priority)) {
            return false;
        }
        Priority other = (Priority) object;
        if ((this.idPriority == null && other.idPriority != null) || (this.idPriority != null && !this.idPriority.equals(other.idPriority))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return value;
    }

}
