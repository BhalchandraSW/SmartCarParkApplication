package uk.ac.aston.wadekabs.smartcarparkapplication.model;

import java.io.Serializable;

/**
 * Created by Bhalchandra Wadekar on 26/02/2017.
 */

class CarParkLocation implements Serializable {

    private double lat;
    private double lng;

    double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lng + ")";
    }
}
