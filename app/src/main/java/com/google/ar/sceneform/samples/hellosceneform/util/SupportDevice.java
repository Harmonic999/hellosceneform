package com.google.ar.sceneform.samples.hellosceneform.util;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class SupportDevice {

    private static final double MIN_OPENGL_VERSION = 3.0;

    public static boolean check(final Activity activity) {
        String openGlVersionString =
                ((ActivityManager) Objects
                        .requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();

        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "App requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "App requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

}
