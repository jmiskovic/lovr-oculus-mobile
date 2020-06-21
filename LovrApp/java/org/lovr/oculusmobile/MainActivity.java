/************************************************************************************
Filename    :   MainActivity.java
Content     :   
Created     :   
Authors     :   
Copyright   :   Copyright (c) Facebook Technologies, LLC and its affiliates. All rights reserved.
*************************************************************************************/
package org.lovr.oculusmobile;

/*
When using NativeActivity, we currently need to handle loading of dependent 
shared libraries manually before a shared library that depends on them
is loaded, since there is not currently a way to specify a shared library
dependency for NativeActivity via the manifest meta-data.
The simplest method for doing so is to subclass NativeActivity with
an empty activity that calls System.loadLibrary on the dependent
libraries, which is unfortunate when the goal is to write a pure
native C/C++ only Android activity.
A native-code only solution is to load the dependent libraries
dynamically using dlopen(). However, there are a few considerations,
see: https://groups.google.com/forum/#!msg/android-ndk/l2E2qh17Q6I/wj6s_6HSjaYJ
1. Only call dlopen() if you're sure it will succeed as the bionic
dynamic linker will remember if dlopen failed and will not re-try
a dlopen on the same lib a second time.
2. Must rememeber what libraries have already been loaded to avoid
infinitely looping when libraries have circular dependencies.
*/

import android.Manifest;
import android.app.NativeActivity;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.os.Bundle;
import 	android.util.Log;

public class MainActivity extends android.app.NativeActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
	
	/** Load jni .so on initialization */
    static {
        System.loadLibrary( "vrapi" );
        System.loadLibrary( "lovractivity" );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LOVR", "MainActivity.onCreate()");
        RequestPermissionsIfNeeded();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Log.i("LOVR", "RECORD_AUDIO permissions have now been granted.");
        }
        else
        {
            Log.i("LOVR", "RECORD_AUDIO permissions were denied.");
        }
    }
    
    private void RequestPermissionsIfNeeded()
    {
        int existingPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(existingPermission != PackageManager.PERMISSION_GRANTED)
        {
            Log.i("LOVR", "Asking for RECORD_AUDIO permissions.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        else
        {
          Log.i("LOVR", "RECORD_AUDIO permissions already granted.");
        }
    }
}
