package com.example.rupali.sos;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import static android.app.Activity.RESULT_OK;
import static android.media.MediaRecorder.VideoSource.CAMERA;

public class InputEmergency extends Fragment {
    //Bitmap bitmap=null;
    Bitmap bm=null;
    //Bitmap thumbnail=null;
    int last_emer_id = 0;
    boolean check = true;
    byte[] byteArrayVar;
    ProgressDialog progressDialog;
    EditText role, name, address, contact, placeedit, desc, hiddenType,imagename;
    Spinner spinner;
    Button submit, choose;
    ImageView ivImage;
    ImageButton placepickerbtn;
    String userChoosenTask, currentAddressOfUser;
    String GetImageNameEditText;
    private android.support.v7.widget.Toolbar page_name;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    double currentlat,currentlong;
    String addressOfUser,nameOfUser,roleOfUser,contactOfUser,emailOfUser;
    String placelattitude,placelongitude;
    String title_emergency ;
    int success;
    ByteArrayOutputStream byteArrayOutputStreamObject ;
    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_write_emergency = "https://sahayyam.000webhostapp.com/write_emergency.php";
    private static String url_write_gen_master = "https://sahayyam.000webhostapp.com/write_gen_master.php";
    String ImageName = "emer_imagename" ;
    String ImagePath = "emer_image" ;
    String ServerUploadPath ="https://sahayyam.000webhostapp.com/image_upload.php" ;
    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    ArrayList<String> emergencyTypes;
    View progressOverlay;
    int PLACE_PICKER_REQUEST = 3;

    public InputEmergency() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_input_emergency, container, false);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show progress overlay (with animation):
        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        emergencyTypes=new ArrayList<>();
        emailOfUser = prefs.getString("user_email","");
        addressOfUser = prefs.getString("user_address","");
        nameOfUser = prefs.getString("user_name", "Guest");
        roleOfUser = prefs.getString("roles","");
        contactOfUser = prefs.getString("user_contact","");
        currentAddressOfUser = prefs.getString("user_current_address", "Not Found");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        page_name = (Toolbar) getActivity().findViewById(R.id.page_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(page_name);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        boolean Islogin = prefs.getBoolean("Islogin", false);

        if (!Islogin) {   // condition true means user is not logged in
            Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(i, 1);
        }
        ivImage = view.findViewById(R.id.uploadedphoto);
        role = view.findViewById(R.id.role);
        name = view.findViewById(R.id.name);
        imagename = view.findViewById(R.id.imagename);
        address = view.findViewById(R.id.address);
        spinner = view.findViewById(R.id.emergency_Type);
        contact = view.findViewById(R.id.contact);
        placeedit = view.findViewById(R.id.place);
        desc = view.findViewById(R.id.description);
        hiddenType = view.findViewById(R.id.title);
        placepickerbtn = view.findViewById(R.id.placepickerbtn);

        address.setText(addressOfUser);
        address.setEnabled(false);
        role.setText(roleOfUser);
        role.setEnabled(false);
        contact.setText(contactOfUser);
        contact.setEnabled(false);
        name.setText(nameOfUser);
        name.setEnabled(false);
        placeedit.setText(currentAddressOfUser);
        //spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        loadSpinnerData(URL);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                // TODO Auto-generated method stub
                String selected_option = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();


                if(selected_option.matches("Other"))
                {
                    hiddenType.setVisibility(View.VISIBLE);
                }
                else {
                    hiddenType.setVisibility(View.INVISIBLE);
                }

                title_emergency = selected_option;

                Toast.makeText(getActivity().getApplicationContext(),selected_option,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }
        });
        choose = view.findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });

        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertAddress();
                GetImageNameEditText = imagename.getText().toString();
                //ImageUploadToServerFunction();
                getCoordinatesFromAddress(placeedit.getText().toString().trim());
                System.out.print("\nLAT:"+placelattitude+"\nLONG:"+placelongitude);
                if ((placeedit.getText().toString()).equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Place of Emergency !", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if ((desc.getText().toString()).equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Emergency Description!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else
                    new writeEmergency().execute();

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


    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    static final int REQUEST_CAMERA = 2;
    public void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    static final int SELECT_FILE = 1;

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
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
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, getActivity().getApplicationContext());
                String toastMsg = String.format("%s", place.getAddress());
                placeedit.setText(toastMsg);
                //Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void ImageUploadToServerFunction(){
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(getActivity(),"Image is Uploading","Please Wait",false,false);
            }
            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                // Printing uploading success message coming from server on android app.
                Toast.makeText(getActivity(),string1,Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(),"Done",Toast.LENGTH_LONG).show();
                // Setting image as transparent after done uploading.
               // ivImage.setImageResource(android.R.color.transparent);
            }
            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, GetImageNameEditText);

                HashMapParams.put(ImagePath, ConvertImage);

                HashMapParams.put("emer_id",String.valueOf(last_emer_id));

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(1900000);

                httpURLConnectionObject.setConnectTimeout(1900000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null){
                        stringBuilder.append(RC2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }
        public String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");
                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilderObject.toString();
        }
    }



    @SuppressWarnings("deprecation")
    public void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        byteArrayVar = byteArrayOutputStreamObject.toByteArray();
    }
    public void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(byteArrayOutputStreamObject.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(bm);
        byteArrayVar = byteArrayOutputStreamObject.toByteArray();

    }

    public void getCoordinatesFromAddress(String locationAddress) {
        Geocoder coder = new Geocoder(getActivity());
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(locationAddress, 50);
            for(Address add : adresses){
                //if (statement) {//Controls to ensure it is right address such as country etc.
                double longitude = add.getLongitude();
                placelongitude = String.valueOf(longitude);
                double latitude = add.getLatitude();
                placelattitude = String.valueOf(latitude);
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async Task to Create new user
     * */
    class writeEmergency extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating user
         */
        protected String doInBackground(String... args) {
            try {
                // Building Parameters

                if(title_emergency.equals("Other"))
                {
                    title_emergency = hiddenType.getText().toString();
                    new writeGen_Master().execute();
                }

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", emailOfUser));
                params.add(new BasicNameValuePair("title", title_emergency));
                params.add(new BasicNameValuePair("desc", desc.getText().toString().trim()));
                params.add(new BasicNameValuePair("addlat", String.valueOf(currentlat)));
                params.add(new BasicNameValuePair("addlong", String.valueOf(currentlong)));
                params.add(new BasicNameValuePair("placelat", placelattitude));
                params.add(new BasicNameValuePair("placelong", placelongitude));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_write_emergency, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);
                    last_emer_id = json.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            catch(Exception e)
            {
                System.out.print(e);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            if(success==1) {
                ImageUploadToServerFunction();
                Toast.makeText(getActivity().getApplicationContext(),"Saved Successfully.",Toast.LENGTH_LONG).show();
                hiddenType.setVisibility(View.INVISIBLE);
                placeedit = getView().findViewById(R.id.place);
                placeedit.setText(currentAddressOfUser);
                desc = getView().findViewById(R.id.description);
                desc.setText("");
                ivImage = getView().findViewById(R.id.uploadedphoto);
                ivImage.setImageDrawable(null);
            }
            else
                Toast.makeText(getActivity().getApplicationContext(),"Failed.",Toast.LENGTH_LONG).show();
        }
    }

    public void convertAddress() {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        if (!currentAddressOfUser.equals("")) {
            try {
                List<Address> addressList = geocoder.getFromLocationName(currentAddressOfUser, 1);
                if (addressList != null && addressList.size() > 0) {
                    currentlat = addressList.get(0).getLatitude();
                    currentlong = addressList.get(0).getLongitude();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    } // end convertAddress

    private void loadSpinnerData(String url) {

        RequestQueue requestQueue=Volley.newRequestQueue(getActivity().getApplicationContext());

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

                            emergencyTypes.add(type);

                        }

                    }
                    if(getActivity()!=null)
                        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, emergencyTypes));

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
                params.put("type", "Emergency");
                return params;
            }
        };

        int socketTimeout = 30000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        requestQueue.add(stringRequest);

    }

    /**
     * Background Async Task to Create new user
     * */
    class writeGen_Master extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating gm_code
         */
        protected String doInBackground(String... args) {


            try {
                // Building Parameters

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("code", "Emergency"));
                params.add(new BasicNameValuePair("desc", title_emergency));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_write_gen_master, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            catch(Exception e)
            {
                System.out.print(e);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {

        }
    }
}





/*public class InputEmergency extends Fragment {
    EditText role, name, address, contact, place, desc, hiddenType;
    Spinner spinner;
    Button submit, choose;
    ImageView ivImage;
    String userChoosenTask, currentAddressOfUser;
    private android.support.v7.widget.Toolbar page_name;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currentlattitude,currentlongitude,addressOfUser,nameOfUser,roleOfUser,contactOfUser,emailOfUser;
    String placelattitude,placelongitude;
    String title_emergency ;

    int success;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_write_emergency = "https://sahayyam.000webhostapp.com/write_emergency.php";
    private static String url_write_gen_master = "https://sahayyam.000webhostapp.com/write_gen_master.php";

    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    ArrayList<String> emergencyTypes;

    View progressOverlay;

    public InputEmergency() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_input_emergency, container, false);
        choose = view.findViewById(R.id.choose);
        progressOverlay = view.findViewById(R.id.progress_overlay);
        progressOverlay.bringToFront();
        choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show progress overlay (with animation):
        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        emergencyTypes=new ArrayList<>();

        currentlattitude = prefs.getString("lat","");
        currentlongitude = prefs.getString("long","");
        emailOfUser = prefs.getString("user_email","");
        addressOfUser = prefs.getString("user_address","");
        nameOfUser = prefs.getString("user_name", "Guest");
        roleOfUser = prefs.getString("roles","");
        contactOfUser = prefs.getString("user_contact","");
        currentAddressOfUser = prefs.getString("user_current_address", "Not Found");


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        page_name = (Toolbar) getActivity().findViewById(R.id.page_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(page_name);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        boolean Islogin = prefs.getBoolean("Islogin", false);

        if (!Islogin) {   // condition true means user is not logged in
            Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(i, 1);
        }
        ivImage = view.findViewById(R.id.uploadedphoto);
        role = view.findViewById(R.id.role);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        spinner = view.findViewById(R.id.emergency_Type);
        contact = view.findViewById(R.id.contact);
        place = view.findViewById(R.id.place);
        desc = view.findViewById(R.id.description);
        hiddenType = view.findViewById(R.id.title);

        address.setText(addressOfUser);
        address.setEnabled(false);
        role.setText(roleOfUser);
        role.setEnabled(false);
        contact.setText(contactOfUser);
        contact.setEnabled(false);
        name.setText(nameOfUser);
        name.setEnabled(false);
        place.setText(currentAddressOfUser);

        submit = view.findViewById(R.id.submit);

        //spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        loadSpinnerData(URL);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                // TODO Auto-generated method stub
                String selected_option = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();


                if(selected_option.matches("Other"))
                {
                    hiddenType.setVisibility(View.VISIBLE);
                }
                else {
                    hiddenType.setVisibility(View.INVISIBLE);
                }

                title_emergency = selected_option;

                Toast.makeText(getActivity().getApplicationContext(),selected_option,Toast.LENGTH_LONG).show();

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinatesFromAddress(place.getText().toString().trim());
                System.out.print("\nLAT:"+placelattitude+"\nLONG:"+placelongitude);

                if ((place.getText().toString()).equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Place of Emergency !", Toast.LENGTH_SHORT).show();
                    return;
                }

               else if ((desc.getText().toString()).equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Emergency Description!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else
                    new writeEmergency().execute();

            }
        });

    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    static final int REQUEST_CAMERA = 2;
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    static final int SELECT_FILE = 1;

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(thumbnail);
    }

    public void getCoordinatesFromAddress(String locationAddress) {
        Geocoder coder = new Geocoder(getActivity());
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(locationAddress, 50);
            for(Address add : adresses){
                //if (statement) {//Controls to ensure it is right address such as country etc.
                    double longitude = add.getLongitude();
                    placelongitude = String.valueOf(longitude);
                    double latitude = add.getLatitude();
                    placelattitude = String.valueOf(latitude);
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async Task to Create new user
     * */
/*    class writeEmergency extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        //@Override
  /*      protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating user
         */
/*        protected String doInBackground(String... args) {


            try {
                // Building Parameters

                if(title_emergency.equals("Other"))
                {
                    title_emergency = hiddenType.getText().toString();
                    new writeGen_Master().execute();
                }

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", emailOfUser));
                params.add(new BasicNameValuePair("title", title_emergency));
                params.add(new BasicNameValuePair("desc", desc.getText().toString().trim()));
                params.add(new BasicNameValuePair("addlat", currentlattitude));
                params.add(new BasicNameValuePair("addlong", currentlongitude));
                params.add(new BasicNameValuePair("placelat", placelattitude));
                params.add(new BasicNameValuePair("placelong", placelongitude));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_write_emergency, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            catch(Exception e)
            {
                System.out.print(e);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
  /*      protected void onPostExecute(String file_url) {
            if(success==1) {
                Toast.makeText(getActivity().getApplicationContext(),"Saved Successfully.",Toast.LENGTH_LONG).show();
                hiddenType.setVisibility(View.INVISIBLE);
                place = getView().findViewById(R.id.place);
                place.setText(currentAddressOfUser);
                desc = getView().findViewById(R.id.description);
                desc.setText("");
                ivImage = getView().findViewById(R.id.uploadedphoto);
                ivImage.setImageDrawable(null);
            }
            else
                Toast.makeText(getActivity().getApplicationContext(),"Failed.",Toast.LENGTH_LONG).show();
        }
    }

    private void loadSpinnerData(String url) {

        RequestQueue requestQueue=Volley.newRequestQueue(getActivity().getApplicationContext());

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

                            emergencyTypes.add(type);

                        }

                    }

                    if(getActivity()!=null)
                        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, emergencyTypes));

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
                params.put("type", "Emergency");
                return params;
            }
        };

        int socketTimeout = 30000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        requestQueue.add(stringRequest);

    }

    /**
     * Background Async Task to Create new user
     * */
   /* class writeGen_Master extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
    //    @Override
     /*   protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating gm_code
         */
       /* protected String doInBackground(String... args) {


            try {
                // Building Parameters

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("code", "Emergency"));
                params.add(new BasicNameValuePair("desc", title_emergency));

                // getting JSON Object
                // Note that create user url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_write_gen_master, "POST", params);
                // check log cat fro response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            catch(Exception e)
            {
                System.out.print(e);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
       /* protected void onPostExecute(String file_url) {

        }
    }
}*/