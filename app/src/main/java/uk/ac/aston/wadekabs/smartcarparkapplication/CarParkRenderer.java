package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarPark;

/**
 * Created by Bhalchandra Wadekar on 06/02/2017.
 */

class CarParkRenderer extends DefaultClusterRenderer<CarPark> {

    private final IconGenerator mIconGenerator;

    CarParkRenderer(Context context, GoogleMap map, ClusterManager<CarPark> clusterManager) {
        super(context, map, clusterManager);
        mIconGenerator = new IconGenerator(context);
    }

    @Override
    protected int getBucket(Cluster<CarPark> cluster) {

        int bucket = 0;

        for (CarPark carPark : cluster.getItems()) {
            bucket += carPark.getFree();
        }

        return bucket / cluster.getSize();
    }

    @Override
    protected void onBeforeClusterItemRendered(CarPark item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        int free = item.getFree();

        mIconGenerator.setStyle(getStyle(free));
        Bitmap icon = mIconGenerator.makeIcon(Integer.toString(free));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected int getColor(int clusterSize) {
        super.getColor(clusterSize);

        if (clusterSize <= 3)
            return -30720; // ORANGE

        if (3 < clusterSize && clusterSize < 6)
            return -16737844; // BLUE

        return -10053376; // GREEN
    }

    private int getStyle(int free) {

        if (free <= 3)
            return IconGenerator.STYLE_ORANGE;

        if (3 < free && free < 6)
            return IconGenerator.STYLE_BLUE;

        return IconGenerator.STYLE_GREEN;
    }
}
