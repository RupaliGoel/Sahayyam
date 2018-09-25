package com.example.rupali.sos;

//Post to show onclick list items

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmergencyPost extends AppCompatActivity {

    TextView titlePost, descriptionPost;
    ImageView imagePost;
    Toolbar page_name;

    TextView tvName,tvContact,tvEmail,tvRole;

    JSONParser jsonParser = new JSONParser();
    private static String url_user_details = "https://sahayyam.000webhostapp.com/get_user_details.php";
    String email,name,role,contact;
    int success = 0;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_post);

        page_name = findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String headline = bundle.getString("Headline");
        String content = bundle.getString("Content");
        double distance = bundle.getDouble("Distance");
        email = bundle.getString("Email");
//        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        titlePost = findViewById(R.id.titlePost);
        titlePost.setText(headline);

        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);

        new GetDetails().execute();
    }

    /**
     * Background Async Task to get username
     * */
    class GetDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * user details
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", email));
                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_user_details, "POST", params);

                JSONArray values = json.getJSONArray("user");

                JSONObject details = values.getJSONObject(0);
                name = details.getString("name");
                role = details.getString("role");
                contact = details.getString("contact");
                // check log cat fro response
                Log.d("Create Response", json.toString());

            } catch (Exception e) {
                System.out.print(e);
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            // dismiss the progressbar once done
            tvName = findViewById(R.id.name);
            tvName.setText(name);
            tvRole = findViewById(R.id.role);
            tvRole.setText(role);
            tvContact = findViewById(R.id.contact);
            tvContact.setText(contact);
            tvEmail = findViewById(R.id.email);
            tvEmail.setText(email);
        }
    }
}
