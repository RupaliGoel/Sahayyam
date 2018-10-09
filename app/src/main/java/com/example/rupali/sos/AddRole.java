package com.example.rupali.sos;

import android.content.Context;
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

    TextView roles;
    private LinearLayout parentLinearLayout;
    int count = 1;

    String URL="https://sahayyam.000webhostapp.com/get_spinners.php";
    ArrayList<String> roleTypes;
    Spinner spinner;

    String selected_option1,selected_option2,selected_option3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_role);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);

        roles = findViewById(R.id.addrolestv);
        spinner =  findViewById(R.id.type_spinner);
        roleTypes=new ArrayList<>();

        loadSpinnerData(URL);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // TODO Auto-generated method stub

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });
    }

    public void onAddField(View v) {

        if(count < 3){

            ++count;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.roles, null);
            spinner = rowView.findViewById(R.id.type_spinner);
            loadSpinnerData(URL);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override

                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    // TODO Auto-generated method stub

                }

                @Override

                public void onNothingSelected(AdapterView<?> adapterView) {

                    // DO Nothing here

                }

            });

            // Add the new row before the add field button.
            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
        }

        else{
            Toast.makeText(AddRole.this,"YOU CAN ADD MAXIMUM 3 ROLES.",Toast.LENGTH_LONG).show();
        }


    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
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

                        spinner.setAdapter(new ArrayAdapter<String>(AddRole.this, android.R.layout.simple_spinner_dropdown_item, roleTypes));

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
}
