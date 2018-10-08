package com.example.rupali.sos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_app_details = "https://sahayyam.000webhostapp.com/get_app_details.php";

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;
    String strWelcomeMessage, strOwner, strAppName, strInventorName, strOrganisationName, strOrganisationAddress, strInventorContact;
    TextView welcome, organisation, appname, name, department, department_address, contact;
    View progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (isOnline()) {
            //do whatever you want to do
            progressOverlay = findViewById(R.id.progress_overlay);
            progressOverlay.bringToFront();

            new GetDetails().execute();

            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Internet Connection");
                builder.setMessage("Please turn on internet connection to use the App.");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //(SplashScreen.this).finishAffinity();
                        finishAndRemoveTask();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
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
            // Show progress overlay (with animation):
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        }

        /**
         * Login user
         */
        protected String doInBackground(String... args) {


            try
            {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_app_details, "POST", params);

                if(json.getInt("success") == 1){

                    JSONArray values = json.getJSONArray("app");

                    JSONObject details = values.getJSONObject(0);

                    strAppName = details.getString("app_name");
                    strOwner = details.getString("app_owner");
                    strWelcomeMessage = details.getString("app_welcome_message");
                    strInventorName = details.getString("app_inventor_name");
                    strOrganisationName = details.getString("app_department");
                    strOrganisationAddress = details.getString("app_department_address");
                    strInventorContact = details.getString("app_contact");
                    // check log cat fro response
                    Log.d("Create Response", json.toString());
                    System.out.println(strWelcomeMessage+ strOwner+ strAppName+ strInventorName+ strOrganisationName+ strOrganisationAddress+ strInventorContact);

                }
                else{
                    Snackbar.make(findViewById(R.id.splashScreenLayout), "SERVER ERROR.",
                            Snackbar.LENGTH_LONG)
                            .show();
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
            welcome = findViewById(R.id.tv_welcome_message);
            organisation = findViewById(R.id.tv_owner);
            appname = findViewById(R.id.tv_appName);
            name = findViewById(R.id.tv_name);
            department = findViewById(R.id.tv_organisation);
            department_address = findViewById(R.id.tv_address);
            contact = findViewById(R.id.tv_contact);

            welcome.setText(strWelcomeMessage);
            organisation.setText(strOwner);
            appname.setText(strAppName);
            name.setText(strInventorName);
            department.setText(strOrganisationName);
            department_address.setText(strOrganisationAddress);
            contact.setText(strInventorContact);

            // Hide it (with animation):
            AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(SplashScreen.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(SplashScreen.this , "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
