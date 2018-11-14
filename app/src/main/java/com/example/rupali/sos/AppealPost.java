package com.example.rupali.sos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AppealPost extends AppCompatActivity {

    TextView typePost, descriptionPost;
    ImageView imagePost;
    Toolbar page_name;
    String email;
    Button details;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeal_post);

        page_name = findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("Type");
        String content = bundle.getString("Content");
        email = bundle.getString("Email");
//        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        typePost = findViewById(R.id.titlePost);
        typePost.setText(type);
        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);

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
