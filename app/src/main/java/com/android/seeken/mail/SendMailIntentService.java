package com.android.seeken.mail;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.seeken.data.PhoneDetails;
import com.android.seeken.utility.CommonUtils;


public class SendMailIntentService extends IntentService {
    private static final String ACTION_SEND_MAIL = "com.android.seeken.mail.action.SEND_MAIL";
    private static final String PHONE_DETAILS_PARAM = "com.android.seeken.mail.extra.PARAM_PHONE_DETAILS";

    public SendMailIntentService() {
        super("SendMailIntentService");
    }

    /**
     * @see IntentService
     */
    public static void sendMail(Context context, PhoneDetails phoneDetails) {
        Intent intent = new Intent(context, SendMailIntentService.class);
        intent.setAction(ACTION_SEND_MAIL);
        intent.putExtra(PHONE_DETAILS_PARAM, phoneDetails);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_MAIL.equals(action)) {
                final PhoneDetails phoneDetails = intent.getParcelableExtra(PHONE_DETAILS_PARAM);
                sendMail(phoneDetails);
            }
        }
    }


    private void sendMail(PhoneDetails phoneDetails) {
        try {
            String mailBody = CommonUtils.getMessage(this, phoneDetails);
            GMailSender sender = new GMailSender(phoneDetails.mailSender, phoneDetails.mailSenderPassword);
            sender.sendMail(this, phoneDetails.mailSubject, mailBody, phoneDetails.mailSender, phoneDetails.mailReceiver);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

}
