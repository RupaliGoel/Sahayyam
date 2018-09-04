package com.example.rupali.sos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;

    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view now
        setContentView(R.layout.activity_login);

        initViews();
        initObjects();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

//        btnReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
//            }
//        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else{
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

                progressBar.setVisibility(View.VISIBLE);
                verifyFromSQLite();
            }
        });
    }

    /**
     * This method is to initialize views
     */
    private void initViews(){
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(LoginActivity.this);
    }

    @Override
    public void onBackPressed() {
       int count=getFragmentManager().getBackStackEntryCount();

       if(count==0)
           super.onBackPressed();
       else
        getFragmentManager().popBackStack();
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {

        if (databaseHelper.checkUser(inputEmail.getText().toString().trim()
                , inputPassword.getText().toString().trim())) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Islogin", true).commit();
            editor.putString("user_email",inputEmail.getText().toString().trim()).commit();
            editor.putString("user_name",databaseHelper.getUsername(inputEmail.getText().toString().trim())).commit();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            setResult(Activity.RESULT_OK, intent);
            //startActivity(intent);
            finish();

        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        inputEmail.setText(null);
        inputPassword.setText(null);
    }
}