package com.ifchan.reader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.ifchan.reader.MainActivity;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by daily on 12/12/17.
 */

public class PermissionUtils {
    public static void applyForPhoneStatePermission(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new
                    String[]{READ_PHONE_STATE}, 1);
        }
    }
}
