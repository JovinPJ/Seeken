package com.android.seeken.utility;

import android.content.Context;
import android.util.Log;

import com.android.seeken.R;
import com.android.seeken.data.PhoneDetails;

/**
 * Common Methods
 * Created by Jovin on 28-06-2017.
 */

public class CommonUtils {

    private CommonUtils() {
        //no instance
    }

    public static String getMessage(Context context, PhoneDetails phoneDetails) {
        String msgBody = "";
        try {
            msgBody = context.getString(R.string.msg_content, phoneDetails.activatedDate,
                    phoneDetails.model, phoneDetails.serialNumber, phoneDetails.IMEI_1,
                    phoneDetails.IMEI_2, phoneDetails.phoneNumber);

        } catch (Exception e) {
            Log.e("mailBody", e.getMessage(), e);
        }

        return msgBody;
    }

}
