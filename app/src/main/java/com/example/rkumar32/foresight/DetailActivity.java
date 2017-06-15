package com.example.rkumar32.foresight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rkumar32.foresight.utility.PlaceWrapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private TextView addressTextView;
    private TextView phoneNumberTextView;
    private TextView websiteTextView;
    private TextView ratingTextView;

    private String LOG_TAG = DetailActivity.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        PlaceWrapper placeWrapper = intent.getParcelableExtra(MainActivity.PLACE_KEY);

        addressTextView = (TextView) findViewById(R.id.address_textview);
        phoneNumberTextView = (TextView) findViewById(R.id.phone_textview);
        websiteTextView = (TextView) findViewById(R.id.website_textview);
        ratingTextView = (TextView) findViewById(R.id.current_rating_textview);

        addressTextView.setText(placeWrapper.getAddress());
        phoneNumberTextView.setText(PhoneNumberUtils.formatNumber(placeWrapper.getPhoneNumber()));
        if (placeWrapper.getUrl() != null)
        websiteTextView.setText(placeWrapper.getUrl().toString());
        ratingTextView.setText(placeWrapper.getRating() + "");

        new HeavyLift().execute();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/place/details/json?placeid="+ placeWrapper.getId() + "&key=AIzaSyDX6lEQZCGZ4io-K4GSpnQ1zJCJ-bLNw6o";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(LOG_TAG, response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray photos = jsonResponse.getJSONObject("result").getJSONArray("photos");
                            String photoReference = photos.getJSONObject(0).getString("photo_reference");
                            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + photoReference + "&key=AIzaSyDX6lEQZCGZ4io-K4GSpnQ1zJCJ-bLNw6o";
                        } catch (Exception e) {
                            Log.d(LOG_TAG, e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "Volley Error");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}


