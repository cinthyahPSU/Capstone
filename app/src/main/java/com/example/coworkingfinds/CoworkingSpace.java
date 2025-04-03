package com.example.coworkingfinds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoworkingSpace implements Serializable {
    private String name;
    private String address;
    private String photoReference;
    private double rating;
    private List<String> amenities;
    private double latitude;
    private double longitude;

    public CoworkingSpace() {}

    public CoworkingSpace(String name, String address, String photoReference, List<String> amenities, double rating, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.photoReference = photoReference;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amenities = (amenities != null) ? amenities : new ArrayList<>();
    }

    public <T> CoworkingSpace(String place, String address, String photoRef, List<T> list, double v) {
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhotoReference() { return photoReference; }
    public double getRating() { return rating; }
    public List<String> getAmenities() { return amenities; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
