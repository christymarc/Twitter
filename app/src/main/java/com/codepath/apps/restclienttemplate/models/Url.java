package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Url {
    public String url_object;

    public Url() {}

    public static Url fromJson(JSONObject jsonObject) throws JSONException {
        Url url = new Url();
        url.url_object = jsonObject.getString("url");
        return url;
    }

    public static List<Url> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Url> url_list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            url_list.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return url_list;
    }
}
