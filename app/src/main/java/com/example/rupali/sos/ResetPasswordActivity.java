package com.example.rupali.sos;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResetPasswordActivity extends AppCompatActivity {


    private EditText inputEmail;
    private Button btnReset, btnBack;
    private ProgressBar progressBar;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_check_user = "https://sahayyam.000webhostapp.com/check_email.php";
    // JSON Node names
    int success;
    String message;
    String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                //String email="rupaligoel@yahoo.com";

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                new CheckUser().execute();

               progressBar.setVisibility(View.VISIBLE);


            }
        });

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

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_check_user, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                JSONArray jsonArray = json.getJSONArray("user");
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                // check for success tag
                try {
                    password =jsonObject.getString("password");
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


        protected void onPostExecute(String file_url) {

            // dismiss the progressbar once done
            Toast.makeText(getApplicationContext(),message ,Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);

            BackgroundMail.newBuilder(ResetPasswordActivity.this)
                    .withUsername("sahayyam18@gmail.com")
                    .withPassword("qwerty@2018")
                    .withMailto(""+email)
                    .withType(BackgroundMail.TYPE_PLAIN)
                    .withSubject("PASSWORD RESET")
                    .withBody("Your Password is "+password)
                    .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ResetPasswordActivity.this,"Successful.",Toast.LENGTH_LONG).show();
//                                Intent i=new Intent(Feedback_ms.this,CustomerActivity_cm.class);
//                                startActivity(i);
                        }
                    })
                    .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                        @Override
                        public void onFail() {
                            Toast.makeText(ResetPasswordActivity.this,"Failed.",Toast.LENGTH_LONG).show();
                        }
                    })
                    .send();
        }
    }
}
