package com.example.rupali.sos;

//Post to show onclick list items

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppealPost extends AppCompatActivity {

    TextView typePost, descriptionPost;
    ImageView imagePost;
    Toolbar page_name;
    ImageButton emailbtn,call;
    View progressOverlay;

    TextView tvName,tvContact,tvEmail,tvRole;

    String email,name,role1,role2,role3,contact;
    JSONParser jsonParser = new JSONParser();
    private static String url_user_details = "https://sahayyam.000webhostapp.com/get_user_details.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeal_post);

        page_name = findViewById(R.id.page_name);
        setSupportActionBar(page_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString("Type");
        String content = bundle.getString("Content");
        email = bundle.getString("Email");

        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();

        new GetDetails().execute();


//        byte[] byteArray = bundle.getByteArray("Picture");

        /*Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        imagePost.setImageBitmap(bmp);*/

        typePost = findViewById(R.id.titlePost);
        typePost.setText(type);
        descriptionPost = findViewById(R.id.descriptionPost);
        descriptionPost.setText(content);



    }

    public void call_action(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        String num= tvContact.getText().toString();
        String uri= "tel:"+ num.trim();
        callIntent.setData(Uri.parse(uri));
        try {
            startActivity(callIntent);
        }
        catch (SecurityException e) {
            Toast.makeText(getApplicationContext(),"Calling Permission Required.",Toast.LENGTH_LONG).show();
        }
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

            // Hide it (with animation):
            AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);

            emailbtn = findViewById(R.id.mail);
            emailbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail(AppealPost.this,email,"","",null);
                }
            });

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
            Toast.makeText(AppealPost.this,"Can't Send Email "+ ex, Toast.LENGTH_LONG).show();

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
