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
import java.util.List;
import java.util.Locale;

import javax.security.auth.Subject;

public class SearchEmergency extends Fragment {

    public SearchEmergency() {
        // Required empty public constructor
    }

    //--------------------------------get location-----------------------------------------
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude, longitude;
    double searchlat,searchlong;
    //---------------------------------get location-----------------------------------------

    //-------------------------------listview---------------------------------------------------

    ListView EmergencyListView;
    JSONArray jsonArray = null;
    JSONObject jsonObject;
    Emergency emergency;
    double latti,longi;
    String addressOfUser;

    String HttpURL = "https://sahayyam.000webhostapp.com/get_emergencies.php";
    String name,desc,emailpost;
    String changeaddress;
    double lat,lon;
    double distance;

    View progressOverlay;
    //-------------------------------listview---------------------------------------------------

    //-------------------------------toolbar, location textbox & button-----------------------------------
    AutoCompleteTextView locationedit;
    ImageButton audio_mode;
    Button change,go;
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

        //-------------------------------toolbar, location textbox & button-----------------------------------
        ((AppCompatActivity)getActivity()).setTitle(null);
        page_name = (Toolbar) getActivity().findViewById(R.id.page_name);
        ((AppCompatActivity)getActivity()).setSupportActionBar(page_name);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        locationedit = view.findViewById(R.id.currentLocation);
        change = view.findViewById(R.id.changeButton);
        go = view.findViewById(R.id.GoButton);
        audio_mode = view.findViewById(R.id.audioModeButton);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationedit.setText("");
                locationedit.setFocusableInTouchMode(true);
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeaddress = locationedit.getText().toString();
                convertAddress();
                new ParseJSonDataClass(getActivity()).execute();
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        progressOverlay = view.findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        //-------------------------------toolbar, location textbox & button-----------------------------------

        //-----------------------------get location-------------------------------------------------
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION },
                REQUEST_LOCATION);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
        //--------------------------------get location----------------------------------------------

        //-------------------------------listview---------------------------------------------------
        /*ListView listView;
        listView = (ListView) view.findViewById(R.id.EmergencyListView);
        final SearchEmergency.CustomAdapter customAdapter = new SearchEmergency.CustomAdapter();
        final int count = customAdapter.getCount();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < count; i++) {
                    if (position == i) {
                        Intent myIntent = new Intent(view.getContext(), EmergencyPost.class);
                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_post);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        String headline = "This is title of the post.";
                        String content = "Post Description.";
                        myIntent.putExtra("Headline", headline);
                        myIntent.putExtra("Content", content);
                        myIntent.putExtra("Picture", byteArray);
                        startActivityForResult(myIntent, i);
                    }
                }
            }
        });*/

        EmergencyListView = (ListView) view.findViewById(R.id.EmergencyListView);

        new ParseJSonDataClass(getActivity()).execute();
        //----------------------------------listview------------------------------------------------
    }



//----------------------------------------get location----------------------------------------------
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

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
        }

        searchlat = Double.parseDouble(lattitude);
        searchlong = Double.parseDouble(longitude);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lat",lattitude).commit();
        editor.putString("long",longitude).commit();
        editor.putString("user_current_address",addressOfUser).commit();
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

//    public void addFragment(Fragment SearchEmergency){
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.container,SearchEmergency);
//        ft.addToBackStack(null);
//        Toast.makeText(getActivity().getApplicationContext(),"thailand",Toast.LENGTH_LONG).show();
//        ft.commit();
//    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {
        public Context context;
        String FinalJSonResult;
        List<Emergency> EmergencyList;

        public ParseJSonDataClass(Context context) {

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

            HttpServiceClass httpServiceClass = new HttpServiceClass(HttpURL);

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {


                        try {

                            jsonArray = new JSONArray(FinalJSonResult);

                            int image;

                            EmergencyList = new ArrayList<Emergency>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                emergency = new Emergency();

                                jsonObject = jsonArray.getJSONObject(i);

                                emergency.Emergency_Name = jsonObject.getString("emer_title");
                                emergency.Emergency_Desc = jsonObject.getString("emer_desc");
                                emergency.User_Email = jsonObject.getString("user_email");
                                emergency.Emergency_Lat = Double.parseDouble(jsonObject.getString("emer_place_lat"));
                                emergency.Emergency_Long = Double.parseDouble(jsonObject.getString("emer_place_long"));
                                distance = getDistance(searchlat,searchlong,emergency.Emergency_Lat,emergency.Emergency_Long);
                                emergency.Emergency_Distance = distance;
                               /* image = jsonObject.getInt("emer_image");
                                emergency.Emergency_Image = image;*/

                                EmergencyList.add(emergency);
                            }

                            ///////////////////////////////////////////////////////////
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

            EmergencyListView.setVisibility(View.VISIBLE);

            if (EmergencyList != null) {

                Collections.sort(EmergencyList, new Comparator<Emergency>() {
                    @Override public int compare(Emergency p1, Emergency p2) {
                        return ((int)Math.round(p1.getEmergency_Distance()))- ((int)Math.round(p2.getEmergency_Distance())); // Ascending
                    }
                });

                EmergencyListAdapter adapter = new EmergencyListAdapter(EmergencyList, context);

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
                        /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), image);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();*/
                        myIntent.putExtra("Headline", name);
                        myIntent.putExtra("Content", desc);
                        myIntent.putExtra("Distance",distance);
                        myIntent.putExtra("Email",emailpost);
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
