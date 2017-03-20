package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Objects.UserEntity;


public class SignupActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Get Firebase auth instance
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final Button buttonSignIn = (Button) findViewById(R.id.sign_in_button);
        final Button buttonSignUp = (Button) findViewById(R.id.sign_up_button);
        final Button buttonResetPassword = (Button) findViewById(R.id.btn_reset_password);
        final EditText inputEmail = (EditText) findViewById(R.id.email);
        final EditText inputPassword = (EditText) findViewById(R.id.password);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this ,"Enter email address!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    createUserDb();
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

                // Perform action on click
            }
        });}


        public void createUserDb(){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            UserEntity user = new UserEntity(FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getUid());
            DatabaseReference usersRef = mDatabase.child("users");
            String encoded = EncodeString(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            usersRef.child(encoded).setValue(user);
    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }
    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }


}