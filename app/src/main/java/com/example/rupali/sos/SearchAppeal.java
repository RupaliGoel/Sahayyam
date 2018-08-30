package com.example.rupali.sos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;

public class SearchAppeal extends AppCompatActivity {

    ImageButton bt1,bt2;
    Toolbar page_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appeal);
        bt1 = findViewById(R.id.adopt);
        bt2 = findViewById(R.id.donate);

        page_name = (Toolbar) findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
