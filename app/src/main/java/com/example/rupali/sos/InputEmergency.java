package com.example.rupali.sos;
import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.iconics.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class InputEmergency extends Fragment {
    EditText role, name, address, contact, place, title, desc;
    Button submit, choose;
    ImageView ivImage;
    String userChoosenTask;
    private android.support.v7.widget.Toolbar page_name;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currentlattitude,currentlongitude,addressOfUser,nameOfUser,roleOfUser,contactOfUser,emailOfUser;
    String placelattitude,placelongitude;

    int success;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_write_emergency = "https://sahayyam.000webhostapp.com/write_emergency.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";


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

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        currentlattitude = prefs.getString("lat","None");
        currentlongitude = prefs.getString("long","None");
        emailOfUser = prefs.getString("user_email","Not Found");
        addressOfUser = prefs.getString("user_address","Not Found");
        nameOfUser = prefs.getString("user_name", "Guest");
        roleOfUser = prefs.getString("user_role","Not Found");
        contactOfUser = prefs.getString("user_contact","Not Found");


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        page_name = (Toolbar) getActivity().findViewById(R.id.page_name);
        ((AppCompatActivity) getActivity()).setSupportActionBar(page_name);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        boolean Islogin = prefs.getBoolean("Islogin", false);

        if (!Islogin) {   // condition true means user is not logged in
            Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivityForResult(i, 1);
        }

        ivImage = view.findViewById(R.id.uploadedphoto);
        role = view.findViewById(R.id.role);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        title = view.findViewById(R.id.title);
        contact = view.findViewById(R.id.contact);
        place = view.findViewById(R.id.place);
        desc = view.findViewById(R.id.description);

        address.setText(addressOfUser);
        address.setEnabled(false);
        role.setText(roleOfUser);
        role.setEnabled(false);
        contact.setText(contactOfUser);
        contact.setEnabled(false);
        name.setText(nameOfUser);
        name.setEnabled(false);

        submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinatesFromAddress(place.getText().toString().trim());
                System.out.print("\nLAT:"+placelattitude+"\nLONG:"+placelongitude);

                if ((place.getText().toString()).equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Place of Emergency !", Toast.LENGTH_SHORT).show();
                    return;
                }

               else if ((title.getText().toString()).equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter Emergency Title!", Toast.LENGTH_SHORT).show();
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

    static final int REQUEST_CAMERA = 1;
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", emailOfUser));
                params.add(new BasicNameValuePair("title", title.getText().toString().trim()));
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
        protected void onPostExecute(String file_url) {
            if(success==1) {
                Toast.makeText(getActivity().getApplicationContext(),"Saved Successfully.",Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getActivity().getApplicationContext(),"Failed.",Toast.LENGTH_LONG).show();
        }
    }

}