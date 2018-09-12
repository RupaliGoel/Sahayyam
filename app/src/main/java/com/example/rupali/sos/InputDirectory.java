package com.example.rupali.sos;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

public class InputDirectory extends AppCompatActivity {
    public InputDirectory() {
        // Required empty public constructor
    }

    //--------------------------------get location-----------------------------------------
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude, longitude;
    //---------------------------------get location-----------------------------------------

    //-------------------------------listview---------------------------------------------------
    int[] images = {R.drawable.ananta,
            R.drawable.anchal,
            R.drawable.nikita,
            R.drawable.rupali,
            R.drawable.salwi,
            R.drawable.vibhuti};

    String[] incidents = {"Sleeping",
            "Jumping",
            "Chatting",
            "Studying",
            "Travelling",
            "Eating"};

    //-------------------------------listview---------------------------------------------------

    //-------------------------------toolbar, location textbox & button-----------------------------------
    EditText locationedit;
    ImageButton audio_mode;
    Button change;
    private android.support.v7.widget.Toolbar input_dir;
    //-------------------------------toolbar, location textbox & button-----------------------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_directory);

        //-------------------------------toolbar, location textbox & button-----------------------------------
        setTitle(null);
        input_dir = (Toolbar) findViewById(R.id.input_dir);
        setSupportActionBar(input_dir);

        locationedit = findViewById(R.id.currentLocation);
        change = findViewById(R.id.changeButton);
        audio_mode = findViewById(R.id.audioModeButton);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationedit.setText("");
                locationedit.setFocusableInTouchMode(true);
            }
        });
        //-------------------------------toolbar, location textbox & button-----------------------------------

        //-----------------------------get location-------------------------------------------------
        ActivityCompat.requestPermissions(InputDirectory.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
        //--------------------------------get location----------------------------------------------

        //-------------------------------listview---------------------------------------------------
        ListView listView;
        listView = (ListView) findViewById(R.id.EmergencyListView);
        final InputDirectory.CustomAdapter customAdapter = new InputDirectory.CustomAdapter();
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
        });
        //----------------------------------listview------------------------------------------------
    }


    //----------------------------------------get location----------------------------------------------
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(InputDirectory.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                //lattitude = String.valueOf(latti);
                //longitude = String.valueOf(longi);
                String address = getCompleteAddressString(latti, longi);
                locationedit.setText(address);

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                //lattitude = String.valueOf(latti);
                //longitude = String.valueOf(longi);
                String address = getCompleteAddressString(latti, longi);
                locationedit.setText(address);


            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                //lattitude = String.valueOf(latti);
                //longitude = String.valueOf(longi);
                String address = getCompleteAddressString(latti, longi);
                locationedit.setText(address);

            } else {

                Toast.makeText(getApplicationContext(), "Unable to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
    //---------------------------------------get location--------------------------------------

//    public void addFragment(Fragment SearchEmergency){
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.container,SearchEmergency);
//        ft.addToBackStack(null);
//        Toast.makeText(getActivity().getApplicationContext(),"thailand",Toast.LENGTH_LONG).show();
//        ft.commit();
//    }


    //-------------------------------listview---------------------------------------------------
    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return incidents.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.custom_list_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.EmergencyImageView);
            TextView textView = view.findViewById(R.id.EmergencyTextView);
            imageView.setImageResource(images[position]);
            textView.setText(incidents[position]);
            return view;
        }
    }
    //-------------------------------listview---------------------------------------------------
}