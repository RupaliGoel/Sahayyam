package com.example.rupali.sos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class ViewUploads extends AppCompatActivity {
    String email,imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploads);
        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        imageurl = bundle.getString("image");
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        for (int x = 0; x < 10; x++) {
            ImageView image = new ImageView(ViewUploads.this);
            if (imageurl.isEmpty()) {
                image.setImageResource(R.drawable.whiteimageview);
            } else{
                Picasso.with(ViewUploads.this)
                        .load(imageurl).resize(150,150)
                        .noFade().into(image);
            }
            linearLayout1.addView(image);
        }
    }
}
