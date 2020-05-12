package com.example.shoppinglistapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login_btn;
    private TextView signup_txt;
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.pass_login);
        login_btn = findViewById(R.id.btn_login);
        signup_txt = findViewById(R.id.txt_signup);
        pDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignupActivity.class));
            }
        });

//        if(mAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
//        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Required..");
                    return;
                }
                if (TextUtils.isEmpty(mPass)) {
                    password.setError("Required..");
                    return;
                }

                pDialog.setMessage("Processing");
                pDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
