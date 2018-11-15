package com.example.rupali.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyProfile extends AppCompatActivity {

    String email,name,role1,role2,role3,contact,address,roles;
    ImageButton editbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        name = prefs.getString("user_name","guest");
        roles = prefs.getString("roles", "guest");
        /*role1 = prefs.getString("user_role1","");
        role2 = prefs.getString("user_role2","");
        role3 = prefs.getString("user_role3","");*/

        contact = prefs.getString("user_contact","**********");
        email = prefs.getString("user_email", "guest@guest.com");
        address = prefs.getString("user_address", "***");
        final boolean isLogin = prefs.getBoolean("Islogin",false);

        TextView textName = findViewById(R.id.name);
        TextView textRole = findViewById(R.id.role);
        TextView textEmail = findViewById(R.id.email);
        TextView textContact =findViewById(R.id.mobileNumber);
        TextView textAddress = findViewById(R.id.address);

        editbtn = findViewById(R.id.edit);
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin == true){
                    Intent intent = new Intent(MyProfile.this, EditProfile.class);
                    startActivity(intent);
                }
            }
        });

        textName.setText(name);
        textContact.setText(contact);
        textEmail.setText(email);

        /*if(!role1.equals("") && !role2.equals("") && !role3.equals("")){
            textRole.setText(role1+", "+role2+", "+role3);
        }
        else if(!role2.equals("") && role3.equals("")){
            textRole.setText(role1+", "+role2);
        }
        else if(role2.equals("") && role3.equals("")){
            textRole.setText(role1);
        }*/

        textRole.setText(roles);

        textAddress.setText(address);
    }
}

