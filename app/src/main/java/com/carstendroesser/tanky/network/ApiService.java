package com.carstendroesser.tanky.network;

import com.carstendroesser.tanky.models.Station;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by carstendrosser on 08.06.16.
 */
public interface ApiService {

    /**
     * Starts a network-request to retreive gasstations.
     *
     * @param pLatitude  the location's latitude
     * @param pLongitude the location's longitude
     * @param pRadius    the radius from the location on
     * @param pType      the fueltype to search for
     * @param pSortby    sortingoption: price or dist
     * @param pCallback  callback to get notified about results
     */
    @GET("/json/list.php")
    public void getGasStations(@Query("lat") String pLatitude,
                               @Query("lng") String pLongitude,
                               @Query("rad") String pRadius,
                               @Query("type") String pType,
                               @Query("sort") String pSortby,
                               Callback<List<Station>> pCallback);

    /**
     * Start a network-request to retreive a station with all it's
     * details by a given station-id.
     *
     * @param pId       the id of station to search for
     * @param pCallback callback to get notified about results
     */
    @GET("/json/detail.php")
    public void getStationForId(@Query("id") String pId, Callback<Station> pCallback);

}
