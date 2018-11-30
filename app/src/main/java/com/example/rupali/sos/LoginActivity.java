package com.example.rupali.sos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;

    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_login_user = "https://sahayyam.000webhostapp.com/login.php";
    private static String url_user_details = "https://sahayyam.000webhostapp.com/get_user_details.php";
    String email,password,name,role1,role2,role3,contact,address,profileimage;
    int success = 0;
    String message = "";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view now
        setContentView(R.layout.activity_login);

        initViews();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else{
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

                new CheckUser().execute();
            }
        });
    }

    /**
     * This method is to initialize views
     */
    private void initViews(){
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        // set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /**
     * Background Async Task to Create new user
     * */
    class CheckUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);

        }

        /**
         * Login user
         */
        protected String doInBackground(String... args) {


            try
            {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("password", password));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_login_user, "POST", params);
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


        protected void onPostExecute(String file_url) {
            // dismiss the progressbar once done
            if(success==1) {
                new GetDetails().execute();
            }
            else{
                Toast.makeText(getApplicationContext(),message ,Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }

        }
    }

    /**
     * Background Async Task to get username
     * */
    class GetDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Login user
         */
        protected String doInBackground(String... args) {


            try
            {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("password", password));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_user_details, "POST", params);

                JSONArray values = json.getJSONArray("user");

                JSONObject details = values.getJSONObject(0);
                name = details.getString("name");
                role1 = details.getString("role1");
                role2 = details.getString("role2");
                role3 = details.getString("role3");
                contact = details.getString("contact");
                address = details.getString("address");
                profileimage = details.getString("image");

                // check log cat fro response
                Log.d("Create Response", json.toString());

            }
            catch(Exception e)
            {
                System.out.print(e);
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            // dismiss the progressbar once done
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.edit().putBoolean("Islogin",true).apply();
            System.out.println("Variables : " + email+ name+ role1+ role2+ role3+ contact+ address);
            prefs.edit().putString("user_email", email).commit();
            prefs.edit().putString("user_name", name).commit();
            prefs.edit().putString("user_role1",role1).commit();
            prefs.edit().putString("user_role2",role2).commit();
            prefs.edit().putString("user_role3",role3).commit();
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
            prefs.edit().putString("roles",roles).commit();
            prefs.edit().putString("user_contact",contact).commit();
            prefs.edit().putString("user_address",address).commit();
            prefs.edit().putString("profile_picture",profileimage).commit();
            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK, intent);
            startActivity((intent).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            progressBar.setVisibility(View.GONE);
            LoginActivity.this.finish();
        }
    }
}
