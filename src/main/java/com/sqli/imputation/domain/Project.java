package com.sqli.imputation.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agresso")
    private String agresso;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(name = "project_team",
               joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id"))
    private Set<Team> teams = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("projects")
    private ProjectType projectType;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgresso() {
        return agresso;
    }

    public Project agresso(String agresso) {
        this.agresso = agresso;
        return this;
    }

    public void setAgresso(String agresso) {
        this.agresso = agresso;
    }

    public String getName() {
        return name;
    }

    public Project name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public Project teams(Set<Team> teams) {
        this.teams = teams;
        return this;
    }

    public Project addTeam(Team team) {
        this.teams.add(team);
        team.getProjects().add(this);
        return this;
    }

    public Project removeTeam(Team team) {
        this.teams.remove(team);
        team.getProjects().remove(this);
        return this;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public Project projectType(ProjectType projectType) {
        this.projectType = projectType;
        return this;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
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
        Project project = (Project) o;
        if (project.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), project.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", agresso='" + getAgresso() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
