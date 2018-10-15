package com.example.rupali.sos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

public class ResetPasswordActivity extends AppCompatActivity {


    private EditText inputEmail;
    private Button btnReset, btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

               progressBar.setVisibility(View.VISIBLE);

//                BackgroundMail.newBuilder(ResetPasswordActivity.this)
//                        .withUsername("mridusharma4567@gmail.com") // compsny ki id se jaega mail
//                        .withPassword("8901054440")
//                        .withMailto(""+email)   //customer ko jaega
//                        .withType(BackgroundMail.TYPE_PLAIN)
//                        .withSubject("FEEDBACK")
//                        .withBody("Thank you for your feedback! \n Feedback given: ")
//                        .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
//                            @Override
//                            public void onSuccess() {
//                                Toast.makeText(ResetPasswordActivity.this,"done",Toast.LENGTH_LONG).show();
////                                Intent i=new Intent(Feedback_ms.this,CustomerActivity_cm.class);
////                                startActivity(i);
//                            }
//                        })
//                        .withOnFailCallback(new BackgroundMail.OnFailCallback() {
//                            @Override
//                            public void onFail() {
//                                Toast.makeText(ResetPasswordActivity.this,"Not done",Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        .send();



            }
        });

























    }
}
