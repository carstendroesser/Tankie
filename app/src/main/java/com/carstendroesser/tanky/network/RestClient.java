package com.carstendroesser.tanky.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by carstendrosser on 08.06.16.
 */
public class RestClient {

    // CONSTANTS

    private static final String BASE_URL = "https://creativecommons.tankerkoenig.de";

    // MEMBERS

    private ApiService mApiService;

    // ENUMS

    /**
     * The type this RestClient should work for.
     */
    public enum Type {
        STATIONS,
        STATION;
    }

    // CONSTRUCTOR

    public RestClient(Type pType) {
        GsonBuilder builder = new GsonBuilder();

        // set the correct deserializer. station or stations.
        if (pType == Type.STATION) {
            builder.registerTypeAdapter(StationDeserializer.TYPE_STATION, new StationDeserializer());
        } else {
            builder.registerTypeAdapter(ResultsDeserializer.TYPE_STATIONS, new ResultsDeserializer());
        }

        Gson gson = builder.create();

        // create an adapter and the apiservice
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new SessionRequestInterceptor())
                .build();
        mApiService = restAdapter.create(ApiService.class);
    }

    // PUBLIC-API

    /**
     * Gets the api-service to make network-requests with.
     *
     * @return the apiservice, used for network-requests
     */
    public ApiService getApiService() {
        return mApiService;
    }

}
