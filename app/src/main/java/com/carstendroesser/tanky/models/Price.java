package com.carstendroesser.tanky.models;

/**
 * Created by carstendrosser on 29.06.16.
 */
public class Price {

    // MEMBERS

    private String mPriceType;
    private double mPriceValue;
    private int mPriceTime;

    // CONSTRUCTOR

    public Price(String pPriceType, double pPriceValue, int pPriceTime) {
        mPriceType = pPriceType;
        mPriceValue = pPriceValue;
        mPriceTime = pPriceTime;
    }

    // PUBLIC-API

    public String getPriceType() {
        return mPriceType;
    }

    public double getPriceValue() {
        return mPriceValue;
    }

    public int getPriceTime() {
        return mPriceTime;
    }

}
