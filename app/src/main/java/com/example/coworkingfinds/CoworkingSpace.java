package com.example.coworkingfinds;

import java.util.ArrayList;
import java.util.List;

public class CoworkingSpace {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private List<String> amenities;


    public CoworkingSpace() {}


    public CoworkingSpace(String name, String address, double latitude, double longitude, List<String> amenities) {
        this.name = (name != null) ? name : "Unknown Name";  // Prevents null values
        this.address = (address != null) ? address : "Address Not Available";
        this.latitude = latitude;
        this.longitude = longitude;
        this.amenities = (amenities != null) ? amenities : new ArrayList<>();  // Avoids null lists
    }

    public CoworkingSpace(String name, String address) {
        this(name, address, 0.0, 0.0, new ArrayList<>());
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<String> getAmenities() { return amenities; }
}
