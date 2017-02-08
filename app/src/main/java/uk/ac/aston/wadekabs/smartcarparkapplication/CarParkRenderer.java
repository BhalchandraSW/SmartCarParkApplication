package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarPark;

/**
 * Created by Bhalchandra Wadekar on 06/02/2017.
 */

public class CarParkRenderer extends DefaultClusterRenderer<CarPark> {

    private final IconGenerator mIconGenerator;
    private ClusterManager<CarPark> mClusterManager;

    public CarParkRenderer(Context context, GoogleMap map, ClusterManager<CarPark> clusterManager) {
        super(context, map, clusterManager);
        mIconGenerator = new IconGenerator(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(CarPark item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        Bitmap icon = mIconGenerator.makeIcon(Integer.toString(item.getFree()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}
