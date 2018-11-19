package com.example.rupali.sos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class EditProfile extends AppCompatActivity {

    EditText name,contact,address,new_password,confirm_password;
    Spinner role1, role2, role3;
    Button update, addRole;
    ImageButton choose;
    ImageView ivImage;
    String userChoosenTask;
    String userName,userRole1,userRole2,userRole3,userContact,userAddress, userEmail;

    int success;
    String message;
    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_edit_user = "https://sahayyam.000webhostapp.com/edit_user.php";
    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";
    ArrayList<String> roleTypes;
    ProgressDialog dialog;
    String[] selected_options = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        dialog = new ProgressDialog(EditProfile.this);
        dialog.setMessage("Loading...");
        dialog.show();

        roleTypes=new ArrayList<>();
        roleTypes.add("-- Select --");

        role1 = findViewById(R.id.role1);
        role2 = findViewById(R.id.role2);
        role3 = findViewById(R.id.role3);

        loadSpinnerData(URL);

        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address);
        update = findViewById(R.id.update);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        ivImage = findViewById(R.id.profile);

        userName = prefs.getString("user_name","");
        userRole1 = prefs.getString("user_role1","");
        userRole2 = prefs.getString("user_role2","");
        userRole3 = prefs.getString("user_role3","");
        userContact = prefs.getString("user_contact","");
        userAddress = prefs.getString("user_address", "");
        userEmail = prefs.getString("user_email", "");

        name.setText(userName);
        contact.setText(userContact);
        address.setText(userAddress);

        choose = findViewById(R.id.choose);
        choose.bringToFront();
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        update.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean spinVerify = verifyspinners();
                boolean verifyEditText = verifyedittexts();

                if(spinVerify == true && verifyEditText == true){
                    new EditUser().execute();
                }
            }
        });

        role1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = role1.getSelectedItem().toString().trim();
                selected_options[0] = Selected ;
                System.out.println(selected_options[0]);
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        role2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = role2.getSelectedItem().toString().trim();
                selected_options[1] = Selected ;
                System.out.println(selected_options[1]);
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        role3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = role3.getSelectedItem().toString().trim();
                selected_options[2] = Selected ;
                System.out.println(selected_options[2]);
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean verifyspinners(){
        if( selected_options[0].equals(selected_options[1]) ){
            if(!selected_options[0].equals("-- Select --")){
                Toast.makeText(EditProfile.this,"Two Roles cannot be same.",Toast.LENGTH_LONG).show();
                role1.setBackgroundColor(getColor(R.color.red));
                role2.setBackgroundColor(getColor(R.color.red));
                return false;
            }
        }
        if( selected_options[0].equals(selected_options[2]) ){
            if(!selected_options[0].equals("-- Select --")){
                Toast.makeText(EditProfile.this,"Two Roles cannot be same.",Toast.LENGTH_LONG).show();
                role1.setBackgroundColor(getColor(R.color.red));
                role3.setBackgroundColor(getColor(R.color.red));
                return false;
            }
        }
        if( selected_options[1].equals(selected_options[2]) ){
            if(!selected_options[1].equals("-- Select --")){
                Toast.makeText(EditProfile.this,"Two Roles cannot be same.",Toast.LENGTH_LONG).show();
                role2.setBackgroundColor(getColor(R.color.red));
                role3.setBackgroundColor(getColor(R.color.red));
                return false;
            }
        }
        if(selected_options[0].equals("-- Select --") && selected_options[1].equals("-- Select --") && selected_options[2].equals("-- Select --")){
            Toast.makeText(EditProfile.this,"Please Select at least one Role.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean verifyedittexts(){
        if((name.getText().toString()).equals("")){
            Toast.makeText(EditProfile.this,"Please Enter Name. ",Toast.LENGTH_LONG).show();
            return false;
        }

        if((contact.getText().toString()).equals("")){
            Toast.makeText(EditProfile.this,"Please Enter Contact. ",Toast.LENGTH_LONG).show();
            return false;
        }

        if((address.getText().toString()).equals("")){
            Toast.makeText(EditProfile.this,"Please Enter address. ",Toast.LENGTH_LONG).show();
            return false;
        }

        if(!(new_password.getText().toString()).equals(confirm_password.getText().toString())){
            Toast.makeText(EditProfile.this,"New Password and Confirmed Password are not same. ",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(EditProfile.this);
                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
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
                    if (userChoosenTask.equals("Camera"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Gallery"))
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

    private void loadSpinnerData(String url) {

        RequestQueue requestQueue=Volley.newRequestQueue(EditProfile.this);

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

                    role1.setAdapter(new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));
                    role2.setAdapter(new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));
                    role3.setAdapter(new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));
                    role1.setSelection(getIndex(role1,userRole1));
                    if(!userRole2.equals("")){
                        role2.setSelection(getIndex(role2,userRole2));
                    }
                    if(!userRole3.equals("")){
                        role3.setSelection(getIndex(role3,userRole3));
                    }
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

    //private method of your class
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    /**
     * Background Async Task to Create new user
     * */
    class EditUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(EditProfile.this);
            dialog.setMessage("Updating...");
            dialog.show();
        }

        /**
         * Creating user
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("name", name.getText().toString().trim()));
                params.add(new BasicNameValuePair("role1", selected_options[0]));
                if(!selected_options[1].equals("-- Select --")){
                    params.add(new BasicNameValuePair("role2" , selected_options[1]));
                }
                if(!selected_options[2].equals("-- Select --")){
                    params.add(new BasicNameValuePair("role3" , selected_options[2]));
                }
                params.add(new BasicNameValuePair("contact", contact.getText().toString().trim()));
                params.add(new BasicNameValuePair("address", address.getText().toString().trim()));
                params.add(new BasicNameValuePair("email", userEmail));
                params.add(new BasicNameValuePair("password", confirm_password.getText().toString()));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_edit_user, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt("success");
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
                editor.putString("user_email",userEmail).commit();
                editor.putString("user_name",name.getText().toString().trim()).commit();
                editor.putString("user_role1",role1.getSelectedItem().toString().trim()).commit();
                editor.putString("user_role2",role2.getSelectedItem().toString().trim()).commit();
                editor.putString("user_role3",role3.getSelectedItem().toString().trim()).commit();
                editor.putString("user_contact",contact.getText().toString().trim()).commit();
                editor.putString("user_address",address.getText().toString().trim()).commit();

                String Role1 = role1.getSelectedItem().toString();
                String Role2 = role2.getSelectedItem().toString();
                String Role3 = role3.getSelectedItem().toString();

                String roles = null;
                if(!Role1.equals("-- Select --") && !Role2.equals("-- Select --") && !Role3.equals("-- Select --")){
                    roles = Role1+", "+Role2+", "+Role3;
                }
                else if(!Role2.equals("-- Select --") && Role3.equals("-- Select --")){
                    roles = Role1+", "+Role2;
                }
                else if(Role2.equals("-- Select --") && Role3.equals("-- Select --")){
                    roles = Role1;
                }
                editor.putString("roles",roles).commit();

                Intent intent=new Intent(EditProfile.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                (EditProfile.this).finish();
            }
            else
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

            dialog.dismiss();
        }
    }


}