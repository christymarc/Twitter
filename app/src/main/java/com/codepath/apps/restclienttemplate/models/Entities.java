package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Entities {
    public List<Media> media_list;
    public List<Url> url_list;

    public Entities() {}

    public static Entities fromJson(JSONObject jsonObject) {
        Entities entities = new Entities();
        try {
            entities.media_list = Media.fromJsonArray(jsonObject.getJSONArray("media"));
        } catch (JSONException e) {
            entities.media_list = null;
            e.printStackTrace();
        }
        try {
            entities.url_list = Url.fromJsonArray(jsonObject.getJSONArray("urls"));
        } catch (JSONException e) {
            entities.url_list = null;
            e.printStackTrace();
        }
        return entities;
    }
}
