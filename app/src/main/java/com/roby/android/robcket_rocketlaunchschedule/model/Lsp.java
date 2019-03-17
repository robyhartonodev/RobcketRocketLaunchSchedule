package com.roby.android.robcket_rocketlaunchschedule.model;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Lsp {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("abbrev")
    @Expose
    private String abbrev;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("infoURL")
    @Expose
    private Object infoURL;
    @SerializedName("wikiURL")
    @Expose
    private String wikiURL;
    @SerializedName("changed")
    @Expose
    private String changed;
    @SerializedName("infoURLs")
    @Expose
    private ArrayList<String> infoURLs = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(Object infoURL) {
        this.infoURL = infoURL;
    }

    public String getWikiURL() {
        return wikiURL;
    }

    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public ArrayList<String> getInfoURLs() {
        return infoURLs;
    }

    public void setInfoURLs(ArrayList<String> infoURLs) {
        this.infoURLs = infoURLs;
    }

}
