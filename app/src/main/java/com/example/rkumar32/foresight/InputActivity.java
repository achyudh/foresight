package com.example.rkumar32.foresight;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.rkumar32.foresight.utility.ImageLoadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import static com.example.rkumar32.foresight.R.id.imageView;

public class InputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView profile_pic = (ImageView) findViewById(R.id.profile_imageview);
        String photoUrl = user.getPhotoUrl().toString();
        new ImageLoadTask(photoUrl, profile_pic).execute();

    }
}
