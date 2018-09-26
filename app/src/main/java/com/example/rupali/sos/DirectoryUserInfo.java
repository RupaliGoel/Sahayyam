package com.example.rupali.sos;

//Post to show onclick list items

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

public class DirectoryUserInfo extends AppCompatActivity {

    TextView tvName,tvContact,tvEmail,tvRole;
    String email,name,role,contact;
    Toolbar page_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_user_info);

        page_name = findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("Email");
        role = bundle.getString("Role");
        name = bundle.getString("Name");
        contact = bundle.getString("Contact");
//        double distance = bundle.getDouble("Distance");
//        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        tvName = findViewById(R.id.name);
        tvName.setText(name);
        tvRole = findViewById(R.id.role);
        tvRole.setText(role);
        tvContact = findViewById(R.id.contact);
        tvContact.setText(contact);
        tvEmail = findViewById(R.id.email);
        tvEmail.setText(email);

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
        startActivity(callIntent);
    }
    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
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
}
