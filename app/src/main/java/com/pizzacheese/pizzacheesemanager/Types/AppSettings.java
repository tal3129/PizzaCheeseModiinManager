package com.pizzacheese.pizzacheesemanager.Types;

/**
 * Created by Tal on 02/10/17.
 */

public class AppSettings{
    String receiverMail;
    boolean appStatus;
    boolean ccStatus;
    double minWheelSum;

    public AppSettings(String receiverMail,boolean appStatus, boolean ccStatus){
        this.appStatus=appStatus;
        this.receiverMail=receiverMail;
        this.ccStatus = ccStatus;
        minWheelSum = 0;
    }
    public AppSettings(){
    }

    public void setReceiverMail(String receiverMail) {
        this.receiverMail = receiverMail;
    }


    public void setAppStatus(boolean appStatus) {
        this.appStatus = appStatus;
    }

    public String getReceiverMail(){
        return receiverMail;
    }
    public boolean getAppStatus(){
        return this.appStatus;
    }

    public boolean isCcStatus() {
        return ccStatus;
    }

    public void setCcStatus(boolean ccStatus) {
        this.ccStatus = ccStatus;
    }

    public double getMinWheelSum() {
        return minWheelSum;
    }

    public void setMinWheelSum(double minWheelSum) {
        this.minWheelSum = minWheelSum;
    }
}