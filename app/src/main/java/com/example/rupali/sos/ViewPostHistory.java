package com.example.rupali.sos;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewPostHistory extends AppCompatActivity {

    TextView titlePost, descriptionPost;
    ImageView imagePost;
    Toolbar page_name;
    int id;
    String imageurl;

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
        imageurl = bundle.getString("Picture");
        id = bundle.getInt("Id");

        imagePost= findViewById(R.id.imagePost);

        if (imageurl.isEmpty()) {
           imagePost.setImageResource(R.drawable.whiteimageview);
        } else{
            Picasso.with(ViewPostHistory.this)
                    .load(imageurl).resize(150,150)
                    .noFade().into(imagePost);
        }
        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPostHistory.this,FullImage.class);
                if(!imageurl.equals("")) {
                    intent.putExtra("image", imageurl);
                    startActivity(intent);
                }
            }
        });

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

