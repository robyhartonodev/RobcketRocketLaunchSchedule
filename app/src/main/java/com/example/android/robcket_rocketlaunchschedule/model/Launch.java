package com.example.android.robcket_rocketlaunchschedule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Launch {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("windowstart")
    @Expose
    private String windowstart;
    @SerializedName("windowend")
    @Expose
    private String windowend;
    @SerializedName("net")
    @Expose
    private String net;
    @SerializedName("wsstamp")
    @Expose
    private int wsstamp;
    @SerializedName("westamp")
    @Expose
    private int westamp;
    @SerializedName("netstamp")
    @Expose
    private int netstamp;
    @SerializedName("isostart")
    @Expose
    private String isostart;
    @SerializedName("isoend")
    @Expose
    private String isoend;
    @SerializedName("isonet")
    @Expose
    private String isonet;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("inhold")
    @Expose
    private int inhold;
    @SerializedName("tbdtime")
    @Expose
    private int tbdtime;
    @SerializedName("vidURLs")
    @Expose
    private ArrayList<String> vidURLs = null;
    @SerializedName("vidURL")
    @Expose
    private Object vidURL;
    @SerializedName("infoURLs")
    @Expose
    private ArrayList<Object> infoURLs = null;
    @SerializedName("infoURL")
    @Expose
    private Object infoURL;
    @SerializedName("holdreason")
    @Expose
    private Object holdreason;
    @SerializedName("failreason")
    @Expose
    private Object failreason;
    @SerializedName("tbddate")
    @Expose
    private int tbddate;
    @SerializedName("probability")
    @Expose
    private int probability;
    @SerializedName("hashtag")
    @Expose
    private Object hashtag;
    @SerializedName("changed")
    @Expose
    private String changed;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("rocket")
    @Expose
    private Rocket rocket;
    @SerializedName("missions")
    @Expose
    private ArrayList<Mission> missions = null;
    @SerializedName("lsp")
    @Expose
    private Lsp lsp;

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

    public String getWindowstart() {
        return windowstart;
    }

    public void setWindowstart(String windowstart) {
        this.windowstart = windowstart;
    }

    public String getWindowend() {
        return windowend;
    }

    public void setWindowend(String windowend) {
        this.windowend = windowend;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public int getWsstamp() {
        return wsstamp;
    }

    public void setWsstamp(int wsstamp) {
        this.wsstamp = wsstamp;
    }

    public int getWestamp() {
        return westamp;
    }

    public void setWestamp(int westamp) {
        this.westamp = westamp;
    }

    public int getNetstamp() {
        return netstamp;
    }

    public void setNetstamp(int netstamp) {
        this.netstamp = netstamp;
    }

    public String getIsostart() {
        return isostart;
    }

    public void setIsostart(String isostart) {
        this.isostart = isostart;
    }

    public String getIsoend() {
        return isoend;
    }

    public void setIsoend(String isoend) {
        this.isoend = isoend;
    }

    public String getIsonet() {
        return isonet;
    }

    public void setIsonet(String isonet) {
        this.isonet = isonet;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getInhold() {
        return inhold;
    }

    public void setInhold(int inhold) {
        this.inhold = inhold;
    }

    public int getTbdtime() {
        return tbdtime;
    }

    public void setTbdtime(int tbdtime) {
        this.tbdtime = tbdtime;
    }

    public ArrayList<String> getVidURLs() {
        return vidURLs;
    }

    public void setVidURLs(ArrayList<String> vidURLs) {
        this.vidURLs = vidURLs;
    }

    public Object getVidURL() {
        return vidURL;
    }

    public void setVidURL(Object vidURL) {
        this.vidURL = vidURL;
    }

    public ArrayList<Object> getInfoURLs() {
        return infoURLs;
    }

    public void setInfoURLs(ArrayList<Object> infoURLs) {
        this.infoURLs = infoURLs;
    }

    public Object getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(Object infoURL) {
        this.infoURL = infoURL;
    }

    public Object getHoldreason() {
        return holdreason;
    }

    public void setHoldreason(Object holdreason) {
        this.holdreason = holdreason;
    }

    public Object getFailreason() {
        return failreason;
    }

    public void setFailreason(Object failreason) {
        this.failreason = failreason;
    }

    public int getTbddate() {
        return tbddate;
    }

    public void setTbddate(int tbddate) {
        this.tbddate = tbddate;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public Object getHashtag() {
        return hashtag;
    }

    public void setHashtag(Object hashtag) {
        this.hashtag = hashtag;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }

    public ArrayList<Mission> getMissions() {
        return missions;
    }

    public void setMissions(ArrayList<Mission> missions) {
        this.missions = missions;
    }

    public Lsp getLsp() {
        return lsp;
    }

    public void setLsp(Lsp lsp) {
        this.lsp = lsp;
    }

}