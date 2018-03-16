package com.example.gaurk.trackofftest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText searchEditText;
    private WebView searchWebView;
    private ImageView settings_imageview;
    ArrayList<String> scripts_arraylist;
    String url_response = "";
    String url_request = "";
    private FloatingActionButton scripts_fab;
    private RelativeLayout loading_scripts_layout;
    private TextView script_status_textview;

    final static int PERMISSION_CODE = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing UI elements
        scripts_fab = (FloatingActionButton) findViewById(R.id.scripts_fab);
        loading_scripts_layout = (RelativeLayout) findViewById(R.id.loading_scripts_layout);
        loading_scripts_layout.setVisibility(View.GONE);
        searchEditText = (EditText) findViewById(R.id.search_bar_edittext);
        settings_imageview = (ImageView) findViewById(R.id.settingsImageView);
        script_status_textview = (TextView) findViewById(R.id.loading_scripts_textview);
        searchWebView = (WebView) findViewById(R.id.search_webview);
        searchWebView.getSettings().setJavaScriptEnabled(true);

        //Check for lovcation access permission
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
//            requestPermissions(PERMISSIONS, PERMISSION_CODE);
        }

        /*Fab leading to scripts activity
        Disabled by default
        Enabled after scripts are loaded*/
        scripts_fab.hide();

        scripts_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Scripts.class);
                intent.putExtra("url_response", url_response);
                startActivity(intent);
            }
        });

        searchWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                loading_scripts_layout.setVisibility(View.VISIBLE);
                getScripts(url);
            }
        });

        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (view != null) {
                        //Hide soft keyboard after URL is entered
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    String input = searchEditText.getText().toString().trim();
                    if (!input.isEmpty() || input.length() != 0 || !input.equals("")) {
                        //Condition to append HTTP if not already present
                        if (!input.startsWith("http://")) {
                            url_request = "http://" + input;
                        } else {
                            url_request = searchEditText.getText().toString();
                        }
                        searchWebView.loadUrl(url_request);
                        searchEditText.setText(url_request);
                        loading_scripts_layout.setVisibility(View.VISIBLE);
                        script_status_textview.setText("Loading page...");
                        scripts_fab.hide();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Input", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        //Open settings activity
        settings_imageview.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Settings_activity.class));
            }
        });
    }

    //Get scripts after the page has been loaded
    private void getScripts(String url_request) {
        RequestQueue queue = Volley.newRequestQueue(this);
        script_status_textview.setText("Loading scripts...");
        scripts_fab.hide();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        url_response = response;
                        scripts_fab.show();
                        loading_scripts_layout.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                url_response = "";
                scripts_fab.hide();
                loading_scripts_layout.setVisibility(View.GONE);

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {

        if (searchWebView.canGoBack()) {
            searchWebView.goBack();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isPermissionGranted() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            return false;
        }
    }
}
