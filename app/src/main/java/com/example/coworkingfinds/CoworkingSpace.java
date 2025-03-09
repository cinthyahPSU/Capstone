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

    public CoworkingSpace() {}

    public CoworkingSpace(String name, String address, String photoReference, List<String> amenities, double rating) {
        this.name = name;
        this.address = address;
        this.photoReference = photoReference;
        this.rating = rating;
        this.amenities = (amenities != null) ? amenities : new ArrayList<>();
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhotoReference() { return photoReference; }
    public double getRating() { return rating; }
    public List<String> getAmenities() {
        return amenities;
    }
}
