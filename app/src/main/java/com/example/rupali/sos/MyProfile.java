package com.example.rupali.sos;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MyProfile extends AppCompatActivity {

    String email,name,role,contact,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        name = prefs.getString("user_name","Not Found");
        role = prefs.getString("user_role","Not Found");
        contact = prefs.getString("user_contact","Not Found");
        email = prefs.getString("user_email", "Not Found");
        address = prefs.getString("user_address", "Not Found");

        TextView textName = findViewById(R.id.name);
        TextView textRole = findViewById(R.id.role);
        TextView textEmail = findViewById(R.id.email);
        TextView textContact =findViewById(R.id.mobileNumber);
        TextView textAddress = findViewById(R.id.location);

        textName.setText(name);
        textContact.setText(contact);
        textEmail.setText(email);
        textRole.setText(role);
        textAddress.setText(address);

    }
}

