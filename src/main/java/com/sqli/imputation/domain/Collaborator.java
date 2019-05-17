package com.sqli.imputation.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Collaborator.
 */
@Entity
@Table(name = "collaborator")
public class Collaborator implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Email
    @Size(min = 5, max = 254)
    @Column(name = "email", length = 254, unique = true)
    private String email;

    @ManyToOne
    @JsonIgnoreProperties("collaborators")
    private Team team;

    @OneToMany(mappedBy = "collaborator")
    private Set<CollaboratorMonthlyImputation> monthlyImputations = new HashSet<>();
    @ManyToOne
    @JsonIgnoreProperties("collaborators")
    private Activity activity;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public Collaborator firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Collaborator lastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public Collaborator email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Team getTeam() {
        return team;
    }

    public Collaborator team(Team team) {
        this.team = team;
        return this;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<CollaboratorMonthlyImputation> getMonthlyImputations() {
        return monthlyImputations;
    }

    public Collaborator monthlyImputations(Set<CollaboratorMonthlyImputation> collaboratorMonthlyImputations) {
        this.monthlyImputations = collaboratorMonthlyImputations;
        return this;
    }

    public Collaborator addMonthlyImputation(CollaboratorMonthlyImputation collaboratorMonthlyImputation) {
        this.monthlyImputations.add(collaboratorMonthlyImputation);
        collaboratorMonthlyImputation.setCollaborator(this);
        return this;
    }

    public Collaborator removeMonthlyImputation(CollaboratorMonthlyImputation collaboratorMonthlyImputation) {
        this.monthlyImputations.remove(collaboratorMonthlyImputation);
        collaboratorMonthlyImputation.setCollaborator(null);
        return this;
    }

    public void setMonthlyImputations(Set<CollaboratorMonthlyImputation> collaboratorMonthlyImputations) {
        this.monthlyImputations = collaboratorMonthlyImputations;
    }

    public Activity getActivity() {
        return activity;
    }

    public Collaborator activity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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
        Collaborator collaborator = (Collaborator) o;
        if (collaborator.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), collaborator.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Collaborator{" +
            "id=" + getId() +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
