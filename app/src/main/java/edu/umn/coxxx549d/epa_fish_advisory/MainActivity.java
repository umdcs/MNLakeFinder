package edu.umn.coxxx549d.epa_fish_advisory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_drawer);
    }

    /**
     * Function to handle button clicks
     * in the main menu
     * @param view
     */
    public void onClick(View view) {
        String button_text = ((Button) view).getText().toString();
        if(button_text.equals("Lake Map")) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        else if(button_text.equals("Current Advisories")) {
            Intent intent = new Intent(this, AdvisoryActivity.class);
            startActivity(intent);
        }
        else if(button_text.equals("Consumption History")) {
            Intent intent = new Intent(this, ConsumptionActivity.class);
            startActivity(intent);
        }
    }
}
