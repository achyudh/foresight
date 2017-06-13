package com.example.rkumar32.foresight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.rkumar32.foresight.utility.PlaceWrapper;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        PlaceWrapper placeWrapper = intent.getParcelableExtra(MainActivity.PLACE_KEY);
        ((TextView) findViewById(R.id.place_id)).setText("Place ID: " + placeWrapper.getId());
        ((TextView) findViewById(R.id.name)).setText(placeWrapper.getName());
        ((TextView) findViewById(R.id.rating)).setText("Rating: " + placeWrapper.getRating() + "");
    }
}
