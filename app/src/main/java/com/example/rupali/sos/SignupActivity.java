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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageButton;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName, inputContact, inputAddress;
    private Button btnSignIn, btnSignUp;
    ImageButton placepickerbtn;
    Spinner Role1, Role2, Role3;
    String email,password,name,role1,role2,role3,contact,address;
    int success;
    Bitmap bm =null;

    JSONParser jsonParser = new JSONParser();
    ProgressDialog dialog;
    // url to create new product
    private static String url_create_user = "https://sahayyam.000webhostapp.com/create_user.php";
    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    String message;

    String userChoosenTask;
    String ImageName = "user_imagename";
    ImageView ivImage;
    Button submit, choose;
    ArrayList<String> roleTypes;
    String[] selected_options = new String[3];
    int PLACE_PICKER_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        roleTypes=new ArrayList<>();
        roleTypes.add("-- Select --");

        dialog = new ProgressDialog(SignupActivity.this);
        dialog.setMessage("Loading...");
        dialog.show();
        initViews();
        loadSpinnerData(URL);

        choose = findViewById(R.id.choose);
        ivImage=(ImageView)findViewById(R.id.uploadedphoto);
        choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                name = inputName.getText().toString().trim();
                role1 = Role1.getSelectedItem().toString().trim();
                role2 = Role2.getSelectedItem().toString().trim();
                role3 = Role3.getSelectedItem().toString().trim();
                contact = inputContact.getText().toString().trim();
                address = inputAddress.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email Address !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(), "Enter Address !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contact)) {
                    Toast.makeText(getApplicationContext(), "Enter Contact !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if( selected_options[0].equals(selected_options[1]) ){
                    if(!selected_options[0].equals("-- Select --")){
                        Toast.makeText(SignupActivity.this,"Two Roles cannot be same.",Toast.LENGTH_LONG).show();
                        Role1.setBackgroundColor(getColor(R.color.red));
                        Role2.setBackgroundColor(getColor(R.color.red));
                        return;
                    }
                }

                if( selected_options[0].equals(selected_options[2]) ){
                    if(!selected_options[0].equals("-- Select --")){
                        Toast.makeText(SignupActivity.this,"Two Roles cannot be same.",Toast.LENGTH_LONG).show();
                        Role1.setBackgroundColor(getColor(R.color.red));
                        Role3.setBackgroundColor(getColor(R.color.red));
                        return;
                    }
                }

                if( selected_options[1].equals(selected_options[2]) ){
                    if(!selected_options[1].equals("-- Select --")){
                        Toast.makeText(SignupActivity.this,"Two Roles cannot be same.",Toast.LENGTH_LONG).show();
                        Role2.setBackgroundColor(getColor(R.color.red));
                        Role3.setBackgroundColor(getColor(R.color.red));
                        return;
                    }
                }

                if(selected_options[0].equals("-- Select --") && selected_options[1].equals("-- Select --") && selected_options[2].equals("-- Select --")){
                    Toast.makeText(SignupActivity.this,"Please Select at least one Role.",Toast.LENGTH_LONG).show();
                    return;
                }
                if(ivImage.getDrawable()==null){
                    Toast.makeText(SignupActivity.this,"Please Select Image",Toast.LENGTH_LONG).show();
                    return;
                }



                //postDataToSQLite();
                // creating new product in background thread
                new CreateNewUser().execute();

            }
        });

        Role1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = Role1.getSelectedItem().toString().trim();
                selected_options[0] = Selected ;
                System.out.println(selected_options[0]);
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        Role2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = Role2.getSelectedItem().toString().trim();
                selected_options[1] = Selected ;
                System.out.println(selected_options[1]);
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        Role3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = Role3.getSelectedItem().toString().trim();
                selected_options[2] = Selected ;
                System.out.println(selected_options[2]);
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        placepickerbtn = findViewById(R.id.placepickerbutton);
        placepickerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SignupActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(SignupActivity.this);
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
            else if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, SignupActivity.this);
                String toastMsg = String.format("%s", place.getAddress());
                inputAddress.setText(toastMsg);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
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
        ivImage=(ImageView)findViewById(R.id.uploadedphoto);
        ivImage.setImageBitmap(thumbnail);
    }




    /**
     * This method is to initialize views
     */
    private void initViews(){
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputName = (EditText) findViewById(R.id.etName);
        inputContact = (EditText) findViewById(R.id.etContact);
        inputAddress = (EditText) findViewById(R.id.etAddress);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        Role1 = findViewById(R.id.role1);
        Role2 = findViewById(R.id.role2);
        Role3 = findViewById(R.id.role3);
    }

    /**
     * Background Async Task to Create new user
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SignupActivity.this);
            dialog.setMessage("Registration in Progress...");
            dialog.show();
        }

        /**
         * Creating user
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("role1", selected_options[0]));
                if(!selected_options[1].equals("-- Select --")){
                    params.add(new BasicNameValuePair("role2" , selected_options[1]));
                }
                if(!selected_options[2].equals("-- Select --")){
                    params.add(new BasicNameValuePair("role3" , selected_options[2]));
                }
                params.add(new BasicNameValuePair("contact", contact));
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("address",address));
                ivImage.buildDrawingCache();
                Bitmap bitmap = ivImage.getDrawingCache();
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] image = stream.toByteArray();
                String img_str = Base64.encodeToString(image,0);
                params.add(new BasicNameValuePair("user_image",img_str));


                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_create_user, "POST", params);
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

            if(success==1) {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("Islogin", true).apply();
                editor.putString("user_email",inputEmail.getText().toString().trim()).commit();
                editor.putString("user_name",inputName.getText().toString().trim()).commit();
                editor.putString("user_role1",selected_options[0]).commit();
                editor.putString("user_role2",selected_options[1]).commit();
                editor.putString("user_role3",selected_options[2]).commit();
                editor.putString("user_contact",inputContact.getText().toString().trim()).commit();
                editor.putString("user_address",inputAddress.getText().toString().trim()).commit();
                String roles = null;
                if(!role1.equals("") && !role2.equals("") && !role3.equals("")){
                    roles = role1+", "+role2+", "+role3;
                }
                else if(!role2.equals("") && role3.equals("")){
                    roles = role1+", "+role2;
                }
                else if(role2.equals("") && role3.equals("")){
                    roles = role1;
                }
                editor.putString("roles",roles).commit();

                Intent intent=new Intent(SignupActivity.this, MainActivity.class);
                setResult(Activity.RESULT_OK, intent);
                startActivity((intent).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                dialog.dismiss();
                SignupActivity.this.finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Registration Failed" + ", "+ message,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
            ivImage.setImageDrawable(null);
        }
    }

    private void loadSpinnerData(String url) {

        RequestQueue requestQueue=Volley.newRequestQueue(SignupActivity.this);

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

                            roleTypes.add(type);

                        }

                    }

                    Role1.setAdapter(new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));
                    Role2.setAdapter(new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));
                    Role3.setAdapter(new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));

                    dialog.dismiss();

                }catch (JSONException e){e.printStackTrace();}

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
                params.put("type", "Role");
                return params;
            }
        };

        int socketTimeout = 30000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        requestQueue.add(stringRequest);

    }

}
