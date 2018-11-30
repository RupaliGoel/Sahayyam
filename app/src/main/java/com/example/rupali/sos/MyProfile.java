package com.example.rupali.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyProfile extends AppCompatActivity {
    String email, name, role1, role2, role3, contact, address, roles;
    ImageButton editbtn;
    String message, url;
    int success = 0;
    String image, imageUri;
    JSONParser jsonParser = new JSONParser();
    Uri imgUri;
    ImageView ivImage;
    String PathUrl = "https://sahayyam.000webhostapp.com/getimage.php";
    Bitmap bm;

    //ImageLoader imageLoader = ImageLoader.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        // imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        name = prefs.getString("user_name", "guest");
        roles = prefs.getString("roles", "guest");
        /*role1 = prefs.getString("user_role1","");
        role2 = prefs.getString("user_role2","");
        role3 = prefs.getString("user_role3","");*/
        contact = prefs.getString("user_contact", "**********");
        email = prefs.getString("user_email", "guest@guest.com");
        address = prefs.getString("user_address", "***");
        final boolean isLogin = prefs.getBoolean("Islogin", false);
        TextView textName = findViewById(R.id.name);
        TextView textRole = findViewById(R.id.role);
        TextView textEmail = findViewById(R.id.email);
        TextView textContact = findViewById(R.id.mobileNumber);
        TextView textAddress = findViewById(R.id.address);
        editbtn = findViewById(R.id.edit);
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin == true) {
                    Intent intent = new Intent(MyProfile.this, EditProfile.class);
                    startActivity(intent);
                }
            }
        });
        textName.setText(name);
        textContact.setText(contact);
        textEmail.setText(email);
        /*if(!role1.equals("") && !role2.equals("") && !role3.equals("")){
            textRole.setText(role1+", "+role2+", "+role3);
        }
        else if(!role2.equals("") && role3.equals("")){
            textRole.setText(role1+", "+role2);
        }
        else if(role2.equals("") && role3.equals("")){
            textRole.setText(role1);
        }*/
        textRole.setText(roles);
        textAddress.setText(address);
        //Bitmap b = BitmapFactory.decodeFile(PathUrl);
        //ivImage=findViewById(R.id.profile);
        new getImage().execute();
    }

    public class getImage extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating user
         */
        public String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", email));
                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(PathUrl, "POST", params);
                JSONArray values = json.getJSONArray("user");

                JSONObject details = values.getJSONObject(0);
                imageUri = details.getString("image");
                success = json.getInt("success");
                message = json.getString("message");
                //System.out.print(imageUri);
                // url="file://"+imageUri;
                //JSONObject json = jsonParser.makeHttpRequest(PathUrl, "GET",);
                //imageUri =PathUrl.details;
                //JSONObject json = jsonParser.makeHttpRequest(url_write_emergency, "POST", params);
                // check log cat fro response
                //Log.d("Create Response", json.toString());
            } catch (Exception e) {
                System.out.print(e);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        public void onPostExecute(String file_url) {
            if (success == 1) {
                try {
                    ivImage = findViewById(R.id.profile);
                    //Toast.makeText(MyProfile.this.getApplicationContext(), imageUri, Toast.LENGTH_LONG).show();
                    //URL img = new URL(imageUri);
                    /*InputStream is = new FileInputStream(mine);
                    Drawable icon = new BitmapDrawable(is);
                    ivImage.setImageDrawable(icon);*/
                    //Toast.makeText(MyProfile.this.getApplicationContext(),imageUri, Toast.LENGTH_LONG).show();
                    //imageUri=imageUri.replace("\\/","/");

                    /*Glide.with(MyProfile.this)
                            .load(imageUri).apply(new RequestOptions().override(150,150))
                            .into(ivImage);*/
                    if (imageUri.isEmpty()) {
                        ivImage.setImageResource(R.drawable.whiteimageview);
                    } else {
                        Picasso.with(MyProfile.this)
                                .load(imageUri).resize(150, 150)
                                .noFade().into(ivImage);
                    }
                    ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MyProfile.this,FullImage.class);
                            if(!ivImage.equals("")) {
                                intent.putExtra("image", imageUri);
                                startActivity(intent);
                            }
                        }
                    });

                    //Toast.makeText(MyProfile.this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    System.out.print(e);
                }
            }
            //else
            //  Toast.makeText(MyProfile.this.getApplicationContext(),"Failed.",Toast.LENGTH_LONG).show();
        }
    }
}
