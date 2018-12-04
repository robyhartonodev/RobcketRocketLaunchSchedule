package com.example.android.robcket_rocketlaunchschedule.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rocket {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("configuration")
    @Expose
    private String configuration;
    @SerializedName("familyname")
    @Expose
    private String familyname;
    @SerializedName("agencies")
    @Expose
    private List<Object> agencies = null;
    @SerializedName("wikiURL")
    @Expose
    private String wikiURL;
    @SerializedName("infoURLs")
    @Expose
    private List<String> infoURLs = null;
    @SerializedName("infoURL")
    @Expose
    private String infoURL;
    @SerializedName("imageSizes")
    @Expose
    private List<Integer> imageSizes = null;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;

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

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public List<Object> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<Object> agencies) {
        this.agencies = agencies;
    }

    public String getWikiURL() {
        return wikiURL;
    }

    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    public List<String> getInfoURLs() {
        return infoURLs;
    }

    public void setInfoURLs(List<String> infoURLs) {
        this.infoURLs = infoURLs;
    }

    public String getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    public List<Integer> getImageSizes() {
        return imageSizes;
    }

    public void setImageSizes(List<Integer> imageSizes) {
        this.imageSizes = imageSizes;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}