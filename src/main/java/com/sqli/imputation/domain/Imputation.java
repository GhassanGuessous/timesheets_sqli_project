package com.sqli.imputation.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Imputation.
 */
@Entity
@Table(name = "imputation")
public class Imputation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month")
    private Integer month;

    @Column(name = "jhi_year")
    private Integer year;

    @OneToMany(mappedBy = "imputation")
    private Set<CollaboratorMonthlyImputation> monthlyImputations = new HashSet<>();
    @ManyToOne
    @JsonIgnoreProperties("imputations")
    private ImputationType imputationType;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMonth() {
        return month;
    }

    public Imputation month(Integer month) {
        this.month = month;
        return this;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public Imputation year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Set<CollaboratorMonthlyImputation> getMonthlyImputations() {
        return monthlyImputations;
    }

    public Imputation monthlyImputations(Set<CollaboratorMonthlyImputation> collaboratorMonthlyImputations) {
        this.monthlyImputations = collaboratorMonthlyImputations;
        return this;
    }

    public Imputation addMonthlyImputation(CollaboratorMonthlyImputation collaboratorMonthlyImputation) {
        this.monthlyImputations.add(collaboratorMonthlyImputation);
        collaboratorMonthlyImputation.setImputation(this);
        return this;
    }

    public Imputation removeMonthlyImputation(CollaboratorMonthlyImputation collaboratorMonthlyImputation) {
        this.monthlyImputations.remove(collaboratorMonthlyImputation);
        collaboratorMonthlyImputation.setImputation(null);
        return this;
    }

    public void setMonthlyImputations(Set<CollaboratorMonthlyImputation> collaboratorMonthlyImputations) {
        this.monthlyImputations = collaboratorMonthlyImputations;
    }

    public ImputationType getImputationType() {
        return imputationType;
    }

    public Imputation imputationType(ImputationType imputationType) {
        this.imputationType = imputationType;
        return this;
    }

    public void setImputationType(ImputationType imputationType) {
        this.imputationType = imputationType;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Imputation imputation = (Imputation) o;
        if (imputation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), imputation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Imputation{" +
            "id=" + getId() +
            ", month=" + getMonth() +
            ", year=" + getYear() +
            "}";
    }
}
