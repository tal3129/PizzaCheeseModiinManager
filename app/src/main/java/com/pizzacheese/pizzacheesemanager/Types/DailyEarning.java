package com.pizzacheese.pizzacheesemanager.Types;


import java.util.Date;


/**
 * Created by Tal on 15/11/17.
 */

public class DailyEarning {
    private Date date;
    private double ccSum;
    private double cashSum;



    public DailyEarning(Date date) {
        this.date = date;
    }
    public DailyEarning(){}

    public DailyEarning(Date date, double cashSum, double ccSum) {
        this.date = date;
        this.cashSum=cashSum;
        this.ccSum=ccSum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getCcSum() {
        return ccSum;
    }

    public void setCcSum(double ccSum) {
        this.ccSum = ccSum;
    }

    public double getCashSum() {
        return cashSum;
    }

    public void setCashSum(double cashSum) {
        this.cashSum = cashSum;
    }
}
