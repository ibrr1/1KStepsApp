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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.parse.SaveCallback;
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

    EditText mET1, mET2, mET3, mET4, mET5, mET6, mET7;
    Button mTransBtn;

    String mIBANPattern = "[a-zA-Z].*";

    int mThreshold;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_transfer, container, false);

        // Retrieve current user from Parse.com
        mCurrentUser = ParseUser.getCurrentUser();

//        mUsernameTV = (TextView) rootView.findViewById(R.id.usernameTV);
//        mEmailTV = (TextView) rootView.findViewById(R.id.emailTV);
//        mTotalStepsTV = (TextView) rootView.findViewById(R.id.totalStepsTV);
        mTotalEarning = (TextView) rootView.findViewById(R.id.totalEarningTV);
        mET1 = (EditText) rootView.findViewById(R.id.et1);
        mET2 = (EditText) rootView.findViewById(R.id.et2);
        mET3 = (EditText) rootView.findViewById(R.id.et3);
        mET4 = (EditText) rootView.findViewById(R.id.et4);
        mET5 = (EditText) rootView.findViewById(R.id.et5);
        mET6 = (EditText) rootView.findViewById(R.id.et6);
        mET7 = (EditText) rootView.findViewById(R.id.et7);
        mTransBtn = (Button) rootView.findViewById(R.id.transBtn);

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
//                    mUsernameTV.setText("اسم المستخدم: " + mCurrentUser.getUsername().toString());
//                    mEmailTV.setText("البريد الالكتروني: " + mCurrentUser.getEmail().toString());
//                    mTotalStepsTV.setText("مجموع الخطوات: "+ userCurrentSteps);
                    mTotalEarning.setText("رصيدك الحالي هو: "+String.format( "%.3f", userCurrentEarning ) + "$");

                }
            }
        });

        // get threshold from bsck4app:
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("StepPrice");
        query2.getInBackground("zsXlkjl0XE", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    mThreshold = object.getInt("stepPrice");

                    // to show snakbar
                    getActivity().findViewById(android.R.id.content);
                    final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "* ملاحظة: يجب ان يكون رصيدك "+ mThreshold + "$"+" او اكثر لكي يتم تحويل المبلغ", Snackbar.LENGTH_INDEFINITE);

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
                } else {
                    // something went wrong
                    Toast.makeText(getActivity(), "Error getting step price!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // for spinner
        // Donation type spinner
        mTransType = ((Spinner) rootView.findViewById(R.id.transType));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter .createFromResource(getActivity(), R.array.trans_types, R.layout.spinner_center_item);
        mTransType.setAdapter(spinnerAdapter);



        // get the selected payment type position
//        mTransTypePosition = mTransType.getSelectedItemPosition();

        mTransType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (mTransType.getSelectedItemPosition() == 0) {
                    mET1.setVisibility(View.GONE);
                    mET2.setVisibility(View.GONE);
                    mET3.setVisibility(View.GONE);
                    mET4.setVisibility(View.GONE);
                    mET5.setVisibility(View.GONE);
                    mET6.setVisibility(View.GONE);
                    mET7.setVisibility(View.GONE);

                } else if (mTransType.getSelectedItemPosition() == 1) {
                    mET2.setVisibility(View.GONE);
                    mET3.setVisibility(View.GONE);
                    mET4.setVisibility(View.GONE);
                    mET5.setVisibility(View.GONE);
                    mET6.setVisibility(View.GONE);
                    mET7.setVisibility(View.GONE);

                    mET1.setVisibility(View.VISIBLE);
                    mET1.setText("");
                    mET1.setBackground(getResources().getDrawable(R.drawable.shape2));
                    mET1.setHint("البريد الالكتروني او الجوال لدى باي بال");

                } else if (mTransType.getSelectedItemPosition() == 2) {
                    mET5.setVisibility(View.GONE);
                    mET6.setVisibility(View.GONE);
                    mET7.setVisibility(View.GONE);

                    mET1.setVisibility(View.VISIBLE);
                    mET2.setVisibility(View.VISIBLE);
                    mET3.setVisibility(View.VISIBLE);
                    mET4.setVisibility(View.VISIBLE);

                    mET1.setText("");
                    mET2.setText("");
                    mET3.setText("");
                    mET4.setText("");

                    mET4.setBackground(getResources().getDrawable(R.drawable.shape2));


                    mET1.setHint("الاسم الثلاثي");
                    mET2.setHint("اسم البنك");
                    mET3.setHint("رقم الايبان");
                    mET4.setHint("تأكيد رقم الايبان");

                }  else if (mTransType.getSelectedItemPosition() == 3){
                    mET1.setVisibility(View.VISIBLE);
                    mET2.setVisibility(View.VISIBLE);
                    mET3.setVisibility(View.VISIBLE);
                    mET4.setVisibility(View.VISIBLE);
                    mET5.setVisibility(View.VISIBLE);
                    mET6.setVisibility(View.VISIBLE);
                    mET7.setVisibility(View.VISIBLE);

                    mET1.setText("");
                    mET2.setText("");
                    mET3.setText("");
                    mET4.setText("");
                    mET5.setText("");
                    mET6.setText("");
                    mET7.setText("");

                    mET7.setBackground(getResources().getDrawable(R.drawable.shape2));

                    mET1.setHint("الاسم الكامل");
                    mET2.setHint("الدولة");
                    mET3.setHint("المدينة");
                    mET4.setHint("اسم البنك");
                    mET5.setHint("عنوان البنك");
                    mET5.setHint("فرع البنك");
                    mET6.setHint("رقم الايبان");
                    mET7.setHint("تأكيد رقم الايبان");
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

        mTransBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String et1 = mET1.getText().toString();
                String et2 = mET2.getText().toString();
                String et3 = mET3.getText().toString();
                String et4 = mET4.getText().toString();
                String et5 = mET5.getText().toString();
                String et6 = mET6.getText().toString();
                String et7 = mET7.getText().toString();

                if (userCurrentEarning >= mThreshold) {


                    if (mTransType.getSelectedItemPosition() == 0) {
                        Toast.makeText(getActivity(), "الرجاء اختيار طريقة تحويل الرصيد!", Toast.LENGTH_SHORT).show();
                    } else if (mTransType.getSelectedItemPosition() == 1) {

                        if (et1.isEmpty()) {
                            Toast.makeText(getActivity(), "يجب تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressBar.setVisibility(View.VISIBLE);
                            ParseObject po = new ParseObject("PaymentRequests");
                            po.put("username", mCurrentUser);
                            po.put("userEmail", mCurrentUser.getEmail());
                            po.put("method", mTransType.getSelectedItem().toString());

                            po.put("PayPalID", mET1.getText().toString());

                            po.put("amount", String.format("%.3f", userCurrentEarning));

                            po.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "تم ارسال الطلب بنجاح", Toast.LENGTH_SHORT).show();

                                    } else {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "حدث خطا", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    } else if (mTransType.getSelectedItemPosition() == 2) {

                        if (et1.isEmpty() || et2.isEmpty() || et3.isEmpty() || et4.isEmpty()) {
                            Toast.makeText(getActivity(), "يجب تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                        } else if (!et3.equals(et4)) {
                            Toast.makeText(getActivity(), "رقم الايبان وتاكيد رقم الايبان يجب ان يكون متشابه", Toast.LENGTH_SHORT).show();

                        } else if (!et3.substring(0, 1).toString().matches(mIBANPattern) || !et3.substring(1, 2).toString().matches(mIBANPattern)) {
                            Toast.makeText(getActivity(), "رقم الايبان غير صحيح", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressBar.setVisibility(View.VISIBLE);

                            ParseObject po = new ParseObject("PaymentRequests");
                            po.put("username", mCurrentUser);
                            po.put("userEmail", mCurrentUser.getEmail());
                            po.put("method", mTransType.getSelectedItem().toString());

                            po.put("fullName", mET1.getText().toString());
                            po.put("bankName", mET2.getText().toString());
                            po.put("IBAN", mET3.getText().toString());

                            po.put("amount", String.format("%.3f", userCurrentEarning));
                            po.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "تم ارسال الطلب بنجاح", Toast.LENGTH_SHORT).show();

                                    } else {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "حدث خطا", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else if (mTransType.getSelectedItemPosition() == 3) {

                        if (et1.isEmpty() || et2.isEmpty() || et3.isEmpty() || et4.isEmpty() ||
                                et5.isEmpty() || et6.isEmpty() || et7.isEmpty()) {
                            Toast.makeText(getActivity(), "يجب تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                        } else if (!et6.equals(et7)) {
                            Toast.makeText(getActivity(), "رقم الايبان وتاكيد رقم الايبان يجب ان يكون متشابه", Toast.LENGTH_SHORT).show();

                        } else if (!et6.substring(0, 1).toString().matches(mIBANPattern) ||
                                !et7.substring(1, 2).toString().matches(mIBANPattern)) {
                            Toast.makeText(getActivity(), "رقم الايبان غير صحيح", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressBar.setVisibility(View.VISIBLE);
                            ParseObject po = new ParseObject("PaymentRequests");
                            po.put("username", mCurrentUser);
                            po.put("userEmail", mCurrentUser.getEmail());
                            po.put("method", mTransType.getSelectedItem().toString());

                            po.put("fullName", mET1.getText().toString());
                            po.put("country", mET2.getText().toString());
                            po.put("city", mET3.getText().toString());
                            po.put("bankName", mET4.getText().toString());
                            po.put("bankBranch", mET5.getText().toString());
                            po.put("IBAN", mET6.getText().toString());


                            po.put("amount", String.format("%.3f", userCurrentEarning));
                            po.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "تم ارسال الطلب بنجاح", Toast.LENGTH_SHORT).show();

                                    } else {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(), "حدث خطا", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                } else{
                    Toast.makeText(getActivity(), "رصيدك الحالي اقل من"+ mThreshold+"$", Toast.LENGTH_SHORT).show();

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