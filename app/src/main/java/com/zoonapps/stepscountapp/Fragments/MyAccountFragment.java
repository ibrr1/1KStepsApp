package com.zoonapps.stepscountapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zoonapps.stepscountapp.R;

/**
 * Created by Belal on 18/09/16.
 */


public class MyAccountFragment extends Fragment {

    TextView mUsernameTV, mEmailTV, mTotalStepsTV, mTotalEarning;
    ParseUser mCurrentUser;
    ProgressBar mProgressBar;

    int userCurrentSteps;
    double userCurrentEarning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);

        // Retrieve current user from Parse.com
        mCurrentUser = ParseUser.getCurrentUser();

        mUsernameTV = (TextView) rootView.findViewById(R.id.usernameTV);
        mEmailTV = (TextView) rootView.findViewById(R.id.emailTV);
        mTotalStepsTV = (TextView) rootView.findViewById(R.id.totalStepsTV);
        mTotalEarning = (TextView) rootView.findViewById(R.id.totalEarningTV);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        // get current user status from back4app
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStatus");
        query.whereEqualTo("userId", mCurrentUser.getObjectId());
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

                    // Show user current info
                    mUsernameTV.setText("اسم المستخدم: " + mCurrentUser.getUsername().toString());
                    mEmailTV.setText("البريد الالكتروني: " + mCurrentUser.getEmail().toString());
                    mTotalStepsTV.setText("مجموع الخطوات: "+ userCurrentSteps);
                    mTotalEarning.setText("الرصيد الحالي: "+String.format( "%.3f", userCurrentEarning ) + "$");

                }
            }
        });

        return rootView;


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("حسابي");
    }
}