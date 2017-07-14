package com.android.seeken.data;

/*
 * Created by Jovin on 26-06-2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class PhoneDetails implements Parcelable {

    public String activatedDate = "";
    public String model = "";
    public String serialNumber = "";
    public String IMEI_1 = "";
    public String IMEI_2 = "";
    public String phoneNumber = "";

    public String mailSubject = "";
    public String mailSender = "";
    public String mailSenderPassword = "";
    public String mailReceiver = "";
    public String smsReceiver = "";

    public PhoneDetails() {
    }

    private PhoneDetails(Parcel in) {
        activatedDate = in.readString();
        model = in.readString();
        serialNumber = in.readString();
        IMEI_1 = in.readString();
        IMEI_2 = in.readString();
        phoneNumber = in.readString();

        mailSubject = in.readString();
        mailSender = in.readString();
        mailSenderPassword = in.readString();
        mailReceiver = in.readString();
        smsReceiver = in.readString();
    }

    public static final Creator<PhoneDetails> CREATOR = new Creator<PhoneDetails>() {
        @Override
        public PhoneDetails createFromParcel(Parcel in) {
            return new PhoneDetails(in);
        }

        @Override
        public PhoneDetails[] newArray(int size) {
            return new PhoneDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(activatedDate);
        parcel.writeString(model);
        parcel.writeString(serialNumber);
        parcel.writeString(IMEI_1);
        parcel.writeString(IMEI_2);
        parcel.writeString(phoneNumber);

        parcel.writeString(mailSubject);
        parcel.writeString(mailSender);
        parcel.writeString(mailSenderPassword);
        parcel.writeString(mailReceiver);
        parcel.writeString(smsReceiver);
    }
}
