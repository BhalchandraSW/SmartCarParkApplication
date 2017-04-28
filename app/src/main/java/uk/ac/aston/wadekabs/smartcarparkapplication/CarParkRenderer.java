package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.Locale;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarParkItem;

import static com.google.maps.android.ui.IconGenerator.STYLE_BLUE;
import static com.google.maps.android.ui.IconGenerator.STYLE_GREEN;
import static com.google.maps.android.ui.IconGenerator.STYLE_ORANGE;

/**
 * Created by Bhalchandra Wadekar on 06/02/2017.
 */

class CarParkRenderer extends DefaultClusterRenderer<CarParkItem> {

    private final IconGenerator mIconGenerator;
    private Context mContext;

    CarParkRenderer(Context context, GoogleMap map, ClusterManager<CarParkItem> clusterManager) {

        super(context, map, clusterManager);

        mContext = context;
        mIconGenerator = new IconGenerator(mContext);
    }

    @Override
    protected int getBucket(Cluster<CarParkItem> cluster) {

        int bucket = 0;

        for (CarParkItem carParkItem : cluster.getItems()) {
            bucket += carParkItem.getCarPark().getFree();
        }

        return bucket / cluster.getSize();
    }

    @Override
    protected void onBeforeClusterItemRendered(CarParkItem carParkItem, MarkerOptions markerOptions) {

        super.onBeforeClusterItemRendered(carParkItem, markerOptions);

        int free = carParkItem.getCarPark().getFree();

        mIconGenerator.setStyle(getStyle(free));

        View view = LayoutInflater.from(mContext).inflate(R.layout.marker, null);

        TextView price = (TextView) view.findViewById(R.id.price);
        price.setText(String.format(Locale.ENGLISH, "%d", free));

        mIconGenerator.setContentView(view);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon()));
    }

    @Override
    protected int getColor(int clusterSize) {

        super.getColor(clusterSize);

        if (clusterSize < 423)
            return Color.RED;

        if (clusterSize < 846)
            return Color.BLUE;

        return Color.GREEN;
    }

    private int getStyle(int free) {

        if (free < 423)
            return STYLE_ORANGE;

        if (free < 846)
            return STYLE_BLUE;

        return STYLE_GREEN;
    }
}
