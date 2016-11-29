package edu.umn.coxxx549d.epa_fish_advisory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    private WebView wv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String id = intent.getStringExtra(AdvisoryActivity.EXTRA_ID);

        wv1=(WebView)findViewById(R.id.webView);

        String url = "https://maps1.dnr.state.mn.us/lakefinder/mobile/#content/" + id + "/summary";

        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setAllowContentAccess(true);
        wv1.getSettings().setAllowFileAccess(true);
        wv1.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.loadUrl(url);
    }

    public void back(View view) {
        Intent intent = new Intent(this, AdvisoryActivity.class);
        startActivity(intent);
    }

}
