package com.example.rkumar32.foresight;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rkumar32.foresight.utility.Contributor;
import com.example.rkumar32.foresight.utility.PlaceWrapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final int PLACE_PICKER_REQUEST = 1;
    public static final int RC_SIGN_IN = 2;
    public static final String PLACE_KEY = "place";
    public static final String CONT_KEY = "cont";
    public static final String LOG_TAG = MainActivity.class.getCanonicalName();
    public GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button mButton, uButton;
    private Contributor contributorDB;
    private PlaceWrapper placeWrapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().hasExtra(CONT_KEY)) {
            contributorDB = getIntent().getParcelableExtra(CONT_KEY);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        mButton = (Button) findViewById(R.id.button_picker);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // This launches the place picker
                        try {
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                        } catch (Exception e) {

                        }

                    }
                });
        uButton = (Button) findViewById(R.id.button_user);
        uButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(intent);
                    }
                });
        if(contributorDB == null)
            signIn();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mButton.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, DetailActivity.class);
                placeWrapper = new PlaceWrapper(place);
                intent.putExtra(PLACE_KEY, placeWrapper);
                intent.putExtra(CONT_KEY, contributorDB);
                startActivity(intent);
            }
        }
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                Log.d(LOG_TAG, result.getStatus().toString());
            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        int pointsToIncr = 1;
                                        contributorDB = snapshot.getValue(Contributor.class);
                                        if (contributorDB == null) {
                                            contributorDB = new Contributor(user.getDisplayName(), user.getEmail(), user.getUid(), 0, 0, 0, 0, 0, 0);
                                            mDatabase.child("users").child(contributorDB.uid).setValue(contributorDB);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError firebaseError) {
                                        System.out.println("Firebase place read failed: " + firebaseError.getMessage());
                                    }
                                });
                                
                            }
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.CONT_KEY, contributorDB);
        outState.putParcelable(MainActivity.PLACE_KEY, placeWrapper);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        placeWrapper = savedInstanceState.getParcelable(MainActivity.PLACE_KEY);
        contributorDB = savedInstanceState.getParcelable(MainActivity.CONT_KEY);
    }
}
