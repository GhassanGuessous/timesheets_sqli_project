package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Administrator;
import com.sqli.imputation.domain.DeliveryCoordinator;
import com.sqli.imputation.domain.User;
import com.sqli.imputation.security.AuthoritiesConstants;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserFactory {

    public User instantiateUser(Set<String> authorities) {
        if(authorities == null){
            return new User();
        }else if(authorities.contains(AuthoritiesConstants.DELCO)){
            return new DeliveryCoordinator();
        }else {
            return new Administrator();
        }
    }
}
