package com.zoonapps.stepscountapp.Fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zoonapps.stepscountapp.R;
import com.zoonapps.stepscountapp.StepDetector;
import com.zoonapps.stepscountapp.StepListener;

import static android.content.Context.SENSOR_SERVICE;


public class MainFragment extends Fragment implements SensorEventListener, StepListener {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "";
//    private static double STEP_PRICE = 0;

    private int numSteps;
    private double stepPrice;
    private double mUpdatedStepPrice;
    TextView TvSteps, TvMoney, mBtnLabel;
    at.markushi.ui.CircleButton mBtnStart;
    Button mNoInternetBtn;

    boolean isPressed = true;


    int userCurrentSteps;
    double userCurrentEarning;

    ParseUser currentUser;
    String oId;

    ProgressBar mProgressBar;

    // for the ads
    InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();
//        // show welcome msg
//        Toast.makeText(getActivity(), "مرحبا, "+currentUser.getUsername(), Toast.LENGTH_SHORT).show();

        // for the ads
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) rootView.findViewById(R.id.tv_steps);
        TvMoney = (TextView) rootView.findViewById(R.id.tv_money);
        mBtnLabel = (TextView) rootView.findViewById(R.id.btnLabel);

        mNoInternetBtn = (Button) rootView.findViewById(R.id.noInternetBtn);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mBtnStart = (at.markushi.ui.CircleButton) rootView.findViewById(R.id.btn_start);

        // check for the internet connection
        if (!isNetworkAvailable()) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mNoInternetBtn.setVisibility(View.VISIBLE);
            mBtnStart.setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.btn_start).setVisibility(View.INVISIBLE);
            TvSteps.setVisibility(View.INVISIBLE);
            TvMoney.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "الرجاء التاكد من اتصالك بالانترنت!", Toast.LENGTH_SHORT).show();

            mNoInternetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainFragment frag_name = new MainFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.content_frame, frag_name, frag_name.getTag()).commit();

                }
            });
        }

        // get stepPrice from bsck4app:
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("StepPrice");
        query2.getInBackground("3GvIDvA5Jy", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    mUpdatedStepPrice = object.getDouble("stepPrice");
                } else {
                    // something went wrong
                    Toast.makeText(getActivity(), "Error getting step price!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // make the button unclickable while wating for the user info to be retrived from back4app
        mBtnStart.setEnabled(false);


        // get current user status from back4app
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStatus");
        query.whereEqualTo("userId", currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                mProgressBar.setVisibility(View.VISIBLE);
                if (object == null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Error getting user data from the backend!", Toast.LENGTH_SHORT).show();

                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    userCurrentSteps = object.getInt("currentSteps");
                    userCurrentEarning = object.getDouble("currentEarning");
                    oId = object.getObjectId();

                    if (userCurrentSteps >= 1000){
                        userCurrentSteps = 0;
                        userCurrentEarning = 0;

                    }

                    // Show user current steps and earning on TextView
                    // 1. Steps
                    TvSteps.setText(""+userCurrentSteps);
                    // 2. Earning
                    TvMoney.setText(""+ "$"+String.format( "%.3f", userCurrentEarning ));
                    // make the btn clickable after getting user info
                    mBtnStart.setEnabled(true);


                }
            }
        });

        // When user Click on the btn
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // when user click the start btn 1st time
                if(isPressed) {

                    mProgressBar.setVisibility(View.VISIBLE);

                    // Get current user info from back4app
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStatus");
                    query.whereEqualTo("userId", currentUser.getObjectId());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                Toast.makeText(getActivity(), "Error getting user data from the backend!", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.INVISIBLE);

                            } else {
                                userCurrentSteps = object.getInt("currentSteps");
                                userCurrentEarning = object.getDouble("currentEarning");

                                numSteps = userCurrentSteps;
                                stepPrice = userCurrentEarning;

                                sensorManager.registerListener(MainFragment.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

                                mBtnStart.setImageResource(R.drawable.ic_man_standing);
                                mBtnLabel.setText("ايقاف");
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }

                // when user click the btn again
                else{
                    mBtnStart.setImageResource(R.drawable.ic_man_walking);
                    mBtnLabel.setText("استمرار");
                    sensorManager.unregisterListener(MainFragment.this);
                }

                isPressed = !isPressed;
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("الرئيسية");
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {


        if (numSteps >= 1000){
            numSteps = 0;
//            stepPrice = 0.0;
        }

        if (numSteps % 50 == 0 && numSteps != 0){
            mInterstitialAd = new InterstitialAd(getActivity());

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }

        numSteps++;
        stepPrice += mUpdatedStepPrice;

        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        TvMoney.setText("$" + String.format("%.3f", stepPrice));


        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStatus");
        // Retrieve the object by id
        query.getInBackground(oId, new GetCallback<ParseObject>() {
            public void done(ParseObject po, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.
                    po.put("currentSteps", numSteps);
                    po.put("currentEarning", stepPrice);
                    po.saveInBackground();
                } else {
                    Toast.makeText(getActivity(), "Error storing data when walking!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // isNetworkAvailable method to check if the internet is Available or not
    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    // for the ads
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

}

