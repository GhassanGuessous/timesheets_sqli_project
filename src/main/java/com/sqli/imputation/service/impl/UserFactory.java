package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Administrator;
import com.sqli.imputation.domain.DeliveryCoordinator;
import com.sqli.imputation.domain.User;
import com.sqli.imputation.security.AuthoritiesConstants;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserFactory {

    public static final int ONE_AUTHORITY = 1;

    public User instantiateUser(Set<String> authorities) {
        if(authorities == null){
            return new User();
        }else if(hasOneElement(authorities)){
            String authority = authorities.stream().findFirst().get();
            if(authority.equals(AuthoritiesConstants.DELCO))
                return new DeliveryCoordinator();
            else
                return new Administrator();
        }else {
            return new DeliveryCoordinator();
        }
    }

    private boolean hasOneElement(Set<String> authorities){
        return (authorities.size() == ONE_AUTHORITY) ? true : false;
    }
}
