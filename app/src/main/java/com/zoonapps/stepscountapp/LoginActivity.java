package com.zoonapps.stepscountapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    EditText mUsernameET;
    EditText mPasswordET;
    Button mLoginBtn, mRegisterBtn;
    TextView mRegisterTV, mContinueTV;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // to stop keyboard from popup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        // Check if the user is logged in
        ParseAnalytics.trackAppOpened(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();

        // if the the user not logged out
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        mUsernameET = (EditText) findViewById(R.id.usernameET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mContinueTV = (TextView) findViewById(R.id.continueTV);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Declare variables	and get the text from the EditText
                String username = mUsernameET.getText().toString();
                String password = mPasswordET.getText().toString();

                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
                }
                //Check if EditTexts are not empty
                else if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "username or password cant't be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                //Login Successful
                                //you can display sth or do sth
                                //For example Welcome + ParseUser.getUsername()
                                Toast.makeText(getApplicationContext(), "Login successfully!", Toast.LENGTH_SHORT).show();
                                // Direct the user to MainActivity.java
                                Intent intent = new Intent();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);

                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                //Login Fail
                                //get error by calling e.getMessage()
                                Toast.makeText(getApplicationContext(), "خظا في تسجيل الدخول!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }

        });

        // to Register
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        mContinueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }


    // isNetworkAvailable method to check if the internet is Available or not
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
