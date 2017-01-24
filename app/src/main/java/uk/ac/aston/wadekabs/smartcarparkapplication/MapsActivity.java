package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
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
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarParkContent;
import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarParkItem;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static int REQUEST_CHECK_LOCATION_SETTINGS = 1;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mMap;
    private Location mLastLocation;
    private Marker mMarker;
    private List<CarPark> carParkList = new ArrayList<>();

    private Map<Integer, CarPark> carParkMap = new LinkedHashMap<>();
    private boolean locationIsAvailable = false, occupancyIsAvailable = false;

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

        MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        String url = "https://api.parkright.io/smartlot/v1/OccupanciesByCoordinate/e0c50d6bc5fc4223a37f3d893e0b7d27/UKLCYWC01/51.5209/-0.1741/";

        // Request a json response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    JSONObject object = response.optJSONObject(i);

                    int free = 0, lotCode = 0;

                    try {
                        free = object.getInt("Free");
                        lotCode = object.getInt("LotCode");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    CarParkItem carPark = new CarParkItem(lotCode, Integer.toString(free), "");
                    CarParkContent.addItem(carPark);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue accessed through singleton class.
        MySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
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

            String key = "AIzaSyDhqp3V7EwriI-CP_2osYH-8ZuC6GuyWGs";

            // update list of car parks here
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + key + "&location=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&radius=50000&type=parking";

            // Instantiate the RequestQueue.
            final RequestQueue queue = Volley.newRequestQueue(this);

//            // Request a string response from the provided URL.
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject object = new JSONObject(response);
//                                JSONArray results = object.getJSONArray("results");
//
//                                for (int i = 0; i < results.length(); i++) {
//                                    JSONObject place = (JSONObject) results.get(i);
//                                    JSONObject geometry = place.getJSONObject("geometry");
//                                    JSONObject location = geometry.getJSONObject("location");
//
//                                    double lat = location.getDouble("lat");
//                                    double lng = location.getDouble("lng");
//
//                                    LatLng placeLatLng = new LatLng(lat, lng);
//
//                                    // mMap.addCircle(new CircleOptions().center(placeLatLng).radius(500).fillColor(Color.BLUE).strokeWidth(0.5f));
//
//                                    CarPark carPark = new CarPark();
//                                    carPark.setLatLng(placeLatLng);
//                                    carParkList.add(carPark);
//                                }
//
//                                locationIsAvailable = true;
//                                addMarkers();
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getApplicationContext(), "That didn't work!", Toast.LENGTH_LONG).show();
//                }
//            });

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

                                        System.out.println("Occupancies: " + occupancies);

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


//            // Request a string response from the provided URL.
//            StringRequest priceAndOccupancyRequest = new StringRequest(Request.Method.POST, occupanciesURL,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                // JSONObject object = new JSONObject(response);
//                                JSONArray array = new JSONArray(response);
//
//                                System.out.println(array);
//
////                                JSONArray results = object.getJSONArray("results");
////
////                                for (int i = 0; i < results.length(); i++) {
////                                    JSONObject place = (JSONObject) results.get(i);
////                                    JSONObject geometry = place.getJSONObject("geometry");
////                                    JSONObject location = geometry.getJSONObject("location");
////
////                                    double lat = location.getDouble("lat");
////                                    double lng = location.getDouble("lng");
////
////                                    LatLng placeLatLng = new LatLng(lat, lng);
////
////                                    // mMap.addCircle(new CircleOptions().center(placeLatLng).radius(500).fillColor(Color.BLUE).strokeWidth(0.5f));
////
////                                    CarPark carPark = new CarPark();
////                                    carPark.setLatLng(placeLatLng);
////                                    carParkList.add(carPark);
////                                }
//
//                                occupancyIsAvailable = true;
//                                addMarkers();
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getApplicationContext(), "That didn't work!", Toast.LENGTH_LONG).show();
//                }
//            });

            // Add the request to the RequestQueue.
            // queue.add(stringRequest);
            queue.add(lotRequest);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
    }

    private void addMarkers() {

        if (locationIsAvailable && occupancyIsAvailable) {

            locationIsAvailable = occupancyIsAvailable = false;

            for (CarPark carPark : carParkMap.values()) {
                if (carPark.getFree() > 0)
                    mMap.addMarker(new MarkerOptions().position(carPark.getLatLng()).title(Integer.toString(carPark.getFree())));
            }
        }
    }

    class CarPark {

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
    }
}
