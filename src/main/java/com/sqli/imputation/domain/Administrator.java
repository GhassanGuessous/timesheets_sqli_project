package com.sqli.imputation.domain;



import javax.persistence.*;

/**
 * A Administrator.
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends User {

    private static final long serialVersionUID = 1L;

    public Administrator() {
        super();
    }
}
