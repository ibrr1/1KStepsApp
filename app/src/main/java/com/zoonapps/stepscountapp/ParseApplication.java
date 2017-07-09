package com.zoonapps.stepscountapp;

/**
 * Created by ibrahim on 6/17/17.
 */

import android.app.Application;
import android.content.Context;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class ParseApplication extends Application {
    private static ParseApplication instance = new ParseApplication();
    public static final String APPLICATION_ID = "hHHjQTziU76Rj6NWyXv5wqpHIVIwDyNgXKLCvqMm";
    public static final String CLIENT_KEY = "3nSCuhJ9TO4lu0mXVZaOFjPoHWs0VH3LxxiI3PU1";
    public static final String BACK4PAPP_API = "https://parseapi.back4app.com/";

    public ParseApplication(){
        instance = this;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server(BACK4PAPP_API).build());

        // This is the installation part
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "745828699509");
        installation.saveInBackground();
    }
}