package com.example.rupali.sos;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputName,inputContact;
    private Button btnSignIn, btnSignUp, btnResetPassword,inputRole;
    private ProgressBar progressBar;
    String email,password,name,role,contact;
    int success;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_create_user = "https://sahayyam.000webhostapp.com/create_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    String userChoosenTask;
    ImageView ivImage;
    Button submit, choose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();

        //btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
//            }
//        });

        inputRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, AddRole.class);
                startActivity(intent);

            }
        });

        choose = findViewById(R.id.choose);
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
                role = inputRole.getText().toString().trim();
                contact = inputContact.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email Address !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(role)) {
                    Toast.makeText(getApplicationContext(), "Enter Role!", Toast.LENGTH_SHORT).show();
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


                //postDataToSQLite();
                // creating new product in background thread
                new CreateNewUser().execute();

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
        ivImage=(ImageView)findViewById(R.id.uploadedphoto);
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
        inputRole = (Button) findViewById(R.id.etRole);
        inputContact = (EditText) findViewById(R.id.etContact);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Creating user
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("role", role));
                params.add(new BasicNameValuePair("contact", contact));
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("password", password));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_create_user, "POST", params);
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
            // dismiss the progressbar once done

            if(success==1) {
                Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(SignupActivity.this, MainActivity.class);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("Islogin", true).apply();
                editor.putString("user_email",inputEmail.getText().toString().trim()).commit();
                editor.putString("user_name",inputName.getText().toString().trim()).commit();
                editor.putString("user_role1",inputRole.getText().toString().trim()).commit();
                editor.putString("user_contact",inputContact.getText().toString().trim()).commit();
                //editor.putString("user_address",inputAddress.getText().toString().trim()).commit();
                setResult(Activity.RESULT_OK, intent);
                startActivity((intent).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                progressBar.setVisibility(View.GONE);
                SignupActivity.this.finish();
            }
            else
                Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_LONG).show();
        }
    }
}
