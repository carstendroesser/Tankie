package com.carstendroesser.tanky.network;

import android.util.Log;

import com.carstendroesser.tanky.models.Station;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by carstendrosser on 08.06.16.
 */
public class StationDeserializer implements JsonDeserializer<Station> {

    // CONSTANTS

    public static final Type TYPE_STATION = new TypeToken<Station>() {
    }.getType();

    // PUBLIC-API

    @Override
    public Station deserialize(JsonElement pJsonElement, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException {
        JsonElement jsonElement = pJsonElement.getAsJsonObject().get("station");
        Gson gson = new Gson();
        Station station = gson.fromJson(jsonElement.toString(), Station.class);
        return station;
    }

}
