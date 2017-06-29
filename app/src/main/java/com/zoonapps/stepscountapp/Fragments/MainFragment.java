package com.zoonapps.stepscountapp.Fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView TvSteps, TvMoney;
    Button BtnStart;

    boolean isPressed = true;

//    SharedPreferences pref;
//    SharedPreferences.Editor editor;
    int userCurrentSteps;
    double userCurrentEarning;

    ParseUser currentUser;
    String oId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();
        // show welcome msg
        Toast.makeText(getActivity(), "Hello, "+currentUser.getUsername(), Toast.LENGTH_SHORT).show();

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) rootView.findViewById(R.id.tv_steps);
        TvMoney = (TextView) rootView.findViewById(R.id.tv_money);
        BtnStart = (Button) rootView.findViewById(R.id.btn_start);

        // SP
//        pref = getActivity().getSharedPreferences("YOUR_PREF_NAME", MODE_PRIVATE);
//        editor = pref.edit();


        // Show user current steps and earning on TextView
        // 1. Steps
//        userCurrentSteps = pref.getInt("userCurrentSteps", 0);

        // get current user status from back4app
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserCurrentStatus");
        query.whereEqualTo("username", currentUser.getUsername());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Toast.makeText(getActivity(), "error back4app", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("score", "Retrieved the object.");

                    oId = object.getObjectId();
                    // Show user current steps and earning on TextView
                    // 1. Steps
                    TvSteps.setText(""+object.getInt("userCurrentSteps"));
                    // 2. Earning
                    TvMoney.setText(""+"$"+ object.get("userCurrentEarning"));

                }
            }
        });

        // 2. Earning
//        userCurrentEarning = pref.getFloat("userCurrentEarning", 0);
//        TvMoney.setText(""+"$"+String.format( "%.3f", userCurrentEarning));

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // when user click the start btn 1st time
                if(isPressed) {
                    numSteps = userCurrentSteps;
                    stepPrice = userCurrentEarning;

                    sensorManager.registerListener(MainFragment.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                    BtnStart.setText("ايقاف");
                }

                // when user click the btn again
                else{
                    BtnStart.setText("بدأ");
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
        numSteps++;
        stepPrice += 0.001;

        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        TvMoney.setText( "$"+String.format( "%.3f", stepPrice ) );

        // store user current steps and earning
//        editor.putInt("userCurrentSteps", numSteps);
//        editor.putFloat("userCurrentEarning", stepPrice);
//        editor.commit();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserCurrentStatus");

        // Retrieve the object by id
        query.getInBackground(oId, new GetCallback<ParseObject>() {
            public void done(ParseObject po, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.
                    po.put("userCurrentSteps", numSteps);
                    po.put("userCurrentEarning", String.format( "%.3f", stepPrice ));
                    po.saveInBackground();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

