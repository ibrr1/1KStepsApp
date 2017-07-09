package com.zoonapps.stepscountapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zoonapps.stepscountapp.Fragments.MainFragment;
import com.zoonapps.stepscountapp.Fragments.MyAccountFragment;
import com.zoonapps.stepscountapp.Fragments.RequestsFragment;
import com.zoonapps.stepscountapp.Fragments.TransferFragment;
import com.zoonapps.stepscountapp.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // to make the Drawer from right to left
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //add this line to display menu1 when the activity is loaded.
        displaySelectedScreen(R.id.nav_menu1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_menu1:
                fragment = new MainFragment();
                break;

            case R.id.nav_menu2:
                fragment = new MyAccountFragment();
                break;

            case R.id.nav_menu3:
                fragment = new TransferFragment();
                break;

            case R.id.nav_menu4:
                fragment = new RequestsFragment();
                break;

            case R.id.nav_menu5:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;

            case R.id.nav_menu6:
                feedback();
                break;

            case R.id.nav_menu7:
                Logout();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    // Logout method
    public void Logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Message
        alertDialog.setMessage(Html.fromHtml("<font color='#a6267e'> <strong> هل انت متأكد من تسجيل الخروج </strong></font>"));

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_account);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("نعم",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Logout current user
                        ParseUser.logOut();
                        startActivity(new Intent(MainActivity.this,
                                LoginActivity.class));
                        finish();
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("لا",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();

    }

    //========Feedback===============

    // feedback method to allow the user to send feedback
    public void feedback() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.prompts_feedback, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setIcon(R.drawable.ic_feedback_black_24dp);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        TextView title = new TextView(this);

// You Can Customise your Title here
        title.setText("تواصل معنا");
        title.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        alertDialogBuilder.setCustomTitle(title);

        final EditText userInput = ((EditText) promptsView.findViewById(R.id.editTextDialogUserInput));

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("ارسال", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // When the user clicks "Save," upload the
                // post to Parse
                // Create the Post object
                if(userInput.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"الرساله قصيره جدا!", Toast.LENGTH_SHORT).show();
                }
                else{
                    ParseObject post = new ParseObject("Feedback");
                    post.put("Feedback", userInput.getText().toString());

                    // Create an author relationship with the
                    // current user
                    post.put("Username",ParseUser.getCurrentUser());

                    // Save the post and return
                    post.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                setResult(RESULT_OK);

                            } else {
                                Toast.makeText(getApplicationContext(),"Error saving: "+ e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                    Toast.makeText(getBaseContext(),"تم الارسال..شكرا",Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNegativeButton("الغاء",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }

                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}