package com.carstendroesser.tanky.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by carstendrosser on 08.06.16.
 */
public class Station {

    // MEMBERS

    @SerializedName("name")
    private String mName;
    @SerializedName("lat")
    private float mLatitude;
    @SerializedName("lng")
    private float mLongitude;
    @SerializedName("brand")
    private String mBrand;
    @SerializedName("dist")
    private float mDistance;
    @SerializedName("price")
    private double mPrice;
    @SerializedName("e5")
    private double mPriceE5;
    @SerializedName("e10")
    private double mPriceE10;
    @SerializedName("diesel")
    private double mPriceDiesel;
    @SerializedName("id")
    private String mId;
    @SerializedName("street")
    private String mStreet;
    @SerializedName("houseNumber")
    private String mHouseNumber;
    @SerializedName("postCode")
    private long mPostCode;
    @SerializedName("place")
    private String mPlace;
    @SerializedName("isOpen")
    private boolean mIsOpen;
    @SerializedName("wholeDay")
    private boolean mWholeDay;
    @SerializedName("openingTimes")
    private List<OpeningTime> mOpeningTimeList;

    // PUBLIC-API

    public String getName() {
        return mName;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public String getBrand() {
        return mBrand;
    }

    public float getDistance() {
        return mDistance;
    }

    public double getPrice() {
        return mPrice;
    }

    public double getPriceE5() {
        return mPriceE5;
    }

    public double getPriceE10() {
        return mPriceE10;
    }

    public double getPriceDiesel() {
        return mPriceDiesel;
    }

    public String getId() {
        return mId;
    }

    public String getStreet() {
        return mStreet;
    }

    public String getHouseNumber() {
        return mHouseNumber;
    }

    public long getPostCode() {
        return mPostCode;
    }

    public String getPlace() {
        return mPlace;
    }

    public boolean getIsOpen() {
        return mIsOpen;
    }

    public boolean getWholeDay() {
        return mWholeDay;
    }

    public List<OpeningTime> getOpenings() {
        return mOpeningTimeList;
    }

    @Override
    public String toString() {
        return "Station{" +
                "mName='" + mName + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mBrand='" + mBrand + '\'' +
                ", mDistance=" + mDistance +
                ", mPrice=" + mPrice +
                ", mId='" + mId + '\'' +
                ", mStreet='" + mStreet + '\'' +
                ", mHouseNumber='" + mHouseNumber + '\'' +
                ", mPostCode=" + mPostCode +
                ", mPlace='" + mPlace + '\'' +
                ", mIsOpen=" + mIsOpen +
                ", mWholeDay=" + mWholeDay +
                ", mOpeningTimeList=" + mOpeningTimeList +
                '}';
    }

    public class OpeningTime {

        @SerializedName("text")
        private String mText;
        @SerializedName("start")
        private String mStart;
        @SerializedName("end")
        private String mEnd;

        public String getText() {
            return mText;
        }

        public String getStart() {
            return mStart;
        }

        public String getEnd() {
            return mEnd;
        }

    }

}
