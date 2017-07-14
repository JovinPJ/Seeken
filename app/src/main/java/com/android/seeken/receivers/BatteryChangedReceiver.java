package com.android.seeken.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.seeken.services.SeekenService;
import com.android.seeken.utility.Constants;
import com.android.seeken.utility.PreferenceHelper;

public class BatteryChangedReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (PreferenceHelper.isDataSent(context))
            return;
        Intent seekenServiceIntent = new Intent(context, SeekenService.class);
        seekenServiceIntent.putExtra(Constants.ACTION_ARG, intent.getAction());
        context.startService(seekenServiceIntent);
    }
}
