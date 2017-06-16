package com.example.rkumar32.foresight;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rkumar32.foresight.utility.Contributor;
import com.example.rkumar32.foresight.utility.ImageLoadTask;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserActivity extends AppCompatActivity {

    private TextView text_username;
    private TextView text_pointsandlevel;
    private TextView text_reviews;
    private TextView text_ratings;
    private TextView text_photos;
    private TextView text_answers;
    private TextView text_edits;
    private ImageView image_medal;
    //private String email = "";
    private Button button_signout;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    public void signout_call(View view) {
        if (view.getId() == R.id.button_signout) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(UserActivity.this, MainActivity.class));
                            finish();
                        }
                    });
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.activity_user);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        text_pointsandlevel = (TextView) findViewById(R.id.text_pointsandlevel);
        button_signout = (Button) findViewById(R.id.button_signout);
        text_username=(TextView) findViewById(R.id.text_username);
        text_reviews=(TextView) findViewById(R.id.text_reviews);
        text_ratings=(TextView) findViewById(R.id.text_ratings);
        text_photos=(TextView) findViewById(R.id.text_photos);
        text_answers=(TextView) findViewById(R.id.text_answers);
        text_edits=(TextView) findViewById(R.id.text_edits);

        progressBar=(ProgressBar) findViewById(R.id.progress_bar);
        ImageView profile_pic = (ImageView) findViewById(R.id.profile_imageview);
        String photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        new ImageLoadTask(photoUrl, profile_pic).execute();
        text_username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        mDatabase.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Contributor person = snapshot.getValue(Contributor.class);
                String uri = "";
                int points = (person.points);
                int level = points/10;
                text_pointsandlevel.setText("Points: " + Integer.toString(points)+ " Level: " + Integer.toString(level));
                progressBar.setProgress(points%10);
                text_edits.setText(Integer.toString(person.edits));
                text_ratings.setText(Integer.toString(person.ratings));
                text_reviews.setText(Integer.toString(person.reviews));
                text_photos.setText(Integer.toString(person.photos));
                text_answers.setText(Integer.toString(person.answers));
                // TODO: Insert medals
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        button_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signout_call(view);
            }
        });

    }

}

