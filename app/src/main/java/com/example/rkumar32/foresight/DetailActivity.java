package com.example.rkumar32.foresight;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rkumar32.foresight.utility.Contributor;
import com.example.rkumar32.foresight.utility.PlaceWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private TextView addressTextView;
    private TextView phoneNumberTextView;
    private TextView websiteTextView;
    private TextView ratingTextView;
    private DatabaseReference mDatabase;
    private ImageView entranceMarker;
    private ImageView elevatorMarker;
    private ImageView toiletMarker;
    private ImageView parkingMarker;
    private ImageView navigationMarker;
    private FloatingActionButton mInputActivityButton;

    private String LOG_TAG = DetailActivity.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference("places");
        Intent intent = getIntent();
        final PlaceWrapper placeWrapper = intent.getParcelableExtra(MainActivity.PLACE_KEY);
        final String placeId = placeWrapper.id;

        addressTextView = (TextView) findViewById(R.id.address_textview);
        phoneNumberTextView = (TextView) findViewById(R.id.phone_textview);
        websiteTextView = (TextView) findViewById(R.id.website_textview);
        addressTextView.setText(placeWrapper.address);
        phoneNumberTextView.setText(PhoneNumberUtils.formatNumber(placeWrapper.phoneNumber));
        entranceMarker = (ImageView) findViewById(R.id.entrance_marker);
        elevatorMarker = (ImageView) findViewById(R.id.elevator_marker);
        toiletMarker = (ImageView) findViewById(R.id.toilet_marker);
        parkingMarker = (ImageView) findViewById(R.id.parking_marker);
        navigationMarker = (ImageView) findViewById(R.id.navigation_marker);
        ratingTextView = (TextView) findViewById(R.id.current_rating_textview);
        mInputActivityButton = (FloatingActionButton) findViewById(R.id.contribute_button);

        mInputActivityButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent inputIntent = new Intent(DetailActivity.this, InputActivity.class);
                        inputIntent.putExtra(MainActivity.PLACE_KEY, placeWrapper);
                        startActivity(inputIntent);
                    }
                }
        );

        mDatabase.child(placeId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    PlaceWrapper placeWrapperDB = snapshot.getValue(PlaceWrapper.class);
                    if (placeWrapperDB == null) {
//                        placeWrapper.rating = -1;
                        mDatabase.child(placeId).setValue(placeWrapper);
                        placeWrapperDB = placeWrapper;
                    }
                    ratingTextView.setText(placeWrapperDB.rating + "");

                    int imageResource;
                    Drawable res;
                    if(placeWrapperDB.hasRampEntrance == 0) {
                        entranceMarker.setImageDrawable(getDrawable(R.drawable.ic_block_black_24dp));
                    }
                    else if(placeWrapperDB.hasRampEntrance == 1) {
                        entranceMarker.setImageDrawable(getDrawable(R.drawable.ic_check_black_24dp));
                    }
                    else {
                        entranceMarker.setImageDrawable(getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
                    }

                    if(placeWrapperDB.hasElevator == 0) {
                        elevatorMarker.setImageDrawable(getDrawable(R.drawable.ic_block_black_24dp));
                    }
                    else if(placeWrapperDB.hasElevator == 1) {
                        elevatorMarker.setImageDrawable(getDrawable(R.drawable.ic_check_black_24dp));
                    }
                    else {
                        elevatorMarker.setImageDrawable(getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
                    }

                    if(placeWrapperDB.hasParking == 0) {
                        parkingMarker.setImageDrawable(getDrawable(R.drawable.ic_block_black_24dp));
                    }
                    else if(placeWrapperDB.hasParking == 1) {
                        parkingMarker.setImageDrawable(getDrawable(R.drawable.ic_check_black_24dp));
                    }
                    else {
                        parkingMarker.setImageDrawable(getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
                    }

                    if(placeWrapperDB.hasRestroom == 0) {
                        toiletMarker.setImageDrawable(getDrawable(R.drawable.ic_block_black_24dp));
                    }
                    else if(placeWrapperDB.hasRestroom == 1) {
                        toiletMarker.setImageDrawable(getDrawable(R.drawable.ic_check_black_24dp));
                    }
                    else {
                        toiletMarker.setImageDrawable(getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
                    }

                    if(placeWrapperDB.hasAccNav == 0) {
                        navigationMarker.setImageDrawable(getDrawable(R.drawable.ic_block_black_24dp));
                    }
                    else if(placeWrapperDB.hasAccNav == 1) {
                        navigationMarker.setImageDrawable(getDrawable(R.drawable.ic_check_black_24dp));
                    }
                    else {
                        navigationMarker.setImageDrawable(getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
                    }

                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    System.out.println("Firebase place read failed: " + firebaseError.getMessage());
                }
            });


        if (placeWrapper.url != null)
            websiteTextView.setText(placeWrapper.url.toString());

//        new HeavyLift().execute();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/place/details/json?placeid="+ placeWrapper.id + "&key=AIzaSyDX6lEQZCGZ4io-K4GSpnQ1zJCJ-bLNw6o";

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


