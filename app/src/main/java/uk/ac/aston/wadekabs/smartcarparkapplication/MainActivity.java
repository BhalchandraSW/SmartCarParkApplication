package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarPark;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static int REQUEST_CHECK_LOCATION_SETTINGS = 1;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    public final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mMap;
    private Location mLastLocation;
    private LatLng destination;
    private Marker mDestinationMarker;

    private boolean locationIsAvailable = false, occupancyIsAvailable = false;

    private Map<Integer, CarPark> carParkMap = new LinkedHashMap<>();
    private ClusterManager<CarPark> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.app_bar_filter:
                return true;
            case R.id.app_bar_search:

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                    e.printStackTrace();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Location getLastLocation() {
        return mLastLocation;
    }

    private void setLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
        updateUI();
    }

    private LatLng getDestination() {
        if (destination == null && getLastLocation() != null) {
            this.setDestination(new LatLng(getLastLocation().getLatitude(), getLastLocation().getLongitude()));
        }
        return destination;
    }

    private void setDestination(LatLng destination) {
        this.destination = destination;
        updateUI();
    }

    /**
     * Add/Update marker at last known location.
     */
    private void updateUI() {

        Log.i("updateUI", "mMap:\t" + mMap);
        Log.i("updateUI", "mLastLocation:\t" + mLastLocation);
        Log.i("updateUI", "destination: " + destination);

        if (mMap != null && mLastLocation != null && this.getDestination() != null) {

            if (mDestinationMarker == null) {

                mDestinationMarker = mMap.addMarker(new MarkerOptions().position(destination).draggable(true));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Drawable drawable = getResources().getDrawable(R.drawable.ic_flag_black_24dp, null);

                    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);

                    mDestinationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }

            } else {
                mDestinationMarker.setPosition(destination);
            }

            // Instantiate the RequestQueue.
            final MySingleton queue = MySingleton.getInstance(this.getApplicationContext());

            String apiKey = "e0c50d6bc5fc4223a37f3d893e0b7d27";
            String siteCode = "UKLCYWC01";

            String lotURL = "https://api.parkright.io/smartlot/v1/LotsByCoordinate/" + apiKey + "/" + siteCode + "/" + this.getDestination().latitude + "/" + this.getDestination().longitude + "/";
            final String occupanciesURL = "https://api.parkright.io/smartlot/v1/OccupanciesByLots/" + apiKey + "/" + siteCode + "/";

            // Request a string response from the provided URL.
            StringRequest lotRequest = new StringRequest(Request.Method.GET, lotURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONArray lots = new JSONArray(response);

                                System.out.println("lots:\t" + lots.length());

                                for (int i = 0; i < lots.length(); i++) {

                                    JSONObject carParkObject = (JSONObject) lots.get(i);

                                    int lotCode = carParkObject.getInt("LotCode");
                                    LatLng latLng = new LatLng(carParkObject.getDouble("Latitude"), carParkObject.getDouble("Longitude"));

                                    carParkMap.put(lotCode, new CarPark(lotCode, latLng));
                                }

                                locationIsAvailable = true;
                                addMarkers();

                                queue.addToRequestQueue(new JsonArrayRequest(Request.Method.POST, occupanciesURL, new JSONArray(carParkMap.keySet()), new Response.Listener<JSONArray>() {
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
                                        Log.i("Occupancy request", "Volley error: " + error);
                                    }
                                }));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Lot request", "Volley error: " + error);
                }
            });

            // Add the request to the RequestQueue.
            queue.addToRequestQueue(lotRequest);


            // String tarrifUrl = "https://api.parkright.io/smartlot/v1/Tariffs/e0c50d6bc5fc4223a37f3d893e0b7d27/UKLCYWC01/";

//            queue.addToRequestQueue(new JsonArrayRequest(Request.Method.GET, tarrifUrl, null, new Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray tarrifs) {
//
//                    System.out.println("tarrifs:\t" + tarrifs.length());
//
////                    for (int i = 0; i < occupancies.length(); i++) {
////                        try {
////
////                            JSONObject occupancy = occupancies.getJSONObject(i);
////
////                            int lotCode = occupancy.getInt("LotCode");
////                            CarPark carPark = carParkMap.get(lotCode);
////
////                            int free = occupancy.getInt("Free");
////                            carPark.setFree(free);
////
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                    }
////
////                    occupancyIsAvailable = true;
////                    addMarkers();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.i("Tarrif request", "Volley error: " + error);
//                }
//            }));

            System.out.println("Lot request added.");

            mDestinationMarker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15.0f));
        }
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
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("onConnected", "bundle:\t" + bundle);

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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

    private void setMap(GoogleMap mMap) {

        this.mMap = mMap;

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, this.mMap);
        mClusterManager.setRenderer(new CarParkRenderer(getApplicationContext(), this.mMap, mClusterManager));

        // Point the map's listeners at the listeners implemented by the cluster manager.
        this.mMap.setOnCameraIdleListener(mClusterManager);

        this.mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        UiSettings settings = mMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
        settings.setIndoorLevelPickerEnabled(false);

        updateUI();
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
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_LOCATION_SETTINGS);
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
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.setLastLocation(location);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("Place onActivityResult", "Place: " + place.getName());
                MainActivity.this.setDestination(place.getLatLng());
                mDestinationMarker.setTitle(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Place onActivityResult", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        private final View mContents;

        MyInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            CarPark carPark = null;

            for (CarPark park : carParkMap.values()) {

                if (park.getPosition().equals(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))) {
                    carPark = park;
                    break;
                }
            }

            if (carPark != null) {
                TextView city = (TextView) mWindow.findViewById(R.id.city_text_view);
                city.setText(Integer.toString(carPark.getFree()));
            }

            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return mContents;
        }
    }
}
