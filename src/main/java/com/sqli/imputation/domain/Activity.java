package com.sqli.imputation.domain;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "activity")
    private Set<Collaborator> collaborators = new HashSet<>();
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

    public Activity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Collaborator> getCollaborators() {
        return collaborators;
    }

    public Activity collaborators(Set<Collaborator> collaborators) {
        this.collaborators = collaborators;
        return this;
    }

    public Activity addCollaborator(Collaborator collaborator) {
        this.collaborators.add(collaborator);
        collaborator.setActivity(this);
        return this;
    }

    public Activity removeCollaborator(Collaborator collaborator) {
        this.collaborators.remove(collaborator);
        collaborator.setActivity(null);
        return this;
    }

    public void setCollaborators(Set<Collaborator> collaborators) {
        this.collaborators = collaborators;
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
        Activity activity = (Activity) o;
        if (activity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
