package com.example.rupali.sos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SearchDirectory extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currentlattitude, currentlongitude, addressOfUser, nameOfUser, roleOfUser, contactOfUser, emailOfUser;
    String currentAddressOfUser;
    ListView DirectoryListView;
    JSONArray jsonArray = null;
    JSONObject jsonObject;
    User user;

    private static String url_details = "https://sahayyam.000webhostapp.com/get_users_by_role.php";
    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";

    String email,role,contact,address,name;
    Double lat,lon;
    int success = 0;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    ArrayList<String> roleTypes;
    Spinner spinner;
    View progressOverlay;


    //-------------------------------toolbar, location textbox & button-----------------------------------
    AutoCompleteTextView locationedit;
    ImageButton audio_mode;
    Button change, go;
    private android.support.v7.widget.Toolbar search_dir;
    //-------------------------------toolbar, location textbox & button-----------------------------------

    public SearchDirectory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_directory);

        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        // Show progress overlay (with animation):
        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        roleTypes=new ArrayList<>();


        //-------------------------------toolbar, location textbox & button-----------------------------------

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        currentlattitude = prefs.getString("lat", "None");
        currentlongitude = prefs.getString("long", "None");
        emailOfUser = prefs.getString("user_email", "Not Found");
        addressOfUser = prefs.getString("user_address", "Not Found");
        currentAddressOfUser = prefs.getString("user_current_address", "Not Found");
        nameOfUser = prefs.getString("user_name", "Guest");
        roleOfUser = prefs.getString("user_role", "Not Found");
        contactOfUser = prefs.getString("user_contact", "Not Found");

        setTitle(null);
        search_dir = (Toolbar) findViewById(R.id.search_dir);
        setSupportActionBar(search_dir);

        locationedit = findViewById(R.id.currentLocation);
        locationedit.setText(currentAddressOfUser);
        change = findViewById(R.id.changeButton);
        audio_mode = findViewById(R.id.audioModeButton);
        go = findViewById(R.id.GoButton);
        spinner = findViewById(R.id.role);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationedit.setText("");
                locationedit.setFocusableInTouchMode(true);
            }
        });
        //-------------------------------toolbar, location textbox & button-----------------------------------
        DirectoryListView = findViewById(R.id.DirectoryListView);

        loadSpinnerData(URL);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                // TODO Auto-generated method stub
                String selected_option = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

                Toast.makeText(SearchDirectory.this,selected_option,Toast.LENGTH_LONG).show();

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetDirectory(SearchDirectory.this).execute();
            }
        });


    }

    /**
     * Background Async Task to get username
     * */
    private class GetDirectory extends AsyncTask<Void, Void, Void> {
        public Context context;
        String FinalJSonResult;
        List<User> DirectoryList;

        public GetDirectory(Context context) {

            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Show progress overlay (with animation):
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpServiceClass httpServiceClass = new HttpServiceClass(url_details);
            httpServiceClass.AddParam("role", spinner.getSelectedItem().toString());

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {
                        try {

                            jsonArray = new JSONArray(FinalJSonResult);

                            int image;

                            DirectoryList = new ArrayList<User>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                user = new User();

                                jsonObject = jsonArray.getJSONObject(i);

                                user.User_Email = jsonObject.getString("user_email");

                                user.User_Name = jsonObject.getString("user_name");

                                if(!((jsonObject.getString("user_role1")).equals("")) && !((jsonObject.getString("user_role2")).equals("")) && !((jsonObject.getString("user_role3")).equals(""))){
                                    user.User_Role = jsonObject.getString("user_role1") + ", " + jsonObject.getString("user_role2") + ", " + jsonObject.getString("user_role3");
                                }
                                else if(!((jsonObject.getString("user_role2")).equals("")) && ((jsonObject.getString("user_role3")).equals(""))){
                                    user.User_Role = jsonObject.getString("user_role1") + ", " + jsonObject.getString("user_role2");
                                }
                                else if(((jsonObject.getString("user_role2")).equals("")) && ((jsonObject.getString("user_role3")).equals(""))){
                                    user.User_Role = jsonObject.getString("user_role1");
                                }

                                user.User_Contact = jsonObject.getString("user_contact");

                                user.User_Address = jsonObject.getString("user_address");

                                //lat = Double.parseDouble(jsonObject.getString("user_address_lat"));
                                //lon = Double.parseDouble(jsonObject.getString("user_address_long"));
                                //address = getCompleteAddressString(lat,lon);

                           /* image = jsonObject.getInt("emer_image");
                            emergency.Emergency_Image = image;*/
                                System.out.println("All details saved in object."+user.User_Email+user.User_Name+user.User_Role+user.User_Contact);
                                DirectoryList.add(user);
                            }

                            DirectoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        Intent myIntent = new Intent(view.getContext(), DirectoryUserInfo.class);
                                        /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), image);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();*/
                                        User clickedUser = DirectoryList.get(position);
                                        email = clickedUser.User_Email;
                                        name = clickedUser.User_Name;
                                        role = clickedUser.User_Role;
                                        contact = clickedUser.User_Contact;
                                        address = clickedUser.User_Address;

                                        myIntent.putExtra("Role",role);
                                        myIntent.putExtra("Name", name);
                                        myIntent.putExtra("Email",email);
                                        myIntent.putExtra("Contact",contact);
                                        myIntent.putExtra("Address",address);
//                                      myIntent.putExtra("Picture", byteArray);
                                        startActivity(myIntent);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {

                    Toast.makeText(context, httpServiceClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)

        {

            if (DirectoryList != null) {

                DirectoryListAdapter adapter = new DirectoryListAdapter(DirectoryList, context);

                DirectoryListView.setAdapter(adapter);

                DirectoryListView.setVisibility(View.VISIBLE);
            }
            else
                DirectoryListView.setVisibility(View.GONE);

            // Hide it (with animation):
            AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);

        }
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Current loction address", "Cannot get Address!");
        }
        return strAdd;
    }

    private void loadSpinnerData(String url) {

        RequestQueue requestQueue=Volley.newRequestQueue(SearchDirectory.this);

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

                    if(SearchDirectory.this !=null)
                        spinner.setAdapter(new ArrayAdapter<String>(SearchDirectory.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));

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