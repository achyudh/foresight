package com.example.rkumar32.foresight;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

class HeavyLift extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {


        //decleration of variables


        HttpClient httpclient = new DefaultHttpClient();
        String azure_string = "";
        String TARGET_URL =
                "https://vision.googleapis.com/v1/images:annotate?";
        String API_KEY =
                "key=AIzaSyAXpb4GZ-o_VCieV_BRhL_bF7KFB7ImbqI";


        String google_string = "";

        //decleratiopn ends


        //calling azure
        try {
            URIBuilder builder = new URIBuilder("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze");
            builder.setParameter("visualFeatures", "Categories");
            builder.setParameter("details", "Celebrities");
            builder.setParameter("language", "en");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "c2019389d5424437b8bff0c696d8547b");

            StringEntity reqEntity = new StringEntity("{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/Satya_Nadella.jpg/250px-Satya_Nadella.jpg\"}");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();


            if (entity != null)

            {
                azure_string = EntityUtils.toString(entity);
                Log.w("hello", azure_string);

            } else {
                Log.w("hello2", "world2");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


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
                            + "}], \"image\": {\"source\": { \"imageUri\":"
                            + " \"https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/MUTCD_D9-6.svg/1200px-MUTCD_D9-6.svg.png\"}}}]}");
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

        LinkedList<String> keyWords = new LinkedList<String>();
        keyWords.add("ramp");
        keyWords.add("Disability");
        keyWords.add("Wheelchair");
        keyWords.add("Elevator");


        int ramp_count = 0;
        int disability_count = 0;
        int wheelchair_count = 0;
        int elevator_count = 0;

        Iterator<String> it = keyWords.iterator();

        while (it.hasNext()) {
            String sub = it.next().toLowerCase();
            Log.w("trunk1", sub);
            if (azure_string.toLowerCase().contains(sub)) {
                Log.w("Inside azure", "Inside");
                if (sub == "ramp") {
                    ramp_count++;
                } else if (sub == "disability") {
                    disability_count++;
                } else if (sub == "wheelchair") {
                    wheelchair_count++;

                } else if (sub == "elevator") {
                    elevator_count++;
                }
            }
            if (google_string.toLowerCase().contains(sub)) {
                Log.w("Inside Google", "Inside");
                if (sub.equals("ramp")) {
                    ramp_count++;
                } else if (sub.equals("disability")) {
                    disability_count++;
                } else if (sub.equals("wheelchair")) {
                    wheelchair_count++;

                } else if (sub == "elevator") {
                    elevator_count++;
                }
            }
        }

        Log.w("ramp_count", "" + ramp_count);
        Log.w("disability_count", "" + disability_count);
        Log.w("wheelchair", "" + wheelchair_count);
        Log.w("elevator", "" + elevator_count);
        Log.w("ending here", "end");

        // parsing done


        return null;
    }
}