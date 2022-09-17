package com.carstendroesser.tanky.models;

import android.location.Address;

/**
 * Created by carstendrosser on 13.06.16.
 */

/**
 * Class used to store the searchparameters the network-request shall be done.
 */
public class SearchParams {

    // MEMBERS

    public static String longitude = null;
    public static String latitude = null;
    public static int radius = 5;
    public static String type = "diesel";
    public static String sortby = "price";
    public static boolean filterClosedStations = false;

    /**
     * Updates longitude & latitude by a given address.
     *
     * @param pAddress the address to take the longitude & latitude from
     */
    public static void updateByAddress(Address pAddress) {
        longitude = "" + pAddress.getLongitude();
        latitude = "" + pAddress.getLatitude();
    }
}
