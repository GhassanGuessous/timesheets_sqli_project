package com.sqli.imputation.service.factory;

import com.sqli.imputation.domain.*;
import org.springframework.stereotype.Component;

@Component
public class ImputationFactory {

    public static final Double INITIAL_VALUE = 0D;

    public Imputation createImputation(int year, int month, ImputationType imputationType){
        Imputation imputation = new Imputation();
        imputation.setYear(year);
        imputation.setMonth(month);
        imputation.setImputationType(imputationType);
        return imputation;
    }

    public CollaboratorMonthlyImputation createMonthlyImputation(Imputation imputation, Collaborator collaborator){
        CollaboratorMonthlyImputation monthlyImputation = new CollaboratorMonthlyImputation();
        monthlyImputation.setImputation(imputation);
        monthlyImputation.setCollaborator(collaborator);
        monthlyImputation.setTotal(INITIAL_VALUE);
        return monthlyImputation;
    }

    public CollaboratorDailyImputation createDailyImputation(int day, String charge, CollaboratorMonthlyImputation monthlyImputation) {
        CollaboratorDailyImputation dailyImputation = new CollaboratorDailyImputation();
        dailyImputation.setDay(day);
        dailyImputation.setCharge(Double.parseDouble(charge));
        dailyImputation.setCollaboratorMonthlyImputation(monthlyImputation);
        return dailyImputation;
    }
}
