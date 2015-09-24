package com.livejoints;

import android.app.Application;
import android.util.Log;

import com.livejoints.data.ParseSensorSummary;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class ParseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Log.d("APP", "ParseApplication onCreated start");

    // Initialize Crash Reporting.
    ParseCrashReporting.enable(this);
    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    ParseObject.registerSubclass(ParseSensorSummary.class);
    // Add your initialization code here
    //Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
    Parse.initialize(this, BuildConfig.PARSE_APPLICATION_ID, BuildConfig.PARSE_CLIENT_KEY);



    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    // defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

    Log.d("APP", "ParseApplication onCreated end");
  }
}
