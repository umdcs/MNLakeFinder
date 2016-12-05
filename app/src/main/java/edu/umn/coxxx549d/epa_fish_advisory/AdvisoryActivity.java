package edu.umn.coxxx549d.epa_fish_advisory;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.icu.lang.UCharacter.toUpperCase;

public class AdvisoryActivity extends AppCompatActivity {

    public final static String EXTRA_NAME = "edu.umn.coxxx549d.epa_fish_advisory.NAME";
    public final static String EXTRA_ID = "edu.umn.coxxx549d.epa_fish_advisory.ID";
    EditText lakeSearch;
    TextView info;
    TextView lake;
    String id;
    // API by name
    static final String API_URL = "HTTP://services.dnr.state.mn.us/api/lakefinder/by_name/v1?name=";
    //API by lat,long
//    static final String API_URL = "http://services.dnr.state.mn.us/api/lakefinder/by_point/v1?lat=";
//    static final String API_URL2 = "&lon=";
    // API by id
//    static final String API_URL = "http://services.dnr.state.mn.us/api/lakefinder/by_id/v1?id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory);

        info = (TextView) findViewById(R.id.info);
        lakeSearch = (EditText) findViewById(R.id.lakeSearch);
        lake = (TextView) findViewById(R.id.lake);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }

    public void track(View view) {
        Intent intent = new Intent(this, ConsumptionActivity.class);
        startActivity(intent);
    }

    public void goToMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        String lakeName = lakeSearch.getText().toString();
        intent.putExtra(EXTRA_NAME, lakeName);

        startActivity(intent);
    }

    public void viewWeb(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(EXTRA_ID, id);

        startActivity(intent);
    }
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;


        String lakeName = lakeSearch.getText().toString();

        protected void onPreExecute() {
            info.setText("");
        }

        protected String doInBackground(Void... urls) {

            // Do some validation here

            try {
                URL url = new URL(API_URL + lakeName);
             //   URL url = new URL (API_URL + lat + API_URL2 + lon);

                Log.i("URL", url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    Log.i("URL", url.toString());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
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
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);
            lake.setText(toUpperCase(lakeSearch.getText().toString()));

            // TODO: check this.exception
            // TODO: do something with the feed

            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONArray results = jsonObj.getJSONArray("results");


                for (int i = 0; i < results.length(); i++) {
                    JSONObject c = results.getJSONObject(i);

                    String fish = c.getString("fishSpecies");
                    fish = fish.replaceAll("\""," ");
                    fish = fish.substring(1, fish.length() - 1);

                    Log.i("fish", fish);
                    info.setText(fish);

                    JSONObject resources = c.getJSONObject("resources");
                    String fca = resources.getString("fca");
                    id = c.getString("id");
                    Log.i("id", id);
                    Log.i("fca", fca);
                    if(fca == "1") {
                        AlertDialog alertDialog = new AlertDialog.Builder(AdvisoryActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("There is a fish consumption advisory for this lake.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                        Button fcaButton = (Button) findViewById(R.id.fcaButton);
                        fcaButton.setVisibility(View.VISIBLE);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


//{"status":"OK","results":[{"resources":{"fca":1,"lakeSurvey":1,"specialFishingRegs":1,"waterQuality":1,"waterLevels":1,"lakeMap":1,"waterAccess":1,"fishStocking":1},"fishSpecies":["black crappie","bluegill","brown bullhead","hybrid sunfish","largemouth bass","northern pike","pumpkinseed","rock bass","smallmouth bass","sunfish","tullibee (cisco)","walleye","yellow bullhead","yellow perch","bowfin (dogfish)","white sucker","banded killifish","blackchin shiner","blacknose shiner","bluntnose minnow","brook stickleback","central mudminnow","common shiner","golden shiner","Iowa darter","Johnny darter","mimic shiner","mottled sculpin"],"specialFishingRegs":[{"regs":[{"text":"All from 24-36\" must be immediately released. One over 36\" allowed in possession.","species":["Northern Pike"]}],"location":"","locDisplayType":1}],"mapid":["B0025"],"name":"Beltrami","border":"","apr_ids":["1738"],"point":{"epsg:26915":[363550,5272840],"epsg:4326":[-94.815049,47.594603]},"bbox":{"epsg:26915":[361182,5271744,365918,5273936],"epsg:4326":[-94.846878,47.584245,-94.78323,47.604953]},"notes":"","nearest_town":"Bemidji","invasiveSpecies":[],"id":"04013500","county":"Beltrami","pca_id":"04-0135-00"}],"message":""}
//["black bullhead","black crappie","bluegill","brown bullhead","crappie","green sunfish","hybrid sunfish","largemouth bass","northern pike","pumpkinseed","smallmouth bass","walleye","white crappie","yellow bullhead","yellow perch","bowfin (dogfish)","common carp","greater redhorse","redhorse","shorthead redhorse","silver redhorse","white sucker","bluntnose minnow","common shiner","creek chub","fathead minnow","golden shiner","Johnny darter","logperch","spottail shiner","trout-perch"]