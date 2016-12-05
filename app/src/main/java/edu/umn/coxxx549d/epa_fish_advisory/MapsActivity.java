package edu.umn.coxxx549d.epa_fish_advisory;

import android.*;
import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static edu.umn.coxxx549d.epa_fish_advisory.R.id.url;


public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static GoogleMap nMap;
    private static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String mLastUpdateTime, adviseName;
    static final String API_URL = "HTTP://services.dnr.state.mn.us/api/lakefinder/by_name/v1?name=";
    SearchView searchView;

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

        //Get the potential string extra for a lake name
        //and start a new lake search if there is a string extra
        Intent intent = getIntent();
        adviseName = intent.getStringExtra("EXTRA_NAME");
        if(adviseName != null) {
            new RetrieveLakeTask();
        }
    }


    //Find a way to search the local area for a nearby lake OR go straight to advisory page and display the current lake


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
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                //collapse the search
//                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
//                alertDialog.setTitle("Alert");
//                alertDialog.setMessage("Query has been submit: " + query);
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                alertDialog.show();

                new RetrieveLakeTask().execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //do stuff
                return false;
            }
        });
        super.onCreateOptionsMenu(menu);

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

        if (id == R.id.nav_main) {//camera->main
            // Handle the camera action
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_consumption) {//slideshow->consumption
            Intent intent = new Intent(this, ConsumptionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_advisory) {//gallery->advisory
            Intent intent = new Intent(this, AdvisoryActivity.class);
            startActivity(intent);

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



        //animate camera to geographical center of minnesota
        LatLng center = new LatLng(46.348723, -94.197646);
        nMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        nMap.animateCamera(CameraUpdateFactory.zoomTo(8));

        //satellite view works better with the lake depth overlay
        nMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Set padding
        //nMap.setPadding(100, 0, 0, 0);

        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String s = String.format(Locale.US, "http://maps1.dnr.state.mn.us/mapcache/gmaps/lakefinder@mn_google/%d/%d/%d.png",
                        zoom, x, y);

                if(!checkTileExists(x, y, zoom)) {
                    return null;
                }
                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 8;
                int maxZoom = 16;

                if((zoom < minZoom || zoom > maxZoom)) {
                    return false;
                }
                return true;
            }
        };

        //Add lake depth overlay
        TileOverlay tileOverlay = nMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        tileOverlay.setTransparency(0.3f);
        tileOverlay.setFadeIn(true);

        nMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //use for getting
                double clickLat = 0.0;
                double clickLong = 0.0;

                MarkerOptions marker = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude))
                        .title("Test");
                nMap.addMarker(marker);
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //show explanation asynchronously
                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("This app needs your location to provide a better experience.");

                //Send user a permission dialog if not cancelled
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Turn on location", new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int which) {
                       ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                       dialog.dismiss();
                   }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
                });
                alertDialog.show();

                //Restructure code to get into the else below if we get into this if
            } else {
                //no explanation needed, we can request the permission
                //LOCATION_PERMISSION_REQUEST_CODE is passed
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }//END ELSE

        } else {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);

            try {
                myLocation = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                System.out.println("Caught Security Exception: " + e);
            }

            //Disable default location button that is covered by toolbar
            //nMap.getUiSettings().setMyLocationButtonEnabled(false);

            //get current location
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            //move camera to current location
            nMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            nMap.animateCamera(CameraUpdateFactory.zoomTo(14));

            //connect api client for location updates
            mGoogleApiClient.connect();

            //drop marker on current location
            nMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title("You're here.").snippet("You're located."));


        }
        //zoom to minnesota if permission not granted
        double latitude = 46.3527;
        double longitude = -94.2020;
        LatLng latLng = new LatLng(latitude, longitude);
        nMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        nMap.animateCamera(CameraUpdateFactory.zoomTo(6));
    }

    /**
     * Handles permission requests inside the Android Manifest.
     * Sets appropriate instructions for permission denied or permission
     * granted.
     *
     * @param requestCode  Code for specific request
     * @param permissions  Permissions that are requesting to be granted
     * @param grantResults Result of the user's response to permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission Granted, do what you gotta do
                    try {
                        nMap.setMyLocationEnabled(true);
                    } catch (SecurityException e) {
                        throw new SecurityException(e);
                    }
                } else {

                    //Permission Denied, Disable functionality that depends of this permission
                    try {
                        nMap.setMyLocationEnabled(false);
                    } catch (SecurityException e) {
                        throw new SecurityException(e);
                    }
                }//end else
            }
            //Other cases put here
        }//end switch
    }

    /**
     * Handles .connect() calls
     * Determines if location updates are available for use.
     *
     * @param connectionHint Details on connection status
     */
    public void onConnected(Bundle connectionHint) {

        boolean mRequestingLocationUpdates;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mRequestingLocationUpdates = true;
        } else
            mRequestingLocationUpdates = false;

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        //mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        //.setInterval(30 * 1000).setFastestInterval(5 * 1000);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.e("Caught", " Security exception" + e);
        }
    }

    /**
     * Handles location changes while user is in the
     * Map activity.
     *
     * @param location Current location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    /**
     * Implements abstract method from ...
     * Handles a suspended connection when the
     * connection is not needed.
     *
     * @param x Flag to indicate a suspended connection
     */
    public void onConnectionSuspended(int x) {
        //Do stuff
    }

    /**
     * Implements abstract method from
     * onConnectionFailedListener. Handles
     * failed connections to GPS/Service provider
     *
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mRequestingLocationUpdates = true;
        } else
            mRequestingLocationUpdates = false;
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Performs lake search to animate
     * camera over the specified lake
     *
     */
    class RetrieveLakeTask extends AsyncTask<Void, Void, String> {

        String lakeName = searchView.getQuery().toString();
        LatLng lakeLatLng;

        protected void onPreExecute() {
            //reset lakeName to adviseName
            //adviseName will be null if we aren't coming from advisory activity
            if(adviseName != null) {
                lakeName = adviseName;
            }
        }

        protected String doInBackground(Void... urls) {

            try{
                URL url = new URL(API_URL + lakeName);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally {
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "There was an error";
            }

            try{
                JSONObject jsonObj = new JSONObject(response);
                JSONArray results = jsonObj.getJSONArray("results");

                    JSONObject c = results.getJSONObject(1);

                    JSONObject point = c.getJSONObject("point");

                    //gets coordinates for lake
                    String coord = point.getString("epsg:4326");

                    //splits the two coordinate strings
                    String array[] = coord.split(",");

                    //replace extra characters left from JSON data
                    String Lat = array[0];
                    String Lon = array[1];
                    String tempLat = Lat.replace('[', ' ');
                    String tempLon = Lon.replace(']', ' ');

                    //parse strings to doubles
                    //coordinates were reversed. Trust me, this works.
                    Double lakeLon = Double.parseDouble(tempLat);
                    Double lakeLat = Double.parseDouble(tempLon);
                    lakeLatLng = new LatLng(lakeLat, lakeLon);
                Log.d("lakeLat", lakeLat.toString());
                Log.d("lakeLon", lakeLon.toString());

                //Update camera to searched lake
                nMap.moveCamera(CameraUpdateFactory.newLatLng(lakeLatLng));
                nMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                nMap.addMarker(new MarkerOptions().position(new LatLng(lakeLat, lakeLon))
                        .title("Current Lake").snippet(lakeName));
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            //Create dialog to allow user to choose to go to advisory activity
            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setTitle("Lake Information");
            alertDialog.setMessage("Would you like to see more information about this lake? By clicking yes, you will be brought to a new page.");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MapsActivity.this, AdvisoryActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }
}
