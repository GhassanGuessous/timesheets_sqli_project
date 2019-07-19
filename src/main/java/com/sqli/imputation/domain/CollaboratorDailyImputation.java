package com.sqli.imputation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A CollaboratorDailyImputation.
 */
@Entity
@Table(name = "daily_imputation")
public class CollaboratorDailyImputation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day")
    private Integer day;

    @Column(name = "charge")
    private Double charge;

    @ManyToOne
    @JsonIgnoreProperties("dailyImputations")
    private CollaboratorMonthlyImputation collaboratorMonthlyImputation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDay() {
        return day;
    }

    public CollaboratorDailyImputation day(Integer day) {
        this.day = day;
        return this;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Double getCharge() {
        return charge;
    }

    public CollaboratorDailyImputation charge(Double charge) {
        this.charge = charge;
        return this;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    public CollaboratorMonthlyImputation getCollaboratorMonthlyImputation() {
        return collaboratorMonthlyImputation;
    }

    public CollaboratorDailyImputation collaboratorMonthlyImputation(CollaboratorMonthlyImputation collaboratorMonthlyImputation) {
        this.collaboratorMonthlyImputation = collaboratorMonthlyImputation;
        return this;
    }

    public void setCollaboratorMonthlyImputation(CollaboratorMonthlyImputation collaboratorMonthlyImputation) {
        this.collaboratorMonthlyImputation = collaboratorMonthlyImputation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollaboratorDailyImputation collaboratorDailyImputation = (CollaboratorDailyImputation) o;
        if (collaboratorDailyImputation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), collaboratorDailyImputation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CollaboratorDailyImputation{" +
            "id=" + getId() +
            ", day=" + getDay() +
            ", charge=" + getCharge() +
            "}";
    }
}
