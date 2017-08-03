package com.example.ahmed.p2_popularmoviesstage2.app.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ahmed on 26/08/2016.
 */
public class Reviews {

    private String id;
    private String author;
    private String content;

    public Reviews() {

    }

    public Reviews(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.author = trailer.getString("author");
        this.content = trailer.getString("content");
    }

    public String getId() { return id; }

    public String getAuthor() { return author; }

    public String getContent() { return content; }
}
