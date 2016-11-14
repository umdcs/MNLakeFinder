package edu.umn.coxxx549d.epa_fish_advisory;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;


public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static GoogleMap nMap;
    private static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 1000).setFastestInterval(5 * 1000);
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
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        Location myLocation = null;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION STATUS", " Not granted yet");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //show explanation asynchronously
            } else {
                //no explanation needed, we can request the permission
                //LOCATION_PERMISSION_REQUEST_CODE is passed
                Log.d("PERMISSION CHECK", " Going to request permissions...");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }//END ELSE

        } else {
            Log.d("PERMISSION STATUS", " Granted.");
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);

            try {
                myLocation = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                System.out.println("Caught Security Exception: " + e);
            }


            nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            nMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            nMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            mGoogleApiClient.connect();

            nMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title("You're here.").snippet("You're located."));

        }
    }

    /**
     * Handles permission requests inside the Android Manifest.
     * Sets appropriate instructions for permission denied or permission
     * granted.
     * @param requestCode Code for specific request
     * @param permissions Permissions that are requesting to be granted
     * @param grantResults Result of the user's response to permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d("onRequestPermission", "....");
        switch(requestCode) {
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission Granted, do what you gotta do
                    try {
                        nMap.setMyLocationEnabled(true);
                    }
                    catch(SecurityException e) {
                        Log.d("Caught ", "Security Exception " + e);
                    }
                }
                else {

                    //Permission Denied, Disable functionality that depends of this permission
                    try {
                        nMap.setMyLocationEnabled(false);
                    }
                    catch(SecurityException e) {
                        Log.d("Caught ", "Security Exception " + e);
                    }
                }//end else
                return;
            }
            //Other cases put here
        }//end switch
    }

    /**
     * Handles .connect() calls
     * Determines if location updates are available for use.
     * @param connectionHint Details on connection status
     */
    public void onConnected(Bundle connectionHint) {

        boolean mRequestingLocationUpdates;
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mRequestingLocationUpdates = true;
        }
        else
            mRequestingLocationUpdates = false;

        if(mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    protected void startLocationUpdates() {
        //mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        //.setInterval(30 * 1000).setFastestInterval(5 * 1000);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch(SecurityException e) {
            Log.e("Caught"," Security exception" + e);
        }
    }

    /**
     * Handles location changes while user is in the
     * Map activity.
     * @param location Current location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    public void updateUI() {

    }

    /**
     * Implements abstract method from ...
     * Handles a suspended connection when the
     * connection is not needed.
     * @param x Flag to indicate a suspended connection
     */
    public void onConnectionSuspended(int x) {

    }

    /**
     * Implements abstract method from
     * onConnectionFailedListener. Handles
     * failed connections to GPS/Service provider
     * @param result the result of the connection
     */
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean mRequestingLocationUpdates;
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mRequestingLocationUpdates = true;
        }
        else
            mRequestingLocationUpdates = false;
        if(mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
}
