package com.sqli.imputation.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A ImputationType.
 */
@Entity
@Table(name = "imputation_type")
public class ImputationType implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "imputationType")
    private Set<Imputation> imputations = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ImputationType name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Imputation> getImputations() {
        return imputations;
    }

    public ImputationType imputations(Set<Imputation> imputations) {
        this.imputations = imputations;
        return this;
    }

    public ImputationType addImputation(Imputation imputation) {
        this.imputations.add(imputation);
        imputation.setImputationType(this);
        return this;
    }

    public ImputationType removeImputation(Imputation imputation) {
        this.imputations.remove(imputation);
        imputation.setImputationType(null);
        return this;
    }

    public void setImputations(Set<Imputation> imputations) {
        this.imputations = imputations;
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
        ImputationType imputationType = (ImputationType) o;
        if (imputationType.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), imputationType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ImputationType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
