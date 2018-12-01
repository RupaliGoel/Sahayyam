package com.example.rupali.sos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SearchAppeal extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currentlattitude, currentlongitude, addressOfUser, nameOfUser, roleOfUser, contactOfUser, emailOfUser;

    ListView AppealListView;
    JSONArray jsonArray = null;
    JSONObject jsonObject;
    Appeal appeal;
    String image;
    Spinner spinner;
    String chosenType ;
    ArrayList<String> appealTypes;

    String toolbarMessage;
    Toolbar toolbar;
    TextView appnametv;


    private static String url_appeal_details = "https://sahayyam.000webhostapp.com/get_appeals.php";
    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";
    String email,desc,type,address;
    Double lat,lon;
    int success = 0;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    //-------------------------------toolbar, location textbox & button-----------------------------------
    EditText textbox;
    ImageButton audio_mode;
    Button go;
    private androidx.appcompat.widget.Toolbar search_app;
    View progressOverlay;
    //-------------------------------toolbar, location textbox & button-----------------------------------

    public SearchAppeal() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appeal);

        //-------------------------------toolbar, location textbox & button-----------------------------------

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        // Show progress overlay (with animation):
        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        appealTypes=new ArrayList<>();

        currentlattitude = prefs.getString("lat", "None");
        currentlongitude = prefs.getString("long", "None");
        emailOfUser = prefs.getString("user_email", "Not Found");
        addressOfUser = prefs.getString("user_address", "Not Found");
        nameOfUser = prefs.getString("user_name", "Guest");
        roleOfUser = prefs.getString("user_role", "Not Found");
        contactOfUser = prefs.getString("user_contact", "Not Found");
        toolbarMessage = prefs.getString("AppName","App");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appnametv = (TextView)findViewById(R.id.appname);
        appnametv.setText(toolbarMessage);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        setTitle(null);
        search_app = (Toolbar) findViewById(R.id.search_dir);
        setSupportActionBar(search_app);

        textbox = findViewById(R.id.textSearch);
        //audio_mode = findViewById(R.id.audioModeButton);
        go = findViewById(R.id.GoButton);

        spinner = findViewById(R.id.apptype);
        loadSpinnerData(URL);

        //-------------------------------toolbar, location textbox & button-----------------------------------
        AppealListView = findViewById(R.id.AppealListView);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                new GetAppeals(SearchAppeal.this).execute();
            }
        });



        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                chosenType =   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

                Toast.makeText(getApplicationContext(),chosenType,Toast.LENGTH_LONG).show();

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });*/
    }

    /**
     * Background Async Task to get username
     * */
    private class GetAppeals extends AsyncTask<Void, Void, Void> {
        public Context context;
        String FinalJSonResult;
        List<Appeal> AppealList;

        public GetAppeals(Context context) {

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

            HttpServiceClass httpServiceClass = new HttpServiceClass(url_appeal_details);
            chosenType = spinner.getSelectedItem().toString();
            httpServiceClass.AddParam("appeal_type", chosenType);
            if(!(textbox.getText().toString().trim()).equals(""))
            {
                httpServiceClass.AddParam("text", textbox.getText().toString().trim());
            }

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {

                        try {

                            jsonArray = new JSONArray(FinalJSonResult);

                            AppealList = new ArrayList<Appeal>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                appeal = new Appeal();

                                jsonObject = jsonArray.getJSONObject(i);

                                appeal.Appeal_Type = jsonObject.getString("appeal_type");
                                appeal.Appeal_Desc = jsonObject.getString("appeal_desc");
                                appeal.User_email = jsonObject.getString("user_email");
                                appeal.Appeal_Image = jsonObject.getString("app_image");
                                email = appeal.User_email;
                                lat = Double.parseDouble(jsonObject.getString("user_address_lat"));
                                lon = Double.parseDouble(jsonObject.getString("user_address_long"));
                                address = getCompleteAddressString(lat,lon);

                           /* image = jsonObject.getInt("emer_image");
                            emergency.Emergency_Image = image;*/
                                System.out.println("All details saved in object."+appeal.Appeal_Desc+appeal.Appeal_Type+appeal.User_email);
                                AppealList.add(appeal);
                            }

                            AppealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try{
                                        Intent myIntent = new Intent(view.getContext(), AppealPost.class);
                                        /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), image);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();*/
                                        Appeal app = AppealList.get(position);
                                        type = app.Appeal_Type;
                                        desc = app.Appeal_Desc;
                                        email = app.User_email;
                                        image = app.Appeal_Image;
                                        myIntent.putExtra("Type",type);
                                        myIntent.putExtra("Content", desc);
                                        myIntent.putExtra("Email",email);
                                        myIntent.putExtra("Picture", image);
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

            AppealListView.setVisibility(View.VISIBLE);

            if (AppealList != null) {

                AppealListAdapter adapter = new AppealListAdapter(AppealList, context);

                AppealListView.setAdapter(adapter);

                AppealListView.setVisibility(View.VISIBLE);
            }
            else
                AppealListView.setVisibility(View.GONE);

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

        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());

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

                            appealTypes.add(type);

                        }

                    }

                    spinner.setAdapter(new ArrayAdapter<String>(SearchAppeal.this, android.R.layout.simple_spinner_dropdown_item, appealTypes));

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
                params.put("type", "Appeal");
                return params;
            }
        };

        int socketTimeout = 30000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        requestQueue.add(stringRequest);

    }
}
