package com.example.rupali.sos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,name,role,contact;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    FirebaseUser user;
    private String userId;
//
//    private DatabaseReference mFirebaseDatabase;
//    private FirebaseDatabase mFirebaseInstance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


      // user=auth.getCurrentUser();
       database = FirebaseDatabase.getInstance();
       databaseReference = database.getReference();



//
//       Toast.makeText(SignupActivity.this, "" + "DONE\n" + databaseReference + "\n " + user.getUid(), Toast.LENGTH_LONG).show();
//


        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        name=(EditText)findViewById(R.id.etName);
        role=(EditText)findViewById(R.id.etRole);
        contact=(EditText)findViewById(R.id.etContact);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
//            }
//        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String name1=name.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check for already existed userId
                //if (TextUtils.isEmpty(userId)) {
                // createUser(name1, email);
                //}


                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                   user=auth.getCurrentUser();
                                    Toast.makeText(getBaseContext(),user.getUid(),Toast.LENGTH_SHORT).show();

                                    database = FirebaseDatabase.getInstance();
                                    databaseReference = database.getReference();
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @SuppressLint("ResourceType")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try{
                                                // Toast.makeText(getBaseContext(),"SEARCH",Toast.LENGTH_SHORT).show();
                                                databaseReference.child("Users").child(user.getUid()).child("UserDetails").child("Name").setValue(name.getText().toString().trim());
                                                databaseReference.child("Users").child(user.getUid()).child("UserDetails").child("Role").setValue(role.getText().toString().trim());
                                                databaseReference.child("Users").child(user.getUid()).child("UserDetails").child("Contact").setValue(Long.parseLong(contact.getText().toString().trim()));
                                                databaseReference.child("Users").child(user.getUid()).child("UserDetails").child("Email-ID").setValue(inputEmail.getText().toString().trim());
                                                //Toast.makeText(getBaseContext(),"SEARCH",Toast.LENGTH_SHORT).show();

                                    Intent intent=new Intent(SignupActivity.this, MainActivity.class);
                                    // String[] myString= new String[]{name.getText().toString(),role.getText().toString()};
                                    intent.putExtra("name",name.getText().toString());
                                    intent.putExtra("role",role.getText().toString());
                                    intent.putExtra("contact",contact.getText().toString().trim());
                                    intent.putExtra("email",inputEmail.getText().toString().trim());
                                    startActivity(intent);
                                    finish();


                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            System.out.println("The Read Failed."+databaseError.getCode());
                                        }
                                    });



//                                    Intent intent=new Intent(SignupActivity.this, MainActivity.class);
//                                    // String[] myString= new String[]{name.getText().toString(),role.getText().toString()};
//                                    intent.putExtra("name",name.getText().toString());
//                                    intent.putExtra("role",role.getText().toString());
//                                    intent.putExtra("contact",contact.getText().toString().trim());
//                                    startActivity(intent);
//                                    finish();
                                }
                            }
                        });

//                user=auth.getCurrentUser();
//                database = FirebaseDatabase.getInstance();
//                databaseReference = database.getReference();
//
//                Toast.makeText(SignupActivity.this, "" + "DONE\n" + databaseReference + "\n " + user.getUid(), Toast.LENGTH_LONG).show();


//                database = FirebaseDatabase.getInstance();
//                databaseReference = database.getReference();
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        try{
//                            // Toast.makeText(getBaseContext(),"SEARCH",Toast.LENGTH_SHORT).show();
//                            databaseReference.child("Users").child(user.getUid()).child("UserDetails").child("Name").setValue(getIntent().getExtras().getString("name"));
//                            databaseReference.child("Users").child(user.getUid()).child("UserDetails").child("Role").setValue(getIntent().getExtras().getString("role"));
//                            databaseReference.child("Users").child(user.getUid()).child("UserDetails").child("Contact").setValue(Long.parseLong(getIntent().getExtras().getString("contact")));
//                            Toast.makeText(getBaseContext(),"SEARCH",Toast.LENGTH_SHORT).show();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        System.out.println("The Read Failed."+databaseError.getCode());
//                    }
//                });

            }
        });
    }


//    /**
//     * Creating new user node under 'users'
//     */
//    private void createUser(String name, String email) {
//
//        User user1 = new User(name, email);
//
//        databaseReference.child("Users").child(user.getUid()).child("UserDetails").setValue(user1);
//        finish();
//
//        //addUserChangeListener();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
