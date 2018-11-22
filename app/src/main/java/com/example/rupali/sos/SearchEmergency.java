package com.example.rupali.sos;

import android.Manifest;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import android.widget.ProgressBar;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;

import static android.app.Activity.RESULT_OK;

public class SearchEmergency extends Fragment {

    public SearchEmergency() {
        // Required empty public constructor
    }

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    //--------------------------------get location-----------------------------------------
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude, longitude;
    double searchlat,searchlong;
    JSONParser jsonParser = new JSONParser();
    //---------------------------------get location-----------------------------------------

    //-------------------------------listview---------------------------------------------------

    ListView EmergencyListView;
    ClearableEditText searchByText;
    JSONArray jsonArray = null;
    JSONObject jsonObject;

    double latti,longi;
    String addressOfUser;
    public static String HttpURL = "https://sahayyam.000webhostapp.com/get_emergencies.php";
    String name,desc,emailpost;
    String changeaddress;
    double lat,lon;
    double distance;
    int PLACE_PICKER_REQUEST = 2;

    View progressOverlay;
    ArrayList<Emergency> EmergencyList ;
    //-------------------------------listview---------------------------------------------------

    //-------------------------------toolbar, location textbox & button-----------------------------------
    AutoCompleteTextView locationedit;
    ImageButton getlocationbtn,audio_mode,placepickerbtn;
    Button go;
    private android.support.v7.widget.Toolbar page_name;
    //-------------------------------toolbar, location textbox & button-----------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_emergency, container, false);

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        editor = prefs.edit();

        //-------------------------------toolbar, location textbox & button-----------------------------------
        ((AppCompatActivity)getActivity()).setTitle(null);
        page_name = (Toolbar) getActivity().findViewById(R.id.page_name);
        ((AppCompatActivity)getActivity()).setSupportActionBar(page_name);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        locationedit = view.findViewById(R.id.currentLocation);
        getlocationbtn = view.findViewById(R.id.LocationButton);
        go = view.findViewById(R.id.GoButton);
        audio_mode = view.findViewById(R.id.audioModeButton);
        searchByText = view.findViewById(R.id.textSearch);
        placepickerbtn = view.findViewById(R.id.placepickerbtn);

        getlocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-----------------------------get location-------------------------------------------------
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION },
                        REQUEST_LOCATION);
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                    //getLocation();
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation();
                }
                //--------------------------------get location----------------------------------------------
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeaddress = locationedit.getText().toString();
                editor.putString("user_current_address",changeaddress).commit();
                convertAddress();
                new getEmergency().execute();
                /*InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/
            }
        });

        placepickerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        progressOverlay = view.findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        //-------------------------------toolbar, location textbox & button-----------------------------------



        EmergencyListView = (ListView) view.findViewById(R.id.EmergencyListView);
        new getEmergency().execute();
        //----------------------------------listview------------------------------------------------
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == RESULT_OK && resultCode == 1 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ivImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }*/
        if (resultCode == RESULT_OK )
        {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, getActivity().getApplicationContext());
                String toastMsg = String.format("%s", place.getAddress());
                LatLng latlongLocation = place.getLatLng();
                locationedit.setText(toastMsg);
                searchlat = latlongLocation.latitude;
                searchlong = latlongLocation.longitude;
                new getEmergency().execute();
                editor.putString("user_current_address",toastMsg).commit();
            }
        }

    }



    //----------------------------------------get location----------------------------------------------
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                latti = location.getLatitude();
                longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                String address = getCompleteAddressString(latti,longi);
                addressOfUser = address;
                locationedit.setText(address);

            } else  if (location1 != null) {
                latti = location1.getLatitude();
                longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                String address = getCompleteAddressString(latti,longi);
                addressOfUser = address;
                locationedit.setText(address);


            } else  if (location2 != null) {
                latti = location2.getLatitude();
                longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                String address = getCompleteAddressString(latti,longi);
                addressOfUser = address;
                locationedit.setText(address);

            }else{

                Toast.makeText(getActivity().getApplicationContext(),"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }

                searchlat = Double.parseDouble(lattitude);
                searchlong = Double.parseDouble(longitude);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("lat",lattitude).commit();
                editor.putString("long",longitude).commit();
                editor.putString("user_current_address",addressOfUser).commit();

        }


    }

    protected void buildAlertMessageNoGps() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        builder.setTitle("GPS is off");
        builder.setMessage("Please turn on gps to use the App.");
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
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
    //---------------------------------------get location--------------------------------------

    class getEmergency extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Show progress overlay (with animation):
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        }

        protected String doInBackground(String... args) {

            try {

                if ((searchByText.getText().toString()).equals("")) {

                    EmergencyList = new ArrayList<Emergency>();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    try {

                        JSONObject jsonObject = jsonParser.makeHttpRequest(HttpURL, "POST", params);

                        if (jsonObject.getInt("success") == 1) {

                            JSONArray jsonArray = jsonObject.getJSONArray("Emergencies");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);
                                Emergency emergency = new Emergency();

                                emergency.Emergency_Name = json.getString("emer_title");
                                emergency.Emergency_Desc = json.getString("emer_desc");
                                emergency.User_Email = json.getString("user_email");
                                emergency.Emergency_Lat = Double.parseDouble(json.getString("emer_place_lat"));
                                emergency.Emergency_Long = Double.parseDouble(json.getString("emer_place_long"));

                                if(!((locationedit.getText().toString()).equals(""))){
                                    distance = getDistance(searchlat, searchlong, emergency.Emergency_Lat, emergency.Emergency_Long);
                                    emergency.Emergency_Distance = distance;
                                }
                                String image = json.getString("emer_image");
                                emergency.Emergency_Image = image;
                                EmergencyList.add(emergency);
                            }

                        }
                        else
                        {
                            System.out.println("No Record Found");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    System.out.println("SEARCH BY TEXT IS USED");
                    EmergencyList = new ArrayList<Emergency>();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("text", searchByText.getText().toString().trim()));

                    try {

                        JSONObject jsonObject = jsonParser.makeHttpRequest(HttpURL, "POST", params);

                        if (jsonObject.getInt("success") == 1) {

                            JSONArray jsonArray = jsonObject.getJSONArray("Emergencies");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);
                                Emergency emergency = new Emergency();

                                emergency.Emergency_Name = json.getString("emer_title");
                                emergency.Emergency_Desc = json.getString("emer_desc");
                                emergency.User_Email = json.getString("user_email");
                                emergency.Emergency_Lat = Double.parseDouble(json.getString("emer_place_lat"));
                                emergency.Emergency_Long = Double.parseDouble(json.getString("emer_place_long"));
                                distance = getDistance(searchlat, searchlong, emergency.Emergency_Lat, emergency.Emergency_Long);
                                emergency.Emergency_Distance = distance;
                            /* image = jsonObject.getInt("emer_image");
                            emergency.Emergency_Image = image;*/

                                String image = json.getString("emer_image");
                                emergency.Emergency_Image = image;
                                EmergencyList.add(emergency);
                            }

                        }
                        else
                        {
                            System.out.println("No Record Found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        public ArrayList<Emergency> getList()
        {
            return EmergencyList;
        }

        protected void onPostExecute(String file_url)
        {

            EmergencyListView.setVisibility(View.VISIBLE);

            if (EmergencyList != null) {

                if(!((locationedit.getText().toString()).equals(""))) {

                    Collections.sort(EmergencyList, new Comparator<Emergency>() {
                        @Override
                        public int compare(Emergency p1, Emergency p2) {
                            return ((int) Math.round(p1.getEmergency_Distance())) - ((int) Math.round(p2.getEmergency_Distance())); // Ascending
                        }
                    });
                }

                EmergencyListAdapter adapter = null;

                if(isAdded())
                    adapter = new EmergencyListAdapter(EmergencyList, getActivity().getApplicationContext());

                EmergencyListView.setAdapter(adapter);

                EmergencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent myIntent = new Intent(view.getContext(), EmergencyPost.class);
                        Emergency sendEmergency = EmergencyList.get(position);
                        name = sendEmergency.Emergency_Name;
                        desc = sendEmergency.Emergency_Desc;
                        emailpost = sendEmergency.User_Email;
                        distance =sendEmergency.Emergency_Distance;
                        String imageuri = sendEmergency.Emergency_Image;
                        double emergency_lat = sendEmergency.Emergency_Lat;
                        double emergency_long = sendEmergency.Emergency_Long;
                        String emergency_address = getCompleteAddressString(emergency_lat,emergency_long);
                        /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), image);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();*/
                        myIntent.putExtra("Headline", name);
                        myIntent.putExtra("Content", desc);
                        myIntent.putExtra("Distance",distance);
                        myIntent.putExtra("Email",emailpost);
                        myIntent.putExtra("ImageURI",imageuri);
                        myIntent.putExtra("Emer_lat",emergency_lat);
                        myIntent.putExtra("Emer_long",emergency_long);
                        myIntent.putExtra("Emer_address",emergency_address);
//                        myIntent.putExtra("Picture", byteArray);
                        startActivity(myIntent);
                    }
                });
            }

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
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
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

}