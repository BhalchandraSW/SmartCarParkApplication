package uk.ac.aston.wadekabs.smartcarparkapplication.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by Bhalchandra Wadekar on 26/02/2017.
 */

public class CarPark implements ClusterItem, Serializable {

    private int capacity;
    private String description;
    private int disabledCapacity;
    private String systemCodeNumber;
    private CarParkLocation location;
    private int occupancy;
    private double occupancyPercentage;
    private double fillRate;
    private double exitRate;
    private String fault;
    private String occupancyTrend;
    private String state;
    private double queueTime;
    private String lastUpdated;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisabledCapacity() {
        return disabledCapacity;
    }

    public void setDisabledCapacity(int disabledCapacity) {
        this.disabledCapacity = disabledCapacity;
    }

    public String getSystemCodeNumber() {
        return systemCodeNumber;
    }

    public void setSystemCodeNumber(String systemCodeNumber) {
        this.systemCodeNumber = systemCodeNumber;
    }

    public CarParkLocation getLocation() {
        return location;
    }

    public void setLocation(CarParkLocation location) {
        this.location = location;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public double getFillRate() {
        return fillRate;
    }

    public void setFillRate(double fillRate) {
        this.fillRate = fillRate;
    }

    public double getExitRate() {
        return exitRate;
    }

    public void setExitRate(double exitRate) {
        this.exitRate = exitRate;
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }

    public String getOccupancyTrend() {
        return occupancyTrend;
    }

    public void setOccupancyTrend(String occupancyTrend) {
        this.occupancyTrend = occupancyTrend;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(double queueTime) {
        this.queueTime = queueTime;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void updateLiveData(CarPark carPark) {
        setOccupancy(carPark.occupancy);
        setOccupancyPercentage(carPark.occupancyPercentage);
        setFillRate(carPark.fillRate);
        setExitRate(carPark.exitRate);
        setFault(carPark.fault);
        setOccupancyTrend(carPark.occupancyTrend);
        setState(carPark.state);
        setQueueTime(carPark.queueTime);
        setLastUpdated(carPark.lastUpdated);
    }

    @Override
    public String toString() {

        String park = "Car Park:";

        park += "\tSystem Code Number:\t" + systemCodeNumber;
        park += "\tLocation: " + location;

        return park;
    }

    @Override
    public boolean equals(Object object) {

        if (super.equals(object))
            return true;
        if (object != null && object instanceof CarPark) {
            CarPark carPark = (CarPark) object;
            return carPark.getSystemCodeNumber().equals(this.getSystemCodeNumber());
        }
        return false;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(location.getLat(), location.getLng());
    }

    @Override
    public String getTitle() {
        return description;
    }

    @Override
    public String getSnippet() {
        return description;
    }
}


