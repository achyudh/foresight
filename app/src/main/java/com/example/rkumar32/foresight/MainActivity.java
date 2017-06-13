package com.example.rkumar32.foresight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.rkumar32.foresight.utility.PlaceWrapper;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {
    public static final int PLACE_PICKER_REQUEST = 1;
    public static final String PLACE_KEY = "place";

    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // This launches the place picker
                        try {
                            PlacePicker.IntentBuilder  builder = new PlacePicker.IntentBuilder();
                            startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                        } catch (Exception e) {

                        }

                    }
                });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mButton.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(PLACE_KEY, new PlaceWrapper(place));
                startActivity(intent);
            }
        }
    }
}
