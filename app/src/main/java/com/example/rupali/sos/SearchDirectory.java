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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

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
    String addressOfUser, nameOfUser, roleOfUser, contactOfUser, emailOfUser;
    String currentAddressOfUser, changeaddress;
    ListView DirectoryListView;
    JSONArray jsonArray = null;
    JSONObject jsonObject;
    User user;
    double searchlat,searchlong,lati,longi;

    private static String url_details = "https://sahayyam.000webhostapp.com/get_users_by_role.php";
    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";

    String email,role,contact,address,name;
    Double lat,lon;
    int success = 0;

    ArrayList<String> roleTypes;
    Spinner spinner;
    View progressOverlay;
    int PLACE_PICKER_REQUEST = 1;


    //-------------------------------toolbar, location textbox & button-----------------------------------
    AutoCompleteTextView locationedit;
    ImageButton audio_mode,placepickerbtn;
    Button go;
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
        placepickerbtn = findViewById(R.id.placepickerbtn);
        audio_mode = findViewById(R.id.audioModeButton);
        go = findViewById(R.id.GoButton);
        spinner = findViewById(R.id.role);
        //-------------------------------toolbar, location textbox & button-----------------------------------
        DirectoryListView = findViewById(R.id.DirectoryListView);

        placepickerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SearchDirectory.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

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
                changeaddress = locationedit.getText().toString();
                editor.putString("user_current_address",changeaddress).commit();
                convertAddress();
                new GetDirectory(SearchDirectory.this).execute();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, SearchDirectory.this);
                String toastMsg = String.format("%s", place.getAddress());
                locationedit.setText(toastMsg);
            }
        }

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
                                convertAddress(user.User_Address);
                                if(!((locationedit.getText().toString()).equals(""))){
                                    double distance = getDistance(searchlat, searchlong, lati, longi);
                                    user.User_Distance = distance;
                                }

                                //lat = Double.parseDouble(jsonObject.getString("user_address_lat"));
                                //lon = Double.parseDouble(jsonObject.getString("user_address_long"));
                                //address = getCompleteAddressString(lat,lon);

                           /* image = jsonObject.getInt("emer_image");
                            emergency.Emergency_Image = image;*/
                                DirectoryList.add(user);
                            }

                            DirectoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        Intent myIntent = new Intent(view.getContext(), ProfileDetails.class);
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

                                        myIntent.putExtra("email",email);

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

                Collections.sort(DirectoryList, new Comparator<User>() {
                    @Override
                    public int compare(User p1, User p2) {
                        return ((int) Math.round(p1.User_Distance)) - ((int) Math.round(p2.User_Distance)); // Ascending
                    }
                });

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

    private double getDistance(double fromLat, double fromLon, double toLat, double toLon){
        double radius = 6371;   // Earth radius in km
        double deltaLat = Math.toRadians(toLat - fromLat);
        double deltaLon = Math.toRadians(toLon - fromLon);
        double lat1 = Math.toRadians(fromLat);
        double lat2 = Math.toRadians(toLat);
        double aVal = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
                Math.sin(deltaLon/2) * Math.sin(deltaLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double cVal = 2*Math.atan2(Math.sqrt(aVal), Math.sqrt(1-aVal));

        double distance = radius*cVal;
        Log.d("distance","radius * angle = " +distance);
        return distance;
    }

    public void convertAddress() {
        Geocoder geocoder = new Geocoder(SearchDirectory.this, Locale.getDefault());
        if (changeaddress != null && !changeaddress.isEmpty()) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(changeaddress, 1);
                if (addressList != null && addressList.size() > 0) {
                    searchlat = addressList.get(0).getLatitude();
                    searchlong = addressList.get(0).getLongitude();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    } // end convertAddress

    public void convertAddress(String address) {
        Geocoder geocoder = new Geocoder(SearchDirectory.this, Locale.getDefault());
        if (address != null && !address.isEmpty()) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    lati = addressList.get(0).getLatitude();
                    longi = addressList.get(0).getLongitude();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    } // end convertAddress

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