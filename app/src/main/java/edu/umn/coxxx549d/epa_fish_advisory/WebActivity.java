package edu.umn.coxxx549d.epa_fish_advisory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    private WebView wv1;
    public final static String EXTRA_ID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String id = intent.getStringExtra(AdvisoryActivity.EXTRA_ID);

        wv1 = (WebView) findViewById(R.id.webView);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.loadUrl("https://maps1.dnr.state.mn.us/lakefinder/mobile/#content/"+id+"/fca");

    }

    public void back(View view) {
        Intent intent = new Intent(this, AdvisoryActivity.class);
        startActivity(intent);
    }

}
