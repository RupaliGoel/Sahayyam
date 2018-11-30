package com.example.rupali.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AppealPost extends AppCompatActivity {

    TextView typePost, descriptionPost;
    ImageView imagePost;
    android.widget.Toolbar page_name;
    String email;

    Button details;
    String imageURI;
    ImageLoader imageLoader = ImageLoader.getInstance();
    String toolbarMessage;
    Toolbar toolbar;
    TextView appnametv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeal_post);
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));


        page_name =findViewById(R.id.page_name);
       // setSupportActionBar(page_name);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        toolbarMessage = prefs.getString("AppName","App");

        toolbar = findViewById(R.id.toolbar);
        appnametv = (TextView)findViewById(R.id.appname);
        appnametv.setText(toolbarMessage);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("Type");
        String content = bundle.getString("Content");
        email = bundle.getString("Email");
        imageURI = bundle.getString("Picture");
//        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        typePost = findViewById(R.id.titlePost);
        typePost.setText(type);
        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);
        imagePost = findViewById(R.id.imagePost);
        if(!imageURI.equals(""))
            imageLoader.displayImage(imageURI, imagePost);
        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppealPost.this,FullImage.class);
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
                Intent intent = new Intent(AppealPost.this, ProfileDetails.class);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });
    }
}
