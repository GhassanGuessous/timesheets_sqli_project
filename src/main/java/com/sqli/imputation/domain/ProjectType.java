package com.sqli.imputation.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A ProjectType.
 */
@Entity
@Table(name = "project_type")
public class ProjectType implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "projectType")
    private Set<Project> projects = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @OneToMany(mappedBy = "projectType")
    private Set<Team> teams = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ProjectType name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public ProjectType projects(Set<Project> projects) {
        this.projects = projects;
        return this;
    }

    public ProjectType addProject(Project project) {
        this.projects.add(project);
        project.setProjectType(this);
        return this;
    }

    public ProjectType removeProject(Project project) {
        this.projects.remove(project);
        project.setProjectType(null);
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
        ProjectType projectType = (ProjectType) o;
        if (projectType.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), projectType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProjectType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
