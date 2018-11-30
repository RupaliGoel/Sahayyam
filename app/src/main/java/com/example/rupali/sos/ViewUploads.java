package com.example.rupali.sos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class ViewUploads extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploads);
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        for (int x = 0; x < 10; x++) {
            ImageView image = new ImageView(ViewUploads.this);
            image.setBackgroundResource(R.drawable.profile);
            linearLayout1.addView(image);
        }
    }
}
