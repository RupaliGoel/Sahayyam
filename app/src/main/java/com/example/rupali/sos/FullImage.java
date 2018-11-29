package com.example.rupali.sos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullImage extends AppCompatActivity {

    ImageView imageView;
    String imageurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Bundle bundle = getIntent().getExtras();
        imageurl = bundle.getString("image",null);

        imageView = findViewById(R.id.fullscreenimage);

        Picasso.with(FullImage.this)
                .load(imageurl)
                .noFade().into(imageView);

    }

}
