package com.sfu.cmpt276groupproject.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent Restaurants, and has necessary information such as name, address,
 * list of inspections, etc.
 */
public class Restaurant {

    private String trackingNumber;
    private String name;
    private String address;
    private String city;
    private String factoryType;
    private double latitude;
    private double longitude;
    private List<Inspection> inspections = new ArrayList<>();
    private boolean isFavorite;

    /* Constructor */
    public Restaurant(String trackingNumber, String name, String address, String city,
                      String factoryType, double latitude, double longitude, boolean isfavorite) {
        this.trackingNumber = trackingNumber;
        this.name = name;
        this.address = address;
        this.city = city;
        this.factoryType = factoryType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFavorite = isfavorite;
    }


    /* Getter methods */
    public String getTrackingNumber() { return trackingNumber; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getFactoryType() { return factoryType; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<Inspection> getInspections() { return inspections; }
    public boolean isFavorite() {
        return isFavorite;
    }

    /* Setter methods */
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setFactoryType(String factoryType) { this.factoryType = factoryType; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setInspections(List<Inspection> inspections) { this.inspections = inspections; }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    /* Other methods */
    public void addInspection(Inspection inspection) {
        inspections.add(inspection);
    }
}
