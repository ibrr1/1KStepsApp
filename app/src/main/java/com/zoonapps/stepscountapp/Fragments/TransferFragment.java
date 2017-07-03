package com.zoonapps.stepscountapp.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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


public class TransferFragment extends Fragment {

    TextView mTotalEarning;
    Spinner mTransType;

    ParseUser mCurrentUser;
    ProgressBar mProgressBar;

    int userCurrentSteps;
    double userCurrentEarning;
    private RelativeLayout mTransLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_transfer, container, false);

        // Retrieve current user from Parse.com
        mCurrentUser = ParseUser.getCurrentUser();

//        mUsernameTV = (TextView) rootView.findViewById(R.id.usernameTV);
//        mEmailTV = (TextView) rootView.findViewById(R.id.emailTV);
//        mTotalStepsTV = (TextView) rootView.findViewById(R.id.totalStepsTV);
        mTotalEarning = (TextView) rootView.findViewById(R.id.totalEarningTV);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        // get current user status from back4app
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStatus");
        query.whereEqualTo("username", mCurrentUser.getUsername());
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
//                    mUsernameTV.setText("اسم المستخدم: " + mCurrentUser.getUsername().toString());
//                    mEmailTV.setText("البريد الالكتروني: " + mCurrentUser.getEmail().toString());
//                    mTotalStepsTV.setText("مجموع الخطوات: "+ userCurrentSteps);
                    mTotalEarning.setText("رصيدك الحالي هو: "+String.format( "%.3f", userCurrentEarning ) + "$");

                }
            }
        });

        // to show snakbar
        getActivity().findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "* ملاحظة: يجب ان يكون رصيدك 40 دولار او اكثر لكي يتم تحويل المبلغ", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("X", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        View view = snackbar.getView();

        view.setBackgroundColor(Color.parseColor("#a6267e"));
        snackbar.setActionTextColor(Color.WHITE);

        TextView tv = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();

        // for spinner
        // Donation type spinner
        mTransType = ((Spinner) rootView.findViewById(R.id.transType));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter .createFromResource(getActivity(), R.array.trans_types, R.layout.spinner_center_item);
        mTransType.setAdapter(spinnerAdapter);

        String transType= mTransType.getSelectedItem().toString();


        // create editText



        return rootView;


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("حسابي");
    }
}