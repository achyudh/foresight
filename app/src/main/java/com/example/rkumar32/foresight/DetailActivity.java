package com.example.rkumar32.foresight;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rkumar32.foresight.utility.Contributor;
import com.example.rkumar32.foresight.utility.PlaceReview;
import com.example.rkumar32.foresight.utility.PlaceWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

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
    private Contributor contributorDB;
    private PlaceWrapper placeWrapper;
    private HashMap<String, Integer > map=new HashMap<String,Integer>();
    private Bitmap mBitmap;

    //rishabh photo part
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayout addPhotoLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    //ends here


    //some other globals from prebvious file
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private static final String CLOUD_VISION_API_KEY = "AIzaSyAXpb4GZ-o_VCieV_BRhL_bF7KFB7ImbqI";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    //ends here

    private String LOG_TAG = DetailActivity.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        final Intent intent = getIntent();

        placeWrapper = intent.getParcelableExtra(MainActivity.PLACE_KEY);
        final String placeId = placeWrapper.id;
        if (!placeWrapper.name.equals(""))
            getSupportActionBar().setTitle(placeWrapper.name);
        contributorDB = intent.getParcelableExtra(MainActivity.CONT_KEY);

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
                        inputIntent.putExtra(MainActivity.CONT_KEY, contributorDB);
                        startActivity(inputIntent);
                    }
                }
        );

        mDatabase.child("places").child(placeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                PlaceWrapper placeWrapperDB = snapshot.getValue(PlaceWrapper.class);
                if (placeWrapperDB == null) {
//                        placeWrapper.rating = -1;
                    mDatabase.child("places").child(placeId).setValue(placeWrapper);
                    placeWrapperDB = placeWrapper;
                }
                Random rand = new Random();
                double f = rand.nextFloat();
                f = Math.round(f * 10.0)/10.0;
                ratingTextView.setText(3 + f * 2 + "");
                ((RatingBar) findViewById(R.id.rating_bar_current)).setRating((float) (3 + f * 2));

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

        if (placeWrapper.url != null)
            websiteTextView.setText(placeWrapper.url.toString());

        final List<PlaceReview> reviewData = new ArrayList<>();
        Query reviewsQuery = mDatabase.child("reviews").child(placeId);
        reviewsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the display of reviews
                    PlaceReview reviewObj = postSnapshot.getValue(PlaceReview.class);
                    reviewData.add(reviewObj);
                }
                RecyclerView listView = (RecyclerView) findViewById(R.id.review_listview);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.VERTICAL, false);
                listView.setLayoutManager(layoutManager);
                ReviewAdapter adapter = new ReviewAdapter(reviewData, DetailActivity.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FIreDB Reviews", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });


        /*insering rishabhs part of creating photos here from line 139*/

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<String> myDataset = new ArrayList<>();
        myDataset.add("http://c0.thejournal.ie/media/2014/10/generic-health-pics-generic-health-pics-2-390x285.jpg");
        myDataset.add("http://mediad.publicbroadcasting.net/p/wvik/files/201412/disabilityparking.jpg");
        Log.d(LOG_TAG, myDataset.size() + "");
        mAdapter = new MyAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        addPhotoLayout = (LinearLayout) findViewById(R.id.add_photo_layout);
        addPhotoLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                        builder
                                .setMessage(R.string.dialog_select_prompt)
                                .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(LOG_TAG, "Gallery Chooser clicked");
                                        startGalleryChooser();
                                    }
                                })
                                .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startCamera();
                                    }
                                });
                        builder.create().show();

                    }
                }
        );








//        new HeavyLift().execute();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/place/details/json?placeid="+ placeWrapper.id + "&key=AIzaSyDX6lEQZCGZ4io-K4GSpnQ1zJCJ-bLNw6o";

// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        Log.d(LOG_TAG, response);
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response);
//                            JSONArray photos = jsonResponse.getJSONObject("result").getJSONArray("photos");
//                            String photoReference = photos.getJSONObject(0).getString("photo_reference");
//                            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
//                                + photoReference + "&key=AIzaSyDX6lEQZCGZ4io-K4GSpnQ1zJCJ-bLNw6o";
//                        } catch (Exception e) {
//                            Log.d(LOG_TAG, e.toString());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(LOG_TAG, "Volley Error");
//            }
//        });
//
//// Add the request to the RequestQueue.
//        queue.add(stringRequest);
    }

    //copied from file from line 231

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Log.d(LOG_TAG, "Gallery returned data");
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.PLACE_KEY, placeWrapper);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        placeWrapper = savedInstanceState.getParcelable(MainActivity.PLACE_KEY);
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Log.d(LOG_TAG, "Gallery Chooser Method called");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                Log.d(LOG_TAG, "Upload Started");
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);
                mBitmap = bitmap;

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d(LOG_TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(LOG_TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        Log.d(LOG_TAG, base64EncodedImage.getContent());
                        annotateImageRequest.setImage(base64EncodedImage);
                        LinkedList<String> ll = new LinkedList<String>();
                        ll.add(base64EncodedImage.getContent());
                        new HeavyLift().execute(ll);
                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("SAFE_SEARCH_DETECTION");
                            labelDetection.setMaxResults(30);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

//                    Vision.Images.Annotate annotateRequest =
//                            vision.images().annotate(batchAnnotateImagesRequest);
//                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
//                    annotateRequest.setDisableGZipContent(true);
//                    Log.d(LOG_TAG, "created Cloud Vision request object, sending request");
//
//                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return "";

                } catch (Exception e) {
                    Log.d(LOG_TAG, "failed to make API request because " + e.toString());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                Log.d(LOG_TAG, result);
            }
        }.execute();
    }


    //copying till line 412


    //from line 412
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }

    //copying till line 446





//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable(MainActivity.CONT_KEY, contributorDB);
//        outState.putParcelable(MainActivity.PLACE_KEY, placeWrapper);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        placeWrapper = savedInstanceState.getParcelable(MainActivity.PLACE_KEY);
//        contributorDB = savedInstanceState.getParcelable(MainActivity.CONT_KEY);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.CONT_KEY, contributorDB);
        intent.putExtra(MainActivity.PLACE_KEY, placeWrapper);
    }


    class HeavyLift extends AsyncTask<LinkedList<String>, Void, HashMap<String,Integer>> {

        int ramp_count = 0;
        int disability_count = 0;
        int wheelchair_count = 0;
        int elevator_count = 0;
        int toilet_count=0;
        int parking_count=0;
        LinkedList<String> keyWords = new LinkedList<String>();

        String TARGET_URL =
                "https://vision.googleapis.com/v1/images:annotate?";
        String API_KEY =
                "key=AIzaSyAXpb4GZ-o_VCieV_BRhL_bF7KFB7ImbqI";

        @Override
        protected HashMap<String, Integer> doInBackground(LinkedList<String>... linkedLists) {
            keyWords.add("ramp");
            keyWords.add("Disability");
            keyWords.add("Wheelchair");
            keyWords.add("Elevator");
            keyWords.add("toilet");
            keyWords.add("parking");

            LinkedList<String> photoUrl = linkedLists[0];
            Iterator<String> photoIt = photoUrl.iterator();
            while (photoIt.hasNext()) {

                Log.w("Fir Ghusa", "Haan Ghusa");
                HttpClient httpclient = new DefaultHttpClient();
                String nextURL = photoIt.next();
                String azure_string = "";
                String google_string = "";

                //decleratiopn ends


                //calling azure
//                try {
//                    URIBuilder builder = new URIBuilder("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze");
//                    builder.setParameter("visualFeatures", "Categories");
//                    builder.setParameter("details", "Celebrities");
//                    builder.setParameter("language", "en");
//
//                    URI uri = builder.build();
//                    HttpPost request = new HttpPost(uri);
//
//                    request.setHeader("Content-Type", "application/json");
//                    request.setHeader("Ocp-Apim-Subscription-Key", "c2019389d5424437b8bff0c696d8547b");
//
//                    StringEntity reqEntity = new StringEntity("{\"url\":\""+nextURL+"\"}");
//                    request.setEntity(reqEntity);
//
//                    HttpResponse response = httpclient.execute(request);
//                    HttpEntity entity = response.getEntity();
//
//
//                    if (entity != null)
//
//                    {
//                        azure_string = EntityUtils.toString(entity);
//                        Log.w("hello", azure_string);
//
//                    } else {
//                        Log.w("hello2", "world2");
//                    }
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }


                //azure ends


                //calling vision


                try {

                    URL serverUrl = new URL(TARGET_URL + API_KEY);
                    URLConnection urlConnection = serverUrl.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

                    httpConnection.setRequestMethod("POST");
                    httpConnection.setRequestProperty("Content-Type", "application/json");

                    httpConnection.setDoOutput(true);

                    BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                            OutputStreamWriter(httpConnection.getOutputStream()));
                    httpRequestBodyWriter.write
                            ("{\"requests\":  [{ \"features\":  [ {\"type\": \"WEB_DETECTION\""
                                    + "}], \"image\": {\"content\": \"" + nextURL + "\"}}]}");
                    Log.d(LOG_TAG, "{\"requests\":  [{ \"features\":  [ {\"type\": \"WEB_DETECTION\""
                            + "}], \"image\": {\"content\": \"" + "abc" + "\"}}]}");
                    httpRequestBodyWriter.close();

                    String response2 = httpConnection.getResponseMessage();

                    Scanner httpResponseScanner = new Scanner(httpConnection.getInputStream());

                    while (httpResponseScanner.hasNext()) {
                        String line = httpResponseScanner.nextLine();
                        google_string += line;
                        //  alternatively, print the line of response
                    }
                    Log.w("Google Stuff", google_string);
                    httpResponseScanner.close();
                } catch (Exception e) {
                    Log.w("error google", e.getMessage());
                }

                //vision ends


                //parsing the strings


                Iterator<String> it = keyWords.iterator();
                LinkedList<String> l1 = new LinkedList<String>();
                LinkedList<String> l2 = new LinkedList<String>();
                LinkedList<String> l3 = new LinkedList<String>();
                LinkedList<String> l4 = new LinkedList<String>();
                while (it.hasNext()) {
                    String sub = it.next().toLowerCase();
                    Log.w("trunk1", sub);
                    int flag_1 = 0, flag_2 = 0, flag_3 = 0, flag_4 = 0;
                    if (azure_string.toLowerCase().contains(sub)) {
                        Log.w("Inside azure", "Inside");
                        if (sub == "ramp") {
                            flag_1 = 1;
                            ramp_count++;
                        } else if (sub == "disability") {
                            flag_2 = 1;
                            disability_count++;
                        } else if (sub == "wheelchair") {
                            flag_3 = 1;
                            wheelchair_count++;

                        } else if (sub == "elevator") {
                            flag_4 = 1;
                            elevator_count++;
                        }
                    }
                    if (google_string.toLowerCase().contains(sub)) {
                        Log.w("Inside Google", "Inside");
                        if (sub.equals("ramp")) {
                            flag_1 = 1;
                            ramp_count++;
                        } else if (sub.equals("disability")) {
                            flag_2 = 1;
                            disability_count++;
                        } else if (sub.equals("wheelchair")) {
                            flag_3 = 1;
                            wheelchair_count++;

                        } else if (sub.equals("elevator")) {
                            flag_4 = 1;
                            elevator_count++;
                        }
                        else if(sub.equals("toilet")){
                            toilet_count++;
                        }
                        else if(sub.equals("parking")) {
                            parking_count++;
                        }
                    }
                    if (flag_1 > 0) {
                        l1.add(nextURL);
                        map.put("ramp_count", ramp_count);
                    }
                    if (flag_2 > 0) {
                        l2.add(nextURL);
                        map.put("disability", disability_count);
                    }
                    if (flag_3 > 0) {
                        l3.add(nextURL);
                        map.put("wheelchair", wheelchair_count);
                    }
                    if (flag_4 > 0) {
                        l4.add(nextURL);
                        map.put("elelvator", elevator_count);
                    }

                    Log.d(LOG_TAG, ramp_count + "");
                    Log.d(LOG_TAG, disability_count + "");
                    Log.d(LOG_TAG, wheelchair_count + "");
                    Log.d(LOG_TAG, elevator_count + "");
                    Log.d(LOG_TAG, toilet_count + "");
                    Log.d(LOG_TAG, parking_count + "");

                }
                map.put("toilet",toilet_count);
                map.put("parking", parking_count);

            }


            // parsing done


            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, Integer> stringIntegerHashMap) {
            super.onPostExecute(stringIntegerHashMap);
            List<String > markers = new ArrayList<>();
            boolean hasRamp = false, hasElevator = false, hasToilet = false, hasParking = false;
            if (ramp_count > 0) {
                markers.add("Entrance");
                hasRamp = true;
            }
            if (elevator_count > 0) {
                markers.add("Elevator");
                hasElevator = true;
            }
            if ((disability_count > 0 || wheelchair_count > 0) && toilet_count > 0) {
                markers.add("Special Restrooms");
                hasToilet = true;
            }
            if ((disability_count > 0 || wheelchair_count > 0) && parking_count > 0) {
                markers.add("Parking Space");
                hasParking = true;
            }
            final boolean hasRamp1 = hasRamp;
            final boolean hasElevator1 = hasElevator;
            final boolean hasToilet1 = hasToilet;
            final boolean hasParking1 = hasParking;
            final Bitmap mBitmap1 = mBitmap;
            final String placeId = placeWrapper.id;

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.alertbox, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Features Detected");
            if (hasElevator || hasParking || hasRamp || hasToilet || hasRamp) {
                alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO : Add the markers and upload Bitmap mBitmap
                        if (hasRamp1) {
                            mDatabase.child("places").child(placeId).child("hasRampEntrance").setValue(2);
                        }
                        if (hasElevator1) {
                            mDatabase.child("places").child(placeId).child("hasElevator").setValue(2);
                        }
                        if (hasParking1) {
                            mDatabase.child("places").child(placeId).child("hasParking").setValue(2);
                        }
                        if (hasToilet1) {
                            mDatabase.child("places").child(placeId).child("hasRestroom").setValue(2);
                        }
                        //Upload mbitmap
                        String time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_S").format(new Date());
                        StorageReference mountainsRef = storageRef.child("images/"+ placeId + "/" + time +".jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = mountainsRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        });
                    }
                });
            }
            ListView lv = (ListView) convertView.findViewById(R.id.listView1);
            lv.setEmptyView((TextView) convertView.findViewById(R.id.empty_view));
            ListAdapter adapter = new ListAdapter(DetailActivity.this,android.R.layout.simple_list_item_1, markers);
            lv.setAdapter(adapter);
            alertDialog.show();
        }
    }
}

