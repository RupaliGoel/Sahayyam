package com.example.rupali.sos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewPostHistory extends AppCompatActivity {

    TextView titlePost, descriptionPost;
    ImageView imagePost;
    Toolbar page_name;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_history);

        page_name = findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("Title");
        String content = bundle.getString("Content");
        id = bundle.getInt("Id");
        //        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        titlePost = findViewById(R.id.titlePost);
        titlePost.setText(title);
        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);

    }

}

