package com.example.rupali.sos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyProfile extends AppCompatActivity {

    String email,name,role,contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        name = prefs.getString("user_name","Not Found");
        role = prefs.getString("user_role","Not Found");
        contact = prefs.getString("user_contact","Not Found");
        email = prefs.getString("user_email", "Not Found");

        TextView textName = findViewById(R.id.name);
        TextView textRole = findViewById(R.id.role);
        TextView textEmail = findViewById(R.id.email);
        TextView textContact =findViewById(R.id.mobileNumber);

        textName.setText(name);
        textContact.setText(contact);
        textEmail.setText(email);
        textRole.setText(role);


    }
}

