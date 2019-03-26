package com.sqli.imputation.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Team.
 */
@Entity
@Table(name = "team")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "agresso")
    private String agresso;

    @OneToOne
    @JoinColumn(unique = true)
    private DeliveryCoordinator deliveryCoordinator;

    @OneToMany(mappedBy = "team")
    private Set<Collaborator> collaborators = new HashSet<>();
    @ManyToMany(mappedBy = "teams")
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("teams")
    private ProjectType projectType;

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

    public Team name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeliveryCoordinator getDeliveryCoordinator() {
        return deliveryCoordinator;
    }

    public Team deliveryCoordinator(DeliveryCoordinator deliveryCoordinator) {
        this.deliveryCoordinator = deliveryCoordinator;
        return this;
    }

    public void setDeliveryCoordinator(DeliveryCoordinator deliveryCoordinator) {
        this.deliveryCoordinator = deliveryCoordinator;
    }

    public Set<Collaborator> getCollaborators() {
        return collaborators;
    }

    public Team collaborators(Set<Collaborator> collaborators) {
        this.collaborators = collaborators;
        return this;
    }

    public Team addCollaborator(Collaborator collaborator) {
        this.collaborators.add(collaborator);
        collaborator.setTeam(this);
        return this;
    }

    public Team removeCollaborator(Collaborator collaborator) {
        this.collaborators.remove(collaborator);
        collaborator.setTeam(null);
        return this;
    }

    public void setCollaborators(Set<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public String getAgresso() {
        return agresso;
    }

    public void setAgresso(String agresso) {
        this.agresso = agresso;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public Team projects(Set<Project> projects) {
        this.projects = projects;
        return this;
    }

    public Team addProject(Project project) {
        this.projects.add(project);
        project.getTeams().add(this);
        return this;
    }

    public Team removeProject(Project project) {
        this.projects.remove(project);
        project.getTeams().remove(this);
        return this;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
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
        Team team = (Team) o;
        if (team.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), team.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Team{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
