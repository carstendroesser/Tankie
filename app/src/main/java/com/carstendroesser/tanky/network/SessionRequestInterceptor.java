package com.carstendroesser.tanky.network;

import retrofit.RequestInterceptor;

/**
 * Created by carstendrosser on 08.06.16.
 */
public class SessionRequestInterceptor implements RequestInterceptor {

    // PUBLIC-API

    @Override
    public void intercept(RequestFacade pRequest) {
        // always attach the apikey to a network-request
        pRequest.addQueryParam("apikey", "how_about_no");
    }

}
