package com.example.rupali.sos;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ContactUs extends AppCompatActivity {

    Toolbar page_name;
    TextView name1,name2,name3,name4;
    TextView mail1,mail2,mail3,mail4;
    ImageButton bt1,bt2,bt3,bt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        page_name = (Toolbar) findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        name1 = findViewById(R.id.person_name1);
        name2 = findViewById(R.id.person_name2);
        name3 = findViewById(R.id.person_name3);
        name4 = findViewById(R.id.person_name4);

        mail1 = findViewById(R.id.person1_email);
        mail2 = findViewById(R.id.person2_email);
        mail3 = findViewById(R.id.person3_email);
        mail4 = findViewById(R.id.person4_email);

        bt1 = findViewById(R.id.mail1);
        bt2 = findViewById(R.id.mail2);
        bt3 = findViewById(R.id.mail3);
        bt4 = findViewById(R.id.mail4);
    }

    protected void sendEmail(View v) {
        String tag = v.getTag().toString();
        this.sendEmail(tag);

    }

    public void sendEmail(String mail) {
        Log.i("Send email", "");
        String[] TO = {mail};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email..."));
            finish();
            Log.i("Email Sent.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactUs.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}


