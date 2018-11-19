package com.example.rupali.sos;

import android.app.ProgressDialog;
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

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class ManageAppeal extends Fragment {

    ListView AppealListView;
    ArrayList<String> appealTypes;

    String user_email;

    Appeal appeal;

    ArrayList<Appeal> AppealList;

    JSONParser jsonParser = new JSONParser();
    private static String url_appeal_details = "https://sahayyam.000webhostapp.com/get_my_appeals.php";
    String email,desc,type,address;
    Double lat,lon;
    int success = 0;
    View progressOverlay;

    SharedPreferences prefs;


    public ManageAppeal() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appealTypes=new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_appeal, container, false);

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressOverlay=view.findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();

        // Show progress overlay (with animation):
        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);


        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        user_email = prefs.getString("user_email","admin");

        AppealListView = view.findViewById(R.id.AppealListView);
        new GetAppeals().execute();
    }

    class GetAppeals extends AsyncTask<String,String,String> {

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

                AppealList = new ArrayList<Appeal>();

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", user_email));

                JSONObject jsonObject = jsonParser.makeHttpRequest(url_appeal_details, "POST", params);
                try {
                    if (jsonObject.getInt("success") == 1) {

                        JSONArray jsonArray = jsonObject.getJSONArray("Appeals");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            appeal = new Appeal();

                            JSONObject json = jsonArray.getJSONObject(i);
                            Appeal appeal = new Appeal();

                            appeal.Appeal_Id = json.getInt("appeal_id");
                            appeal.Appeal_Type = json.getString("appeal_type");
                            appeal.Appeal_Desc = json.getString("appeal_desc");
                            appeal.User_email = json.getString("user_email");
                            email = appeal.User_email;
                            lat = Double.parseDouble(json.getString("user_address_lat"));
                            lon = Double.parseDouble(json.getString("user_address_long"));
                            address = getCompleteAddressString(lat, lon);

                                   /* image = jsonObject.getInt("emer_image");
                                    emergency.Emergency_Image = image;*/
                            System.out.println("All details saved in object." + appeal.Appeal_Desc + appeal.Appeal_Type + appeal.User_email);
                            AppealList.add(appeal);
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
        public ArrayList<Appeal> getList()
        {
            return AppealList;
        }

        protected void onPostExecute(String file_url)
        {

            AppealListView.setVisibility(View.VISIBLE);

            if (AppealList != null) {

                AppealListAdapter adapter = new AppealListAdapter(AppealList, getActivity());

                AppealListView.setAdapter(adapter);

                AppealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try{
                                       Intent myIntent = new Intent(view.getContext(), PostViewNDelete.class);
                                        /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), image);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();*/
                                        Appeal app = AppealList.get(position);
                                        type = app.Appeal_Type;
                                        desc = app.Appeal_Desc;
                                        email = app.User_email;
                                        int appeal_id = app.Appeal_Id;
                                        myIntent.putExtra("Id",appeal_id);
                                        myIntent.putExtra("Title",type);
                                        myIntent.putExtra("Type","appeal");
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
            AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);

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
}


