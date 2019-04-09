package com.sqli.imputation.service.dto;

public class AppChargeDTO {

    private String appLogin;
    private int day;
    private Double charge;

    public String getAppLogin() {
        return appLogin;
    }

    public void setAppLogin(String appLogin) {
        this.appLogin = appLogin;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    @Override
    public String toString() {
        return "AppChargeDTO{" +
            "appLogin='" + appLogin + '\'' +
            ", day=" + day +
            ", charge=" + charge +
            '}';
    }
}
