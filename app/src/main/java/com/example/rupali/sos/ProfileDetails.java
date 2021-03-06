package com.example.rupali.sos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ProfileDetails extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private static String url_user_details = "https://sahayyam.000webhostapp.com/get_user_details.php";
    String email,name,role1,role2,role3,contact,address;
    Toolbar page_name;
    ImageButton emailbtn,call,direction;
    ImageView imageView;
    String imageUrl;
    Button history;
    View progressOverlay;
    TextView tvName,tvContact,tvEmail,tvRole,tvAddress;

    double searchlat;
    double searchlong;
    String CurrentAddress ;
    double lat1,long1;



    String toolbarMessage;
    androidx.appcompat.widget.Toolbar toolbar;
    TextView appnametv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        /*imageView = findViewById(R.id.image);
        if (imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.profile);
        } else {
            Picasso.with(ProfileDetails.this)
                    .load(imageUrl).resize(150, 150)
                    .noFade().into(imageView);
        }*/
        new GetDetails().execute();
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
         * user details
         */
        protected String doInBackground(String... args) {
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", email));
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
                convertAddress(address);
                imageUrl = details.getString("image");
                // check log cat fro response
                Log.d("Create Response", json.toString());

            } catch (Exception e) {
                System.out.print(e);
            }
            return null;
        }

        public void convertAddress(String address) {
            Geocoder geocoder = new Geocoder(ProfileDetails.this, Locale.getDefault());
            if (address != null && !address.isEmpty()) {
                try {
                    List<Address> addressList = geocoder.getFromLocationName(address, 1);
                    if (addressList != null && addressList.size() > 0) {
                        searchlat = addressList.get(0).getLatitude();
                        searchlong = addressList.get(0).getLongitude();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } // end catch
            } // end if
        }

        public void convertAddress(String address,int a) {
            Geocoder geocoder = new Geocoder(ProfileDetails.this, Locale.getDefault());
            if (address != null && !address.isEmpty()) {
                try {
                    List<Address> addressList = geocoder.getFromLocationName(address, 1);
                    if (addressList != null && addressList.size() > 0) {
                        lat1 = addressList.get(0).getLatitude();
                        long1 = addressList.get(0).getLongitude();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } // end catch
            } // end if
        }


        protected void onPostExecute(String file_url) {
            //Toast.makeText(ProfileDetails.this,imageUrl,Toast.LENGTH_LONG).show();
            // dismiss the progressbar once done
            tvName = findViewById(R.id.name);
            tvName.setText(name);
            tvRole = findViewById(R.id.role);
            if(!role1.equals("") && !role2.equals("") && !role3.equals("")){
                tvRole.setText(role1+", "+role2+", "+role3);
            }
            else if(!role2.equals("") && role3.equals("")){
                tvRole.setText(role1+", "+role2);
            }
            else if(role2.equals("") && role3.equals("")){
                tvRole.setText(role1);
            }
            tvContact = findViewById(R.id.contact);
            tvContact.setText(contact);
            tvEmail = findViewById(R.id.email);
            tvEmail.setText(email);
            imageView = findViewById(R.id.image);
            if (imageUrl.isEmpty()) {
                imageView.setImageResource(R.drawable.whiteimageview);
            } else {
                Picasso.with(ProfileDetails.this)
                        .load(imageUrl).resize(150, 150)
                        .noFade().into(imageView);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileDetails.this,FullImage.class);
                    if(!imageUrl.equals("")) {
                        intent.putExtra("image", imageUrl);
                        startActivity(intent);
                    }
                }
            });
            tvAddress = findViewById(R.id.address);
            tvAddress.setText(address);
            emailbtn = findViewById(R.id.mail);
            emailbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail(ProfileDetails.this, email,"","",null);
                }
            });
            // Hide it (with animation):
            AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);

            call = findViewById(R.id.callButton);

            call.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View arg0)
                {
                    if(isPermissionGranted()){
                        call_action();
                    }
                }
            });

            history = findViewById(R.id.historyButton);
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileDetails.this, PostHistory.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
            });

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ProfileDetails.this);
            CurrentAddress = prefs.getString("user_current_address","");
            convertAddress(CurrentAddress,1);

            toolbarMessage = prefs.getString("AppName","App");

            toolbar = findViewById(R.id.toolbar);
            appnametv = (TextView)findViewById(R.id.appname);
            appnametv.setText(toolbarMessage);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);


            direction = findViewById(R.id.imageButton);
            direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String lat=Double.toString(searchlat);
                    String lon=Double.toString(searchlong);
                    String lat2=Double.toString(lat1);
                    String long2=Double.toString(long1);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+lat2+","+long2+"&daddr="+lat+","+lon));startActivity(intent);
                }
            });

        }
    }

    public void call_action(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        String num= tvContact.getText().toString();
        String uri= "tel:"+ num.trim();
        callIntent.setData(Uri.parse(uri));
        startActivity(callIntent);
    }
    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void sendEmail(final Context p_context, final String mailto, final String p_subject, final String p_body, final ArrayList<String> p_attachments)
    {
        try
        {
            PackageManager pm = p_context.getPackageManager();
            ResolveInfo selectedEmailActivity = null;

            Intent emailDummyIntent = new Intent(Intent.ACTION_SENDTO);
            emailDummyIntent.setData(Uri.parse("mailto:"+mailto));

            List<ResolveInfo> emailActivities = pm.queryIntentActivities(emailDummyIntent, 0);

            if (null == emailActivities || emailActivities.size() == 0)
            {
                Intent emailDummyIntentRFC822 = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailDummyIntentRFC822.setType("message/rfc822");

                emailActivities = pm.queryIntentActivities(emailDummyIntentRFC822, 0);
            }

            if (null != emailActivities)
            {
                if (emailActivities.size() == 1)
                {
                    selectedEmailActivity = emailActivities.get(0);
                }
                else
                {
                    for (ResolveInfo currAvailableEmailActivity : emailActivities)
                    {
                        if (true == currAvailableEmailActivity.isDefault)
                        {
                            selectedEmailActivity = currAvailableEmailActivity;
                        }
                    }
                }

                if (null != selectedEmailActivity)
                {
                    // Send email using the only/default email activity
                    sendEmailUsingSelectedEmailApp(p_context, mailto, p_subject, p_body, p_attachments, selectedEmailActivity);
                }
                else
                {
                    final List<ResolveInfo> emailActivitiesForDialog = emailActivities;

                    String[] availableEmailAppsName = new String[emailActivitiesForDialog.size()];
                    for (int i = 0; i < emailActivitiesForDialog.size(); i++)
                    {
                        availableEmailAppsName[i] = emailActivitiesForDialog.get(i).activityInfo.applicationInfo.loadLabel(pm).toString();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(p_context);
                    builder.setTitle("Send Email using");
                    builder.setItems(availableEmailAppsName, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            sendEmailUsingSelectedEmailApp(p_context, mailto, p_subject, p_body, p_attachments, emailActivitiesForDialog.get(which));
                        }
                    });

                    builder.create().show();
                }
            }
            else
            {
                sendEmailUsingSelectedEmailApp(p_context, mailto, p_subject, p_body, p_attachments, null);
            }
        }
        catch (Exception ex)
        {
            //Log.e(TAG, "Can't send email", ex);
            Toast.makeText(ProfileDetails.this,"Can't Send Email "+ ex, Toast.LENGTH_LONG).show();

        }
    }

    protected void sendEmailUsingSelectedEmailApp(Context p_context,String mailto, String p_subject, String p_body, ArrayList<String> p_attachments, ResolveInfo p_selectedEmailApp)
    {
        try
        {
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            String aEmailList[] = { mailto};

            emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, null != p_subject ? p_subject : "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, null != p_body ? p_body : "");

            if (null != p_attachments && p_attachments.size() > 0)
            {
                ArrayList<Uri> attachmentsUris = new ArrayList<Uri>();

                // Convert from paths to Android friendly Parcelable Uri's
                for (String currAttachemntPath : p_attachments)
                {
                    File fileIn = new File(currAttachemntPath);
                    Uri currAttachemntUri = Uri.fromFile(fileIn);
                    attachmentsUris.add(currAttachemntUri);
                }
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentsUris);
            }

            if (null != p_selectedEmailApp)
            {
                //Log.d(TAG, "Sending email using " + p_selectedEmailApp);
                //Toast.makeText(ContactUs.this,"Error sending Email "+p_selectedEmailApp, Toast.LENGTH_LONG).show();
                emailIntent.setComponent(new ComponentName(p_selectedEmailApp.activityInfo.packageName, p_selectedEmailApp.activityInfo.name));

                p_context.startActivity(emailIntent);
            }
            else
            {
                Intent emailAppChooser = Intent.createChooser(emailIntent, "Select Email app");

                p_context.startActivity(emailAppChooser);
            }
        }
        catch (Exception ex)
        {
            //Log.e(TAG, "Error sending email", ex);
            //Toast.makeText(ContactUs.this,"Error sending Email",Toast.LENGTH_LONG).show();
        }
    }
}