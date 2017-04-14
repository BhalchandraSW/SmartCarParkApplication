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

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarPark;

/**
 * Created by Bhalchandra Wadekar on 06/02/2017.
 */

class CarParkRenderer extends DefaultClusterRenderer<CarPark> {

    private final IconGenerator mIconGenerator;
    private Context mContext;

    private static final int STYLE_LOW = 8;
    private static final int STYLE_MODERATE = 9;
    private static final int STYLE_HIGH = 10;

    CarParkRenderer(Context context, GoogleMap map, ClusterManager<CarPark> clusterManager) {

        super(context, map, clusterManager);

        mContext = context;

        mIconGenerator = new IconGenerator(context) {

            @Override
            public void setStyle(int style) {

                super.setStyle(STYLE_BLUE);

                int colour = Color.BLUE;

                int red = Color.red(colour);
                int green = Color.green(colour);
                int blue = Color.blue(colour);

                if (style >= 8) {
                    red = Math.round(Math.max(0, red - 255 / 4));
                    green = Math.round(Math.max(0, green - 255 / 4));
                    blue = Math.round(Math.max(0, blue - 255 / 4));
                }

                if (style >= 9) {
                    red = Math.round(Math.max(0, red - 255 / 4));
                    green = Math.round(Math.max(0, green - 255 / 4));
                    blue = Math.round(Math.max(0, blue - 255 / 4));
                }

                if (style >= 10) {
                    red = Math.round(Math.max(0, red - 255 / 4));
                    green = Math.round(Math.max(0, green - 255 / 4));
                    blue = Math.round(Math.max(0, blue - 255 / 4));
                }

                this.setColor(Color.rgb(red, green, blue));
            }
        };
    }

    @Override
    protected int getBucket(Cluster<CarPark> cluster) {

        int bucket = 0;

        for (CarPark carPark : cluster.getItems()) {
            bucket += (carPark.getCapacity() - carPark.getOccupancy());
        }

        return bucket / cluster.getSize();
    }

    @Override
    protected void onBeforeClusterItemRendered(CarPark carPark, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(carPark, markerOptions);

        int free = carPark.getCapacity() - carPark.getOccupancy();

        mIconGenerator.setStyle(getStyle(free));

        View view = LayoutInflater.from(mContext).inflate(R.layout.marker, null);

        TextView price = (TextView) view.findViewById(R.id.price);
        price.setText(Integer.toString(free));

        mIconGenerator.setContentView(view);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon()));
    }

    @Override
    protected int getColor(int clusterSize) {
        super.getColor(clusterSize);

        int colour = Color.BLUE;

        int red = Color.red(colour);
        int green = Color.green(colour);
        int blue = Color.blue(colour);

        if (clusterSize > 423) {
            red = Math.round(Math.max(0, red - 255 / 3));
            green = Math.round(Math.max(0, green - 255 / 3));
            blue = Math.round(Math.max(0, blue - 255 / 3));
        }

        if (clusterSize > 846) {
            red = Math.round(Math.max(0, red - 255 / 3));
            green = Math.round(Math.max(0, green - 255 / 3));
            blue = Math.round(Math.max(0, blue - 255 / 3));
        }

        if (clusterSize > 1269) {
            red = Math.round(Math.max(0, red - 255 / 3));
            green = Math.round(Math.max(0, green - 255 / 3));
            blue = Math.round(Math.max(0, blue - 255 / 3));
        }

        return Color.rgb(red, green, blue);
    }

    private int getStyle(int free) {

        if (free < 423)
            return STYLE_LOW;

        if (free < 846)
            return STYLE_MODERATE;

        return STYLE_HIGH;
    }
}
