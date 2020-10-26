/**
 * Transistor.java
 * Implements the Transistor class
 * Transistor starts up the app and sets up the basic theme (Day / Night)
 *
 * This file is part of
 * TRANSISTOR - Radio App for Android
 *
 * Copyright (c) 2015-20 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */

package org.y20k.transistor;

import android.app.Application;

import org.y20k.transistor.helpers.LogHelper;
import org.y20k.transistor.helpers.NightModeHelper;


/**
 * Transistor.class
 */
public class Transistor extends Application {

    /* Define log tag */
    private static final String LOG_TAG = Transistor.class.getSimpleName();

    private static Transistor instance = null;

    public static Transistor getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        // set Day / Night theme state
        NightModeHelper.restoreSavedState(this);

// todo remove
//        if (Build.VERSION.SDK_INT >= 28) {
//            // Android P might introduce a system wide theme option - in that case: follow system (28 = Build.VERSION_CODES.P)
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        } else {
//            // try to get last state the user chose
//            NightModeHelper.restoreSavedState(this);
//        }

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        LogHelper.v(LOG_TAG, "Transistor application terminated.");
    }

}
