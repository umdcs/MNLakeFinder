package edu.umn.coxxx549d.epa_fish_advisory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_drawer);
    }


    public void addEvent(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Calendar Event");

    // Set up the input
        final EditText input = new EditText(this);
    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.show();

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 0, 19, 7, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 0, 19, 8, 30);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Yoga")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");

        startActivity(intent);
        System.out.println("Hey");
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
        else if(button_text.equals("Share")) {
            Intent intent = new Intent(this, ShareActivity.class);
            startActivity(intent);
        }
    }
}
