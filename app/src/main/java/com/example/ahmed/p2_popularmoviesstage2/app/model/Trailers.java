package com.example.ahmed.p2_popularmoviesstage2.app.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ahmed on 21/08/2016.
 */
public class Trailers {

    private String id;
    private String key;
    private String name;

    public Trailers() {

    }

    public Trailers(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
    }

    public String getId() {
        return id;
    }

    public String getKey() { return key; }

    public String getName() { return name; }

}
