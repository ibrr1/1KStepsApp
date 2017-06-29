package com.zoonapps.stepscountapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.zoonapps.stepscountapp.R;

public class RegistrationActivity extends AppCompatActivity {

    EditText mUsernameET, mPasswordET, mEmailET;
    Button mRegisterBtn;
    ProgressBar mProgressBar;
    TextView mLoginTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // to stop keyboard from popup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mUsernameET = (EditText) findViewById(R.id.usernameET);
        mEmailET = (EditText) findViewById(R.id.emailET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLoginTV = (TextView) findViewById(R.id.loginTV);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Declare variables	and get the text from the EditText
                String username = mUsernameET.getText().toString();
                String password = mPasswordET.getText().toString();
                String email = mEmailET.getText().toString();

                //email validation
                String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-zA-Z0-9._-]+";

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "اسم المستخدم او كلمة المرور او الايميل لايمكن ان تكون فارغة!", Toast.LENGTH_SHORT).show();
                }
                // username must be more than 4 characters
                else if (username.trim().length() < 4) {
                    Toast.makeText(getApplicationContext(), "اسم المستخدم لايمكن ان يكون اقل من 4 حروف!", Toast.LENGTH_SHORT).show();
                }
                //if the email is not valid
                else if (!email.matches(emailPattern))
                {
                    Toast.makeText(getApplicationContext(),"الرجاء ادخال ايميل صحيح!", Toast.LENGTH_SHORT).show();
                }


                else {
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);

                    mProgressBar.setVisibility(View.VISIBLE);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                //Register Successful
                                Toast.makeText(getApplicationContext(), "تم التسجيل بنجاح!", Toast.LENGTH_SHORT).show();


                                ParseUser currentUser;
                                ParseObject po = new ParseObject("UserCurrentStatus");

                                // Retrieve current user from Parse.com
                                currentUser = ParseUser.getCurrentUser();

                                po.put("username", currentUser.getUsername());
                                po.put("userId", currentUser.getObjectId());
                                po.put("userCurrentSteps", 0);
                                po.put("userCurrentEarning", String.format( "%.3f", 0.0 ));
                                po.saveInBackground();

                                // Direct the user to SetpsCountActivity.java
                                Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                //Register Fail
                                //get error by calling e.getMessage()
                                Toast.makeText(getApplicationContext(), "حدث خطا!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

        mLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
