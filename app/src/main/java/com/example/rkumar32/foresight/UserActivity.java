package com.example.rkumar32.foresight;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rkumar32.foresight.utility.Contributor;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserActivity extends AppCompatActivity {

    private TextView text_email;
    private TextView text_level;
    private TextView text_points;
    private ImageView image_medal;
    private String email = "";
    private Button button_signout;

    private DatabaseReference mDatabase;
    private String userID;

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

        text_email = (TextView) findViewById(R.id.text_email);
        text_points = (TextView) findViewById(R.id.text_points);
        button_signout = (Button) findViewById(R.id.button_signout);
        text_level = (TextView) findViewById(R.id.text_level);
        image_medal= (ImageView)findViewById(R.id.image_medal);

        mDatabase.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Contributor person = snapshot.getValue(Contributor.class);
                String uri = "";
                int points = (person.points);
                int level = points/10;
                text_points.setText(Integer.toString(points));
                text_level.setText(Integer.toString(level));
                text_email.setText(person.email);
                if(level>10)
                    uri="@mipmap/gold_medal";
                else if(level>5)
                    uri="@mipmap/silver_medal";
                else
                    uri="@mipmap/bronze_medal";
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                image_medal.setImageDrawable(res);
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

