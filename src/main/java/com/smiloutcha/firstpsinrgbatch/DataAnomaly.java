package com.smiloutcha.firstpsinrgbatch;


public class DataAnomaly {

    private String date;
    private AnomalyType type;
    private double value;

    public DataAnomaly(String date, AnomalyType type, double value) {
        this.date = date;
        this.type = type;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public AnomalyType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }
}