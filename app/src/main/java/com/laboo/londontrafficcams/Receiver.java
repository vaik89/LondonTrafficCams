package com.laboo.londontrafficcams;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by VaiosK on 04/01/2015.
 */
public class Receiver extends ParsePushBroadcastReceiver  {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");

        ParseAnalytics.trackAppOpenedInBackground(intent);

        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);









    }
}