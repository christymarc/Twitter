package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Media {

    public String media_url;
    public String type;

    public Media() {}

    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.media_url = jsonObject.getString("media_url_https");
        media.type = jsonObject.getString("type");
        return media;
    }

    public static List<Media> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Media> media_list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            media_list.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return media_list;
    }
}
