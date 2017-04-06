package com.alexsantos.gameappfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUserName;
    private EditText mUserEmail;
    private EditText mUserPassword;
    private Button mButtonRegister;
    private TextView linkToLogin;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mUserName = (EditText) findViewById(R.id.userNameText);
        mUserEmail = (EditText) findViewById(R.id.userEmailText);
        mUserPassword = (EditText) findViewById(R.id.userPasswordText);
        mButtonRegister = (Button) findViewById(R.id.registerButton);
        linkToLogin = (TextView) findViewById(R.id.link_login);

        progressDialog = new ProgressDialog(this);


        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegister();
            }
        });

        linkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginLinkIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                loginLinkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginLinkIntent);
            }
        });

    }

    public void startRegister(){

        final String name = mUserName.getText().toString().trim();
        String email = mUserEmail.getText().toString().trim();
        String password = mUserPassword.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            progressDialog.setMessage("Signing Up...");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUser = mDatabase.child(user_id);
                        currentUser.child("name").setValue(name);

                        progressDialog.dismiss();

                        Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);

                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }else{
                        Toast.makeText(RegisterActivity.this,"Account Failed to login",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(RegisterActivity.this,"Fields can't be blank",Toast.LENGTH_SHORT).show();
        }

    }

}
