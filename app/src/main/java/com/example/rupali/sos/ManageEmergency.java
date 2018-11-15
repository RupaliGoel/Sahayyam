package com.example.rupali.sos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ManageEmergency extends Fragment {

    ListView EmergencyListView;
    ArrayList<String> EmergencyTypes;

    ArrayList<Emergency> EmergencyList;

    String user_email;

    Emergency emergency;

    JSONParser jsonParser = new JSONParser();
    private static String url_emergency_details = "https://sahayyam.000webhostapp.com/get_my_emergencies.php";
    String email,desc,type,address;
    Double lat,lon;
    int success = 0;
    View progressOverlay;

    SharedPreferences prefs;

    public ManageEmergency() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmergencyTypes=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_emergency, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        user_email = prefs.getString("user_email","admin");

        EmergencyListView = view.findViewById(R.id.EmergencyListView);
        new GetEmergency().execute();
    }

    class GetEmergency extends AsyncTask<String,String,String> {

        /**
         * Before starting background thread Show Progress Dialog
         */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Show progress overlay (with animation):
//            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        }

        @Override
        protected String doInBackground(String... args) {

            try {

                EmergencyList = new ArrayList<Emergency>();

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", user_email));

                JSONObject jsonObject = jsonParser.makeHttpRequest(url_emergency_details, "POST", params);
                try {
                    if (jsonObject.getInt("success") == 1) {

                        JSONArray jsonArray = jsonObject.getJSONArray("Emergencies");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            emergency = new Emergency();

                            JSONObject json = jsonArray.getJSONObject(i);
                            Emergency emergency = new Emergency();

                            emergency.Emergency_Id = json.getInt("emer_id");
                            emergency.Emergency_Name = json.getString("emer_title");
                            emergency.Emergency_Desc = json.getString("emer_desc");
                            emergency.User_Email = json.getString("user_email");
                            email = emergency.User_Email;
                            lat = Double.parseDouble(json.getString("emer_place_lat"));
                            lon = Double.parseDouble(json.getString("emer_place_long"));
                            emergency.Emergency_Lat = lat;
                            emergency.Emergency_Long = lon;
                            //address = getAddress(getActivity().getApplicationContext(),lat,lon);
                                   /* image = jsonObject.getInt("emer_image");
                                    emergency.Emergency_Image = image;*/
                            EmergencyList.add(emergency);
                        }
                    } else {
                        System.out.println("No Record Found");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
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

                EmergencyListAdapter adapter = new EmergencyListAdapter(EmergencyList, getActivity().getApplicationContext());

                EmergencyListView.setAdapter(adapter);

                EmergencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try{
                            Intent myIntent = new Intent(view.getContext(), PostViewNDelete.class);
                                        /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), image);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();*/
                            Emergency emer = EmergencyList.get(position);
                            type = emer.Emergency_Name;
                            desc = emer.Emergency_Desc;
                            address = getCompleteAddressString(emer.Emergency_Lat, emer.Emergency_Long);
                            desc = desc+"\n\nAddress : "+address;
                            int emer_id = emer.Emergency_Id;
                            myIntent.putExtra("Id",emer_id);
                            myIntent.putExtra("Title",type);
                            myIntent.putExtra("Type","emergency");
                            myIntent.putExtra("Content", desc);
//                                      myIntent.putExtra("Picture", byteArray);
                            startActivity(myIntent);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
            // Hide it (with animation):
            //AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);

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
    }

    /*public static String getAddress(Context context, double LATITUDE, double LONGITUDE) {

        String fulladdress = "";
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {



                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                if(address!=null)
                    fulladdress = address;
                if(city!=null)
                    fulladdress = fulladdress +","+ city;
                if(state!=null)
                    fulladdress = fulladdress +","+ state;
                if(country!=null)
                    fulladdress = fulladdress +","+ country;
                if(postalCode!=null)
                    fulladdress = fulladdress +","+ postalCode;
                if(knownName!=null)
                    fulladdress = fulladdress +","+ knownName;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fulladdress;
    }
*/
}
