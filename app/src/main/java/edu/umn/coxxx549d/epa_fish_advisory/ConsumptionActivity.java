package edu.umn.coxxx549d.epa_fish_advisory;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;


import java.lang.reflect.Array;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.N)
public class ConsumptionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CalendarView calendar;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    String fishType, lakeName, fishSize;
    Date eventDate;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        // Allows user to select the date of their consumption event on calendar instead of entering
        // date manually.
        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Toast.makeText(getApplicationContext(), formatter.format(date),
                        Toast.LENGTH_SHORT).show();
                eventDate = date;
            }
            @Override
            public void onLongClickDate(Date date, View view) {
                Toast.makeText(getApplicationContext(),
                        "Long click " + formatter.format(date),
                        Toast.LENGTH_SHORT).show();
                eventDate = date;
            }
        };

        caldroidFragment.setCaldroidListener(listener);
    }


    public void addEvent(View view) {
        GridLayout layout = new GridLayout(this);
        layout.setOrientation(GridLayout.VERTICAL);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Calendar Event");
        Button send = new Button(this);
        send.setHint("Send To Calendar");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter Type of Fish");
        final EditText input2 = new EditText(this);
        input2.setHint("Enter The Lake Name");
        final EditText input3 = new EditText(this);
        input3.setHint("Enter Fish Size (lbs)");
        final EditText input4 = new EditText(this);
        input4.setText(eventDate.toString());
        //input4.setHint("Enter Date (mm/dd/yyyy)");
        layout.addView(input);
        layout.addView(input2);
        layout.addView(input3);
        layout.addView(input4);
        layout.addView(send);

        //Send Button saves the inputs and uses them on the calendar intent
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fishType = input.getText().toString();
                lakeName = input2.getText().toString();
                fishSize = input3.getText().toString();
                sendToCalendar(view);
            }
        });

        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input2.setInputType(InputType.TYPE_CLASS_TEXT);
        input3.setInputType(InputType.TYPE_CLASS_TEXT);
        input4.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(layout);
        builder.show();
    }

    public void sendToCalendar(View view) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, eventDate)
                .putExtra(CalendarContract.Events.TITLE, "First Fish")
                .putExtra(CalendarContract.Events.DESCRIPTION, fishType)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, lakeName);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.consumption, menu);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        }
        else if (id == R.id.nav_maps) {//slideshow->consumption
        Intent intent = new Intent(this, MapsActivity.class);
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


}
