package com.example.rupali.sos;


import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.rupali.sos.R;

public class UploadPhoto extends Activity implements View.OnClickListener{

    private LinearLayout lnrImages;
    private Button btnAddPhots;
    private Button btnSaveImages;
    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;
    private Bitmap resized;

    private final int PICK_IMAGE_MULTIPLE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
       // lnrImages = (LinearLayout)findViewById(R.id.lnrImages);
        btnAddPhots = (Button)findViewById(R.id.addphoto);
        //btnSaveImages = (Button)findViewById(R.id.btnSaveImages);
        btnAddPhots.setOnClickListener(this);
        //btnSaveImages.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addphoto:
                try {
                    if (ActivityCompat.checkSelfPermission(UploadPhoto.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UploadPhoto.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);
                    }
                    else{
                    Intent intent = new Intent(UploadPhoto.this,SelectImagesFromGallery.class); //CustomPhotoGalleryActivity.class);
                    startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
//            case R.id.btnSaveImages:
//                if(imagesPathList !=null){
//                    if(imagesPathList.size()>1) {
//                        Toast.makeText(UploadPhoto.this, imagesPathList.size() + " no of images are selected", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(UploadPhoto.this, imagesPathList.size() + " no of image are selected", Toast.LENGTH_SHORT).show();
//                    }
//                }else{
//                    Toast.makeText(UploadPhoto.this," no images are selected", Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if(requestCode == PICK_IMAGE_MULTIPLE){
//                imagesPathList = new ArrayList<String>();
//                String[] imagesPath = data.getStringExtra("data").split("\\|");
//                try{
//                    lnrImages.removeAllViews();
//                }catch (Throwable e){
//                    e.printStackTrace();
//                }
//                for (int i=0;i<imagesPath.length;i++){
//                    imagesPathList.add(imagesPath[i]);
//                    yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
//                    ImageView imageView = new ImageView(this);
//                    imageView.setImageBitmap(yourbitmap);
//                    imageView.setAdjustViewBounds(true);
//                    lnrImages.addView(imageView);
//                }
//            }
        }

    }

