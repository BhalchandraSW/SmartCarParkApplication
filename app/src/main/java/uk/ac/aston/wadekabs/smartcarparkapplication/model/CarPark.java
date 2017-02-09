package uk.ac.aston.wadekabs.smartcarparkapplication.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Bhalchandra Wadekar on 06/02/2017.
 */

public class CarPark implements ClusterItem {

    private int lotCode;
    private LatLng latLng;
    private double price;
    private int free;

    public CarPark(int lotCode, LatLng latLng) {
        this.setLotCode(lotCode);
        this.setLatLng(latLng);
    }

    public int getLotCode() {
        return this.lotCode;
    }

    public void setLotCode(int lotCode) {
        this.lotCode = lotCode;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    @Override
    public LatLng getPosition() {
        return getLatLng();
    }

    public String toString() {
        return "Car Park: [Lot Code:\t" + lotCode + "\tPosition:\t(" + latLng.latitude + ", " + latLng.longitude + ")\tPrice:\t" + price + "]";
    }
}