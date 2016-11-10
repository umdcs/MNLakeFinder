package edu.umn.coxxx549d.epa_fish_advisory;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String mLastUpdateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API)
                .build();
        if(mGoogleApiClient != null)
            Log.d("Google API: ", "Connected");
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 1000).setFastestInterval(5 * 1000);
        if(mLocationRequest != null)
            Log.d("Location Request: ", "Created");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location myLocation = null;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION STATUS: ", "Not granted yet.");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show an explanation asynchronously
            }
            else {
                //No explanation needed, we can request the permission
                //LOCATION_PERMISSION_REQUEST_CODE is passed
                Log.d("PERMISSION CHECK:", " Going to request permissions....");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

            }//end else
        }
        else {
            Log.d("PERMISSION STATUS: ", "Granted.");
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);

            try {
                myLocation = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                System.out.println("Caught Security Exception: " + e);
            }


            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            mGoogleApiClient.connect(); //Call onConnected for location updates

            //Change title to name of lake on dropped pin
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title("You are here.").snippet("Consider yourself located."));
        }//end else
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
                        mMap.setMyLocationEnabled(true);
                    }
                    catch(SecurityException e) {
                        Log.d("Caught ", "Security Exception " + e);
                    }
                }
                else {

                    //Permission Denied, Disable functionality that depends of this permission
                    try {
                        mMap.setMyLocationEnabled(false);
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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