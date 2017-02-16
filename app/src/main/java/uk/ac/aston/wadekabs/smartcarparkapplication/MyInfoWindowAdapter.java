package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by bhalchandrawadekar on 15/02/2017.
 */

class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final View mContents;

    MyInfoWindowAdapter() {
        mWindow = null;
        mContents = null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
