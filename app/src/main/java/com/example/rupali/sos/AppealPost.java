package com.example.rupali.sos;

//Post to show onclick list items

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

public class AppealPost extends AppCompatActivity {

    TextView typePost, descriptionPost;
    ImageView imagePost;
    Toolbar page_name;

    TextView tvName,tvContact,tvEmail,tvRole;

    String email,name,role,contact;
    JSONParser jsonParser = new JSONParser();
    private static String url_user_details = "https://sahayyam.000webhostapp.com/get_user_details.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeal_post);

        page_name = findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("Type");
        String content = bundle.getString("Content");
        email = bundle.getString("Email");

        new GetDetails().execute();


//        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        typePost = findViewById(R.id.titlePost);
        typePost.setText(type);
        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);

        ImageView call = (ImageView) findViewById(R.id.callButton);

            call.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View arg0)
                {
                    if(isPermissionGranted()){
                        call_action();
                    }
                }
            });

    }

    public void call_action(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        String num= tvContact.getText().toString();
        String uri= "tel:"+ num.trim();
        callIntent.setData(Uri.parse(uri));
        try {
            startActivity(callIntent);
        }
        catch (SecurityException e) {
            Toast.makeText(getApplicationContext(),"Calling Permission Required.",Toast.LENGTH_LONG).show();
        }
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
         * Login user
         */
        protected String doInBackground(String... args) {


            try
            {
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

            }
            catch(Exception e)
            {
                System.out.print(e);
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            // dismiss the progressbar once done
            System.out.println("Fetched Details = "+name+" "+role+" "+contact);
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
