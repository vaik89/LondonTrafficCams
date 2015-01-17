package com.laboo.londontrafficcams;

/**
 * Created by VaiosK on 04/01/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.PushService;

public class Application extends android.app.Application {
    private static Application instance = new Application();
    public Application() {
        instance = this;
    }
    public static Context getContext() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        // configure Flurry
       // FlurryAgent.setLogEnabled(false);

// init Flurry
        FlurryAgent.init(this, "GRPVSRP4NKVSN2RSVZK9");


        // Enable Crash Reporting
        ParseCrashReporting.enable(this);
        // Initialize the Parse SDK.
        Parse.initialize(this, "kd20FTHIk0TCWAXcDQRlgqwe5bIZEX7OiaSUIbZ2", "3kPPf40fVkxajCtgaVUfu1gA2CxH9oeKP1xNSBd1");

        // Specify a Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, MainActivity.class);

        // Save the current installation.
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseInstallation.getCurrentInstallation().put("Mobile", Build.MODEL);
        ParseInstallation.getCurrentInstallation().put("Manufacturer", Build.MANUFACTURER);
        ParseInstallation.getCurrentInstallation().saveInBackground();
       // ParseAnalytics.trackAppOpenedInBackground(Intent.getIntent());
    }
}