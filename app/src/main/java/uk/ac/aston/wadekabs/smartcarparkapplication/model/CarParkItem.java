package uk.ac.aston.wadekabs.smartcarparkapplication.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import uk.ac.aston.wadekabs.smartcarparkapplication.backend.carParkApi.model.CarPark;

public class CarParkItem implements ClusterItem {

    private CarPark mCarPark;

    public CarParkItem(CarPark carPark) {
        mCarPark = carPark;
    }

    public CarPark getCarPark() {
        return mCarPark;
    }

    public void setCarPark(CarPark carPark) {
        mCarPark = carPark;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(mCarPark.getLatitude(), mCarPark.getLongitude());
    }

    @Override
    public String getTitle() {
        return mCarPark.getDescription();
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
