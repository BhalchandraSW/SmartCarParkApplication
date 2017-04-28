package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import uk.ac.aston.wadekabs.smartcarparkapplication.backend.carParkApi.model.CarPark;
import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarParkItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public final static int REQUEST_CHECK_LOCATION_SETTINGS = 1;
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    public final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mMap;
    private Location mLastLocation;
    private LatLng mDestination;
    private Marker mDestinationMarker;

    private List<CarPark> mCarParkList = new ArrayList<>();

    private ClusterManager<CarParkItem> mClusterManager;

    private RecyclerView mRecyclerView;
    private CarParkListAdapter mAdapter;

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
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        mAdapter = new CarParkListAdapter(mCarParkList);
        mRecyclerView = (RecyclerView) findViewById(R.id.car_park_list);
        mRecyclerView.setAdapter(mAdapter);

        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);

        DividerItemDecoration decoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayout.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                int i = recyclerView.computeVerticalScrollOffset() /
                        (recyclerView.computeVerticalScrollRange() /
                                recyclerView.getAdapter().getItemCount());

                double latitude = mCarParkList.get(i).getLatitude();
                double longitude = mCarParkList.get(i).getLongitude();

                LatLng position0 = new LatLng(latitude, longitude);

                LatLngBounds bounds = LatLngBounds.builder()
                        .include(position0)
                        .build();

                latitude = mCarParkList.get(i + 1).getLatitude();
                longitude = mCarParkList.get(i + 1).getLongitude();

                LatLng position1 = new LatLng(latitude, longitude);

                if (i + 1 < mCarParkList.size())
                    bounds = bounds.including(position1);

                latitude = mCarParkList.get(i + 2).getLatitude();
                longitude = mCarParkList.get(i + 2).getLongitude();

                LatLng position2 = new LatLng(latitude, longitude);

                if (i + 2 < mCarParkList.size())
                    bounds = bounds.including(position2);

                Collection<Marker> markers = mClusterManager.getMarkerCollection().getMarkers();

                for (Marker marker : markers) {
                    if (marker.getPosition().equals(position0)
                            || marker.getPosition().equals(position1)
                            || marker.getPosition().equals(position2)) {
                        marker.setAlpha(1.0f);
                        marker.setZIndex(1.0f);
                        marker.showInfoWindow();
                    } else {
                        marker.setAlpha(0.5f);
                        marker.setZIndex(0.0f);
                        marker.hideInfoWindow();
                    }
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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

        switch (id) {

            case R.id.app_bar_filter:

                Snackbar.make(findViewById(id), "Hello", Snackbar.LENGTH_LONG).show();

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_settings:

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                break;
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
        if (mDestination == null && getLastLocation() != null) {
            this.setDestination(new LatLng(getLastLocation().getLatitude(), getLastLocation().getLongitude()));
        }
        return mDestination;
    }

    private void setDestination(LatLng destination) {
        this.mDestination = destination;
        updateUI();
    }

    private void updateUI() {

        if (mMap != null && mLastLocation != null && this.getDestination() != null) {

            if (mDestinationMarker == null) {

                mDestinationMarker = mMap.addMarker(new MarkerOptions().position(mDestination));

                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_pin_drop_black_24dp);

                IconGenerator generator = new IconGenerator(this);
                generator.setBackground(drawable);

                mDestinationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(generator.makeIcon()));

            } else {
                mDestinationMarker.setPosition(mDestination);
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDestination, 15.0f));

            try {

                mCarParkList.addAll(new CarParkEndpointAsyncTask()
                        .execute(mDestination)
                        .get());
                mAdapter.notifyDataSetChanged();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            addMarkers();
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
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void addMarkers() {

        mClusterManager.clearItems();

        for (CarPark carPark : mCarParkList) {
            mClusterManager.addItem(new CarParkItem(carPark));
        }
    }

    private void setMap(GoogleMap map) {

        mMap = map;

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new CarParkRenderer(this, mMap, mClusterManager));

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<CarParkItem>() {
            @Override
            public boolean onClusterItemClick(CarParkItem selectedCarPark) {

                Collection<Marker> markers = mClusterManager.getMarkerCollection().getMarkers();

                for (Marker marker : markers) {
                    if (marker.getPosition().equals(selectedCarPark.getPosition())) {
                        marker.setAlpha(1.0f);
                        marker.setZIndex(1.0f);
                    } else {
                        marker.setAlpha(0.5f);
                        marker.setZIndex(0.0f);
                    }
                }

                // for not showing info window
                return true;
            }
        });

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
        map.setMyLocationEnabled(true);

        UiSettings settings = map.getUiSettings();
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
        setLastLocation(LocationServices.FusedLocationApi.getLastLocation(
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

        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
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
                break;
        }
    }
}