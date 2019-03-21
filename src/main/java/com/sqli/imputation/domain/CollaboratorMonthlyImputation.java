package com.sqli.imputation.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A CollaboratorMonthlyImputation.
 */
@Entity
@Table(name = "monthly_imputation")
public class CollaboratorMonthlyImputation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total")
    private Integer total;

    @ManyToOne
    @JsonIgnoreProperties("monthlyImputations")
    private Collaborator collaborator;

    @ManyToOne
    @JsonIgnoreProperties("monthlyImputations")
    private Imputation imputation;

    @OneToMany(mappedBy = "collaboratorMonthlyImputation")
    private Set<CollaboratorDailyImputation> dailyImputations = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotal() {
        return total;
    }

    public CollaboratorMonthlyImputation total(Integer total) {
        this.total = total;
        return this;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public CollaboratorMonthlyImputation collaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
        return this;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public Imputation getImputation() {
        return imputation;
    }

    public CollaboratorMonthlyImputation imputation(Imputation imputation) {
        this.imputation = imputation;
        return this;
    }

    public void setImputation(Imputation imputation) {
        this.imputation = imputation;
    }

    public Set<CollaboratorDailyImputation> getDailyImputations() {
        return dailyImputations;
    }

    public CollaboratorMonthlyImputation dailyImputations(Set<CollaboratorDailyImputation> collaboratorDailyImputations) {
        this.dailyImputations = collaboratorDailyImputations;
        return this;
    }

    public CollaboratorMonthlyImputation addDailyImputation(CollaboratorDailyImputation collaboratorDailyImputation) {
        this.dailyImputations.add(collaboratorDailyImputation);
        collaboratorDailyImputation.setCollaboratorMonthlyImputation(this);
        return this;
    }

    public CollaboratorMonthlyImputation removeDailyImputation(CollaboratorDailyImputation collaboratorDailyImputation) {
        this.dailyImputations.remove(collaboratorDailyImputation);
        collaboratorDailyImputation.setCollaboratorMonthlyImputation(null);
        return this;
    }

    public void setDailyImputations(Set<CollaboratorDailyImputation> collaboratorDailyImputations) {
        this.dailyImputations = collaboratorDailyImputations;
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
        CollaboratorMonthlyImputation collaboratorMonthlyImputation = (CollaboratorMonthlyImputation) o;
        if (collaboratorMonthlyImputation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), collaboratorMonthlyImputation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CollaboratorMonthlyImputation{" +
            "id=" + getId() +
            ", total=" + getTotal() +
            "}";
    }
}
