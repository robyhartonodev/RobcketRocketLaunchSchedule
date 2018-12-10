package com.example.android.robcket_rocketlaunchschedule.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mission {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("wikiURL")
    @Expose
    private String wikiURL;
    @SerializedName("typeName")
    @Expose
    private String typeName;
    @SerializedName("agencies")
    @Expose
    private ArrayList<Agency> agencies = null;
    @SerializedName("payloads")
    @Expose
    private ArrayList<Payload> payloads = null;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWikiURL() {
        return wikiURL;
    }

    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ArrayList<Agency> getAgencies() {
        return agencies;
    }

    public void setAgencies(ArrayList<Agency> agencies) {
        this.agencies = agencies;
    }

    public ArrayList<Payload> getPayloads() {
        return payloads;
    }

    public void setPayloads(ArrayList<Payload> payloads) {
        this.payloads = payloads;
    }

}