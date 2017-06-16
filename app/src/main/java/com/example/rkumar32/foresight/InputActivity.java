package com.example.rkumar32.foresight;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.rkumar32.foresight.utility.Contributor;
import com.example.rkumar32.foresight.utility.ImageLoadTask;
import com.example.rkumar32.foresight.utility.PlaceReview;
import com.example.rkumar32.foresight.utility.PlaceWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;


public class InputActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Contributor contributor;
    private PlaceWrapper placeWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        ImageView profile_pic = (ImageView) findViewById(R.id.profile_imageview);
        String photoUrl = user.getPhotoUrl().toString();
        new ImageLoadTask(photoUrl, profile_pic).execute();

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar_add_rating);
        final CheckBox entranceCB = (CheckBox) findViewById(R.id.entrance_cb);
        final CheckBox elevatorCB = (CheckBox) findViewById(R.id.elevator_cb);
        final CheckBox restroomCB = (CheckBox) findViewById(R.id.toilet_cb);
        final CheckBox parkingCB = (CheckBox) findViewById(R.id.parking_cb);
        final CheckBox navigationCB = (CheckBox) findViewById(R.id.navigation_cb);
        final EditText editTextReview = (EditText) findViewById(R.id.editTextReview);

        TextView textUsername = (TextView) findViewById(R.id.text_username);
        textUsername.setText(user.getDisplayName());

        Intent intent = getIntent();
        placeWrapper = intent.getParcelableExtra(MainActivity.PLACE_KEY);
        contributor = intent.getParcelableExtra(MainActivity.CONT_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (!placeWrapper.name.equals(""))
            getSupportActionBar().setTitle(placeWrapper.name);

        Button mButton = (Button) findViewById(R.id.button_submit);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pointsToIncr = 1;
                        if (contributor == null) {
                            contributor = new Contributor(user.getDisplayName(), user.getEmail(), user.getUid(), 0, 0, 0, 0, 0, 0);
                        }
                        if (!editTextReview.getText().toString().equals("")) {
                            pointsToIncr += 2;
                            contributor.reviews += 1;
                        }
                        contributor.answers += 1;
                        contributor.points += pointsToIncr;
                        final Contributor contributorDB = contributor;
                        float rating = ratingBar.getRating();
                        PlaceReview reviewObj = new PlaceReview(contributor.name, rating, editTextReview.getText().toString(), date);
                        mDatabase.child("users").child(contributorDB.uid).setValue(contributorDB);
                        if (!editTextReview.getText().toString().equals(""))
                            mDatabase.child("reviews").child(placeWrapper.id).child(user.getUid()).setValue(reviewObj);

                        mDatabase.child("places").child(placeWrapper.id).addValueEventListener(new ValueEventListener() {

                        PlaceWrapper placeWrapperDB;

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            placeWrapperDB = snapshot.getValue(PlaceWrapper.class);
                            if (placeWrapperDB == null) {
//                    placeWrapper.rating = -1;
                                placeWrapperDB = placeWrapper;
                            }
                            if (entranceCB.isChecked())
                                placeWrapperDB.hasRampEntrance = 1;
                            else
                                placeWrapperDB.hasRampEntrance = 0;

                            if (elevatorCB.isChecked())
                                placeWrapperDB.hasElevator = 1;
                            else
                                placeWrapperDB.hasElevator = 0;

                            if (restroomCB.isChecked())
                                placeWrapperDB.hasRestroom = 1;
                            else
                                placeWrapperDB.hasRestroom = 0;

                            if (parkingCB.isChecked())
                                placeWrapperDB.hasParking = 1;
                            else
                                placeWrapperDB.hasParking = 0;

                            if (navigationCB.isChecked())
                                placeWrapperDB.hasAccNav = 1;
                            else
                                placeWrapperDB.hasAccNav = 0;

                            mDatabase.child("places").child(placeWrapperDB.id).setValue(placeWrapperDB);
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            System.out.println("Firebase place read failed: " + firebaseError.getMessage());
                        }
                    });
                        onBackPressed();

                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.CONT_KEY, contributor);
        intent.putExtra(MainActivity.PLACE_KEY, placeWrapper);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
