package com.example.rkumar32.foresight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rkumar32.foresight.utility.ImageLoadTask;
import com.example.rkumar32.foresight.utility.PlaceWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InputActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView profile_pic = (ImageView) findViewById(R.id.profile_imageview);
        String photoUrl = user.getPhotoUrl().toString();
        new ImageLoadTask(photoUrl, profile_pic).execute();

        final CheckBox entranceCB = (CheckBox) findViewById(R.id.entrance_cb);
        final CheckBox elevatorCB = (CheckBox) findViewById(R.id.elevator_cb);
        final CheckBox restroomCB = (CheckBox) findViewById(R.id.toilet_cb);
        final CheckBox parkingCB = (CheckBox) findViewById(R.id.parking_cb);
        final CheckBox navigationCB = (CheckBox) findViewById(R.id.navigation_cb);
        final EditText editTextReview = (EditText) findViewById(R.id.editTextReview);

        TextView textUsername = (TextView) findViewById(R.id.text_username);
        textUsername.setText(user.getDisplayName());

        Intent intent = getIntent();
        final PlaceWrapper placeWrapper = intent.getParcelableExtra(MainActivity.PLACE_KEY);


        mDatabase = FirebaseDatabase.getInstance().getReference();



        Button mButton = (Button) findViewById(R.id.button_submit);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabase.child("reviews").child(placeWrapper.id).child(user.getUid()).setValue(editTextReview.getText().toString());
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

                    }
                });

    }
}
