package com.carstendroesser.tanky.network;

import com.carstendroesser.tanky.models.SearchParams;
import com.carstendroesser.tanky.models.Station;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carstendrosser on 08.06.16.
 */
public class ResultsDeserializer implements JsonDeserializer<List<Station>> {

    // CONSTANTS

    public static final Type TYPE_STATIONS = new TypeToken<List<Station>>() {
    }.getType();

    // PUBLIC-API

    @Override
    public List<Station> deserialize(JsonElement pJsonElement, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException {
        JsonArray jsonArray = pJsonElement.getAsJsonObject().get("stations").getAsJsonArray();
        Gson gson = new Gson();
        ArrayList<Station> resultArray = new ArrayList<>();

        // iterate the json to turn it into a list of stations
        for (JsonElement jsonElement : jsonArray) {
            Station station = gson.fromJson(jsonElement.toString(), Station.class);

            // check if filter is activated
            if (SearchParams.filterClosedStations) {
                if (station.getIsOpen()) {
                    resultArray.add(station);
                }
            } else {
                resultArray.add(station);
            }
        }

        return resultArray;
    }

}
