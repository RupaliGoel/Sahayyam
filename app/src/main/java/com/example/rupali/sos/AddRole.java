package com.example.rupali.sos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.Map;

public class AddRole extends AppCompatActivity {

    int count = 0, count_selected_options = 0;

    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";
    ArrayList<String> roleTypes;
    Spinner spinner1, spinner2, spinner3;
    Button enable1, enable2, enable3, disable1, disable2, disable3, submit ;
    Bundle bundle;
    ProgressDialog dialog;
    String callingActivity,role1,role2,role3;
    String[] selected_options = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_role);

        dialog = new ProgressDialog(AddRole.this);
        dialog.setMessage("Loading...");
        dialog.show();

        roleTypes=new ArrayList<>();
        roleTypes.add("-- Select --");

        spinner1 = findViewById(R.id.type_spinner1);
        spinner1.setEnabled(false);
        enable1 = findViewById(R.id.enable_button1);
        disable1 = findViewById(R.id.disable_button1);

        spinner2 = findViewById(R.id.type_spinner2);
        spinner2.setEnabled(false);
        enable2 = findViewById(R.id.enable_button2);
        disable2 = findViewById(R.id.disable_button2);

        spinner3 = findViewById(R.id.type_spinner3);
        spinner3.setEnabled(false);
        enable3 = findViewById(R.id.enable_button3);
        disable3 = findViewById(R.id.disable_button3);

        loadSpinnerData(URL);

        bundle = getIntent().getExtras();
        callingActivity = bundle.getString("CallingActivity");

        if(callingActivity.equals("EditProfile")) {

            role1 = bundle.getString("Role1");
            System.out.println("ROLE 1 = "+role1);
            role2 = bundle.getString("Role2");
            System.out.println("ROLE 2 = "+role2);
            role3 = bundle.getString("Role3");
            System.out.println("ROLE 3 = "+role3);

        }

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(AddRole.this,"Working",Toast.LENGTH_LONG).show();

                String Selected = spinner1.getSelectedItem().toString().trim();
                selected_options[0] = Selected ;
                System.out.println(selected_options[0]);
                Toast.makeText(AddRole.this,Selected,Toast.LENGTH_LONG).show();
                count_selected_options++;
                ArrayList<String> roles = new ArrayList<>();
                roles.addAll(roleTypes);
                roles.remove(selected_options[0]);
                spinner2.setAdapter(new ArrayAdapter<String>(AddRole.this, android.R.layout.simple_spinner_dropdown_item, roles));
                if(callingActivity.equals("EditProfile")) {
                    spinner1.setSelection(getIndex(spinner1,role1));
                    spinner1.setEnabled(true);
                    if(!role2.equals("")){
                        spinner2.setSelection(getIndex(spinner2,role2));
                        spinner2.setEnabled(true);
                    }
                }
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = spinner2.getSelectedItem().toString().trim();
                selected_options[1] = Selected ;
                System.out.println(selected_options[1]);
                count_selected_options++;
                ArrayList<String> roles = new ArrayList<>();
                roles.addAll(roleTypes);
                roles.remove(selected_options[0]);
                roles.remove(selected_options[1]);
                spinner3.setAdapter(new ArrayAdapter<String>(AddRole.this, android.R.layout.simple_spinner_dropdown_item, roles));
                if(callingActivity.equals("EditProfile")) {
                    if(!role3.equals("")){
                        spinner3.setSelection(getIndex(spinner3,role3));
                        spinner3.setEnabled(true);
                    }
                }
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Selected = spinner3.getSelectedItem().toString().trim();
                selected_options[2] = Selected ;
                System.out.println(selected_options[2]);
                count_selected_options++;
            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });

        enable1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner1.setClickable(true);
                spinner1.setEnabled(true);
            }
        });
        disable1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner1.setClickable(false);
                spinner1.setEnabled(false);
            }
        });

        enable2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner1.isEnabled()&&!(spinner1.getSelectedItem().toString()).equals("-- Select --")){
                    spinner2.setClickable(true);
                    spinner2.setEnabled(true);
                }
            }
        });
        disable2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinner3.isEnabled()){
                    spinner2.setClickable(false);
                    spinner2.setEnabled(false);
                }
            }
        });


        enable3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner1.isEnabled() && !(spinner1.getSelectedItem().toString()).equals("-- Select --")
                        && spinner2.isEnabled() && !(spinner2.getSelectedItem().toString()).equals("-- Select --")){
                    spinner3.setClickable(true);
                    spinner3.setEnabled(true);
                }
            }
        });
        disable3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner3.setClickable(false);
                spinner3.setEnabled(false);
            }
        });


        submit = findViewById(R.id.submitRoles);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_options[0] = spinner1.getSelectedItem().toString().trim();
                selected_options[1] = spinner2.getSelectedItem().toString().trim();
                selected_options[2] = spinner3.getSelectedItem().toString().trim();

                if(selected_options[0].equals("-- Select --")){
                    Toast.makeText(AddRole.this,"Please Add at least one Role .",Toast.LENGTH_LONG).show();
                }
                else{

                    System.out.println("SELECTED ROLES = "+selected_options[0]+","+selected_options[1]+","+selected_options[2]);

                    Intent intent = new Intent();
                    intent.putExtra("SelectedRole1", selected_options[0]);
                    intent.putExtra("SelectedRole2", selected_options[1]);
                    intent.putExtra("SelectedRole3", selected_options[2]);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    private void loadSpinnerData(String url) {

        RequestQueue requestQueue=Volley.newRequestQueue(AddRole.this);

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

                    spinner1.setAdapter(new ArrayAdapter<String>(AddRole.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));
                    spinner2.setAdapter(new ArrayAdapter<String>(AddRole.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));
                    spinner3.setAdapter(new ArrayAdapter<String>(AddRole.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));

                    dialog.dismiss();

                }catch (JSONException e){e.printStackTrace();}

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

    //private method of your class
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

}
