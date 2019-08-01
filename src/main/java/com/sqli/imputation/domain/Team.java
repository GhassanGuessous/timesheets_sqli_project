package com.sqli.imputation.domain;

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

    @Column(name = "displayName")
    private String displayName;

    @Column(name = "agresso")
    private String agresso;

    @Column(name = "id_tbp")
    private String idTbp;

    @OneToOne
    @JoinColumn(unique = true)
    private DeliveryCoordinator deliveryCoordinator;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team")
    private Set<Collaborator> collaborators = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "team", cascade = CascadeType.REMOVE)
    private Set<AppTbpIdentifier> appTbpIdentifiers = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("teams")
    private ProjectType projectType;

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

    public String getDisplayName() {
        return displayName;
    }

    public Team displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getAgresso() {
        return agresso;
    }

    public void setAgresso(String agresso) {
        this.agresso = agresso;
    }

    public String getIdTbp() {
        return idTbp;
    }

    public void setIdTbp(String idTbp) {
        this.idTbp = idTbp;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public Set<AppTbpIdentifier> getAppTbpIdentifiers() {
        return appTbpIdentifiers;
    }

    public void setAppTbpIdentifiers(Set<AppTbpIdentifier> appTbpIdentifiers) {
        this.appTbpIdentifiers = appTbpIdentifiers;
    }

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
            "id=" + id +
            ", name='" + name + '\'' +
            ", displayName='" + displayName + '\'' +
            ", agresso='" + agresso + '\'' +
            ", idTbp='" + idTbp + '\'' +
            ", deliveryCoordinator=" + deliveryCoordinator +
            ", collaborators=" + collaborators +
            ", projectType=" + projectType +
            '}';
    }
}
