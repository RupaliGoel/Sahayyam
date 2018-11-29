package com.example.rupali.sos;

//Post to show onclick list items

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;
import java.util.Locale;

public class EmergencyPost extends AppCompatActivity {

    TextView titlePost, descriptionPost;
    ImageView imagePost;
    ImageButton direction;
    Button details;
    Toolbar page_name;
    String imageURI,emer_address,email;
    double emer_lat,emer_long;
    int success = 0;
    String CurrentAddress ;
    double searchlat,searchlong;

    ImageLoader imageLoader = ImageLoader.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_post);

        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));

        page_name = findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String headline = bundle.getString("Headline");
        String content = bundle.getString("Content");
        double distance = bundle.getDouble("Distance");
        emer_lat = bundle.getDouble("Emer_lat");
        emer_long = bundle.getDouble("Emer_long");
        emer_address = bundle.getString("Emer_address");
        imageURI = bundle.getString("ImageURI");
        email = bundle.getString("Email");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EmergencyPost.this);
        CurrentAddress = prefs.getString("user_current_address","");
        convertAddress(CurrentAddress);

       direction = findViewById(R.id.imageButton);
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String lat=Double.toString(emer_lat);
            String lon=Double.toString(emer_long);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+searchlat+","+searchlong+"&daddr="+lat+","+lon));startActivity(intent);
            }
        });

        titlePost = findViewById(R.id.titlePost);
        titlePost.setText(headline);

        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);

        imagePost = findViewById(R.id.imagePost);
        if(!imageURI.equals(""))
            imageLoader.displayImage(imageURI, imagePost);
        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyPost.this,FullImage.class);
                if(!imageURI.equals("")) {
                    intent.putExtra("image", imageURI);
                    startActivity(intent);
                }
            }
        });

        details = findViewById(R.id.detailsButton);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyPost.this, ProfileDetails.class);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });



    }
    public void convertAddress(String address) {
        Geocoder geocoder = new Geocoder(EmergencyPost.this, Locale.getDefault());
        if (address != null && !address.isEmpty()) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    searchlat = addressList.get(0).getLatitude();
                    searchlong = addressList.get(0).getLongitude();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    }
}


