package com.example.rupali.sos;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaiseOnAppeal extends AppCompatActivity {
    EditText role, name, address, contact, desc, hiddenType;
    Button submit, choose;
    Spinner type;
    ProgressDialog progressDialog;
    ImageView ivImage;
    String userChoosenTask;
    private androidx.appcompat.widget.Toolbar page_name;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currentlattitude,currentlongitude,addressOfUser,nameOfUser,roleOfUser,contactOfUser,emailOfUser;
    String placelattitude,placelongitude;
    String title_appeal ;

    int success;


    String toolbarMessage;
    Toolbar toolbar;
    TextView appnametv;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_write_appeal = "https://sahayyam.000webhostapp.com/write_appeal.php";
    private static String url_write_gen_master = "https://sahayyam.000webhostapp.com/write_gen_master.php";

    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";
    ArrayList<String> appealTypes;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    View progressOverlay;



    public RaiseOnAppeal() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_on_appeal);

        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        // Show progress overlay (with animation):
        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        appealTypes=new ArrayList<>();

        choose = findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        currentlattitude = prefs.getString("lat","");
        currentlongitude = prefs.getString("long","");
        emailOfUser = prefs.getString("user_email","");
        addressOfUser = prefs.getString("user_address","");
        nameOfUser = prefs.getString("user_name", "Guest");
        roleOfUser = prefs.getString("roles","");
        contactOfUser = prefs.getString("user_contact","");


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        page_name = (Toolbar) findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean Islogin = prefs.getBoolean("Islogin", false);


        toolbarMessage = prefs.getString("AppName","App");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appnametv = (TextView)findViewById(R.id.appname);
        appnametv.setText(toolbarMessage);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        if (!Islogin) {   // condition true means user is not logged in
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(i, 1);
        }

        ivImage = findViewById(R.id.uploadedphoto);
        role = findViewById(R.id.role);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        type = findViewById(R.id.apptype);
        contact = findViewById(R.id.contact);
        desc = findViewById(R.id.description);
        hiddenType = findViewById(R.id.title);

        address.setText(addressOfUser);
        address.setEnabled(false);
        role.setText(roleOfUser);
        role.setEnabled(false);
        contact.setText(contactOfUser);
        contact.setEnabled(false);
        name.setText(nameOfUser);
        name.setEnabled(false);

        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((type.getSelectedItem().toString()).equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Appeal Type!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if ((desc.getText().toString()).equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Appeal Description!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (ivImage.getDrawable()==null) {
                    Toast.makeText(getApplicationContext(), "Select Image!", Toast.LENGTH_SHORT).show();
                    return;
                }


                else
                    new writeAppeal().execute();
            }
        });

        loadSpinnerData(URL);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                // TODO Auto-generated method stub
                String selected_option = type.getItemAtPosition(type.getSelectedItemPosition()).toString();


                if(selected_option.matches("Other"))
                {
                    hiddenType.setVisibility(View.VISIBLE);
                }
                else {
                    hiddenType.setVisibility(View.INVISIBLE);
                }

                title_appeal = selected_option;

                Toast.makeText(getApplicationContext(),selected_option,Toast.LENGTH_LONG).show();

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RaiseOnAppeal.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(RaiseOnAppeal.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    static final int REQUEST_CAMERA = 2;
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    static final int SELECT_FILE = 1;

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(thumbnail);
    }

    /**
     * Background Async Task to Create new user
     * */
    class writeAppeal extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RaiseOnAppeal.this," Appeal is Uploading","Please Wait",false,false);
        }

        /**
         * Creating user
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters
                if(title_appeal.equals("Other"))
                {
                    title_appeal = hiddenType.getText().toString();
                    new writeGen_Master().execute();
                }

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", emailOfUser));
                params.add(new BasicNameValuePair("type",title_appeal ));
                params.add(new BasicNameValuePair("desc", desc.getText().toString().trim()));
                params.add(new BasicNameValuePair("addlat", currentlattitude));
                params.add(new BasicNameValuePair("addlong", currentlongitude));
                ivImage.buildDrawingCache();
                Bitmap bitmap = ivImage.getDrawingCache();
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
                byte[] image = stream.toByteArray();
                String img_str = Base64.encodeToString(image,0);
                params.add(new BasicNameValuePair("app_image",img_str));
                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_write_appeal, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);
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
            if(success==1) {
                Toast.makeText(getApplicationContext(),"Saved Successfully.",Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(getApplicationContext(),"Failed.",Toast.LENGTH_LONG).show();
            ivImage.setImageDrawable(null);
            RaiseOnAppeal.this.finish();
        }
    }


    private void loadSpinnerData(String url) {

        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override

            public void onResponse(String response) {

                try{

                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getInt("success")==1){

                        JSONArray jsonArray=jsonObject.getJSONArray("types");

                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject jsonObject1=jsonArray.getJSONObject(i);

                            String type=jsonObject1.getString("desc");

                            appealTypes.add(type);

                        }

                    }

                    type.setAdapter(new ArrayAdapter<String>(RaiseOnAppeal.this, android.R.layout.simple_spinner_dropdown_item, appealTypes));

                }catch (JSONException e){e.printStackTrace();}

                // Hide it (with animation):
                AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);

            }

        }, new Response.ErrorListener() {

            @Override

            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }

        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("type", "Appeal");
                return params;
            }
        };

        int socketTimeout = 30000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        requestQueue.add(stringRequest);

    }

    /**
     * Background Async Task to Create new user
     * */
    class writeGen_Master extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating gm_code
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("code", "Appeal"));
                params.add(new BasicNameValuePair("desc", title_appeal));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_write_gen_master, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);
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

        }
    }

}