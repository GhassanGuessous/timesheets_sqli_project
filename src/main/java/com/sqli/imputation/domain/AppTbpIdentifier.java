package com.sqli.imputation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "app_tbp_identifier")
public class AppTbpIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_tbp")
    private String idTbp;

    @Column(name = "agresso")
    private String agresso;

    @Column(name = "mission")
    private String mission;

    @ManyToOne
    @JsonIgnore
    private Team team;

    public AppTbpIdentifier() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdTbp() {
        return idTbp;
    }

    public void setIdTbp(String idTbp) {
        this.idTbp = idTbp;
    }

    public String getAgresso() {
        return agresso;
    }

    public void setAgresso(String agresso) {
        this.agresso = agresso;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppTbpIdentifier appTbpIdentifier = (AppTbpIdentifier) o;
        if (appTbpIdentifier.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), appTbpIdentifier.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AppTbpIdentifier{" +
            "id=" + id +
            ", idTbp='" + idTbp + '\'' +
            ", agresso='" + agresso + '\'' +
            ", mission='" + mission + '\'' +
            ", team=" + team +
            '}';
    }
}


