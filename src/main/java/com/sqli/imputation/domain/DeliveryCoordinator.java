package com.sqli.imputation.domain;



import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DeliveryCoordinator.
 */
@Entity
@DiscriminatorValue("DELCO")
public class DeliveryCoordinator extends User {

    private static final long serialVersionUID = 1L;

    public DeliveryCoordinator() {
        super();
    }
}
