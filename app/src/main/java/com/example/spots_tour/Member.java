package com.example.spots_tour;

import com.google.android.gms.maps.model.LatLng;

public class Member {
    private  String name;
    private  String Videouri;
    private  String search;
    private  double latitude;
    private  double longitude;
    private  String comment;


    public Member() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideouri() {
        return Videouri;
    }

    public void setVideouri(String videouri) {
        Videouri = videouri;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
