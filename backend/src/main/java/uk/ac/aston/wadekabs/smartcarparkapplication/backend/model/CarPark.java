package uk.ac.aston.wadekabs.smartcarparkapplication.backend.model;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.ac.aston.wadekabs.smartcarparkapplication.backend.model.birminghamdatafactory.BirminghamDataFactoryCarPark;
import uk.ac.aston.wadekabs.smartcarparkapplication.backend.model.parkright.ParkRightCarPark;

public class CarPark {

    private String id;
    private String description;
    private double latitude, longitude;
    private int free;
    private String trend;

    public CarPark() {
    }

    public CarPark(BirminghamDataFactoryCarPark birminghamDataFactoryCarPark) {
        id = birminghamDataFactoryCarPark.getSystemCodeNumber();
        description = birminghamDataFactoryCarPark.getShortDescription();
        JSONObject geom = new JSONObject(birminghamDataFactoryCarPark.getGeom());
        JSONArray coordinates = geom.getJSONArray("coordinates");
        latitude = coordinates.getDouble(1);
        longitude = coordinates.getDouble(0);
        free = birminghamDataFactoryCarPark.getCapacity()
                - birminghamDataFactoryCarPark.getOccupancy();
    }

    public CarPark(ParkRightCarPark parkRightCarPark) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
