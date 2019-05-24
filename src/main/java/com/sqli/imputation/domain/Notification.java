package com.sqli.imputation.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Collaborator collaborator;

    @Column(name = "month")
    private int month;

    @Column(name = "year")
    private int year;

    @Column(name = "type")
    private String type;

    @Column(name = "sending_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendingDate;

    public Notification() {
    }

    public Notification(Collaborator collaborator, int month, int year, Date sendingDate) {
        this.collaborator = collaborator;
        this.month = month;
        this.year = year;
        this.sendingDate = sendingDate;
    }

    public long getId() {
        return id;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Notification{" +
            "id=" + id +
            ", collaborator=" + collaborator +
            ", month=" + month +
            ", year=" + year +
            ", type='" + type + '\'' +
            ", sendingDate=" + sendingDate +
            '}';
    }
}
