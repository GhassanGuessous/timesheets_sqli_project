package com.sqli.imputation.domain;



import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Correspondence.
 */
@Entity
@Table(name = "correspondence")
public class Correspondence implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_app")
    private String idAPP;

    @Column(name = "id_ppmc")
    private String idPPMC;

    @Column(name = "id_tbp")
    private String idTBP;

    @ManyToOne
    private Collaborator collaborator;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdAPP() {
        return idAPP;
    }

    public Correspondence idAPP(String idAPP) {
        this.idAPP = idAPP;
        return this;
    }

    public void setIdAPP(String idAPP) {
        this.idAPP = idAPP;
    }

    public String getIdPPMC() {
        return idPPMC;
    }

    public Correspondence idPPMC(String idPPMC) {
        this.idPPMC = idPPMC;
        return this;
    }

    public void setIdPPMC(String idPPMC) {
        this.idPPMC = idPPMC;
    }

    public String getIdTBP() {
        return idTBP;
    }

    public Correspondence idTBP(String idTBP) {
        this.idTBP = idTBP;
        return this;
    }

    public void setIdTBP(String idTBP) {
        this.idTBP = idTBP;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public Correspondence collaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
        return this;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
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
        Correspondence correspondence = (Correspondence) o;
        if (correspondence.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), correspondence.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Correspondence{" +
            "id=" + getId() +
            ", idAPP='" + getIdAPP() + "'" +
            ", idPPMC='" + getIdPPMC() + "'" +
            ", idTBP='" + getIdTBP() + "'" +
            "}";
    }
}
