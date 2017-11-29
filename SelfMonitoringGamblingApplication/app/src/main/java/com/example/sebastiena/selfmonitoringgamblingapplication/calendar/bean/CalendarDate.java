package com.example.sebastiena.selfmonitoringgamblingapplication.calendar.bean;

import java.io.Serializable;

/**
 * yangqiangyu on 09/11/2016 14:17
 */

public class CalendarDate implements Serializable {

    private int id;
    private int productId;
    private String outcomeDate;
    private double outcome;
    private int limitStock;
    private int stock;
    private int online;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getOutcomeDate() {
        return  outcomeDate;
    }

    public void setOutcomeDate(String outcomeDate) {
        this. outcomeDate = outcomeDate;
    }

    public double getOutcome() {
        return outcome;
    }

    public void setOutcome(double outcome) {
        this.outcome = outcome;
    }

    public int getLimitStock() {
        return limitStock;
    }

    public void setLimitStock(int limitStock) {
        this.limitStock = limitStock;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return String.format("%s %s", outcomeDate, outcome);
    }
}
