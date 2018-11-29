package com.example.rupali.sos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostViewNDelete extends AppCompatActivity {

    TextView titlePost, descriptionPost;
    ImageView imagePost;
    Toolbar page_name;
    int id;
    String type;
    String imageurl;
    Button delete;

    int success;

    JSONParser jsonParser = new JSONParser();
    ProgressDialog dialog;
    // url to create new product
    private static String url_delete_post = "https://sahayyam.000webhostapp.com/delete_posts.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view_ndelete);

        page_name = findViewById(R.id.page_name);
        imagePost = findViewById(R.id.imagePost);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("Title");
        String content = bundle.getString("Content");
       // String imageurl = bundle.getString("Picture");
        type = bundle.getString("Type");
        id = bundle.getInt("Id");
        //        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        titlePost = findViewById(R.id.titlePost);
        titlePost.setText(title);
        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);
        imageurl = bundle.getString("Picture");
        System.out.println(imageurl);

        imagePost = findViewById(R.id.imagePost);
        if (imageurl.isEmpty()) {
            imagePost.setImageResource(R.drawable.whiteimageview);
        } else {
            Picasso.with(PostViewNDelete.this)
                    .load(imageurl).resize(150, 150)
                    .noFade().into(imagePost);
        }
        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostViewNDelete.this,FullImage.class);
                if(!imageurl.equals("")) {
                    intent.putExtra("image", imageurl);
                    startActivity(intent);
                }
            }
        });


        delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DeletePost().execute();
            }
        });

    }

    /**
     * Background Async Task to Create new user
     * */
    class DeletePost extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(PostViewNDelete.this);
            dialog.setMessage("Deleting...");
            dialog.show();
        }

        /**
         * Creating user
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", type));
                params.add(new BasicNameValuePair("id", String.valueOf(id)));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_delete_post, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            catch(Exception e)
            {
                System.out.print(e);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the progressbar once done

            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

            dialog.setMessage(message);
            dialog.dismiss();

            Intent returnIntent = new Intent();
            if(success == 1){

                setResult(RESULT_OK, returnIntent);

            }
            else{

                setResult(RESULT_CANCELED, returnIntent);

            }
            PostViewNDelete.this.finish();
        }
    }

}
