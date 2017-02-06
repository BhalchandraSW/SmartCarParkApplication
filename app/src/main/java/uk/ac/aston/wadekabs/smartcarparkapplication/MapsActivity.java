package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarPark;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static int REQUEST_CHECK_LOCATION_SETTINGS = 1;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mMap;
    private Location mLastLocation;
    private Marker mMarker;

    private Map<Integer, CarPark> carParkMap = new LinkedHashMap<>();
    private boolean locationIsAvailable = false, occupancyIsAvailable = false;
    private ClusterManager<CarPark> mClusterManager;

    private void setLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
        updateUI();
    }

    private void setMap(GoogleMap mMap) {
        this.mMap = mMap;
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.setMap(googleMap);

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, googleMap);
        mClusterManager.setRenderer(new CarParkRenderer(getApplicationContext(), googleMap, mClusterManager));

        // Point the map's listeners at the listeners implemented by the cluster manager.
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getTitle() == null) {
                    for (CarPark carPark : carParkMap.values()) {
                        LatLng latLng = marker.getPosition();

                        if (carPark.getPosition().equals(latLng)) {

                            IconGenerator iconGenerator = new IconGenerator(getApplicationContext());
                            Bitmap bitmap = iconGenerator.makeIcon(Integer.toString(carPark.getFree()));

                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        }
                    }
                }

                marker.showInfoWindow();
                return false;
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    protected void startLocationUpdates() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            return;
        }
        this.setLastLocation(LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient));

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialise location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startLocationUpdates();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_LOCATION_SETTINGS:
                if (resultCode == RESULT_OK)
                    startLocationUpdates();
                return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.setLastLocation(location);
    }

    /**
     * Add/Update marker at last known location.
     */
    private void updateUI() {

        if (mMap != null && mLastLocation != null) {

            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            if (mMarker == null) {
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!"));
            } else {
                mMarker.setPosition(latLng);
            }

            // Instantiate the RequestQueue.
            final RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

            String apiKey = "e0c50d6bc5fc4223a37f3d893e0b7d27";
            String siteCode = "UKLCYWC01";

            String lotURL = "https://api.parkright.io/smartlot/v1/LotsByCoordinate/" + apiKey + "/" + siteCode + "/" + mLastLocation.getLatitude() + "/" + mLastLocation.getLongitude() + "/";
            final String occupanciesURL = "https://api.parkright.io/smartlot/v1/OccupanciesByLots/" + apiKey + "/" + siteCode + "/";

            // Request a string response from the provided URL.
            StringRequest lotRequest = new StringRequest(Request.Method.GET, lotURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONArray lots = new JSONArray(response);


                                for (int i = 0; i < lots.length(); i++) {

                                    JSONObject carParkObject = (JSONObject) lots.get(i);

                                    int lotCode = carParkObject.getInt("LotCode");
                                    LatLng latLng = new LatLng(carParkObject.getDouble("Latitude"), carParkObject.getDouble("Longitude"));

                                    carParkMap.put(lotCode, new CarPark(lotCode, latLng));
                                }

                                locationIsAvailable = true;
                                addMarkers();

                                queue.add(new JsonArrayRequest(Request.Method.POST, occupanciesURL, new JSONArray(carParkMap.keySet()), new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray occupancies) {

                                        for (int i = 0; i < occupancies.length(); i++) {
                                            try {

                                                JSONObject occupancy = occupancies.getJSONObject(i);

                                                int lotCode = occupancy.getInt("LotCode");
                                                CarPark carPark = carParkMap.get(lotCode);

                                                int free = occupancy.getInt("Free");
                                                carPark.setFree(free);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        occupancyIsAvailable = true;
                                        addMarkers();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "That didn't work!", Toast.LENGTH_LONG).show();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(lotRequest);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
    }

    private void addMarkers() {

        if (locationIsAvailable && occupancyIsAvailable) {

            locationIsAvailable = occupancyIsAvailable = false;

            for (CarPark carPark : carParkMap.values()) {
                if (carPark.getFree() > 0) {
                    mClusterManager.addItem(carPark);
                }
            }
        }
    }
}
