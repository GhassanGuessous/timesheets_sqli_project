package com.sqli.imputation.service.factory;

import com.sqli.imputation.domain.*;
import org.springframework.stereotype.Component;

@Component
public class ImputationFactory {


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
      monthlyImputation.setTotal(0);
      return monthlyImputation;
  }

  public CollaboratorDailyImputation createDailyImputation(CollaboratorMonthlyImputation monthlyImputation, int day, int charge){
      CollaboratorDailyImputation dailyImputation = new CollaboratorDailyImputation();
      dailyImputation.setCollaboratorMonthlyImputation(monthlyImputation);
      dailyImputation.setDay(day);
      dailyImputation.setCharge(charge);
      return dailyImputation;
  }
}
