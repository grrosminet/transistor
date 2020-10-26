/**
 * StorageHelper.java
 * Implements the StorageHelper class
 * A StorageHelper provides reliable access to Androids external storage
 *
 * This file is part of
 * TRANSISTOR - Radio App for Android
 *
 * Copyright (c) 2015-20 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package org.y20k.transistor.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.os.EnvironmentCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.preference.PreferenceManager;

import org.y20k.transistor.R;

import java.io.File;


/**
 * StorageHelper class
 */
public final class StorageHelper {

    /* Define log tag */
    private static final String LOG_TAG = StorageHelper.class.getSimpleName();


    /* Getter for collection directory */
    public static DocumentFile getCollectionDirectory(Context context) {
        return findCollectionDirectory(context);
    }


    /* Checks if given folder holds any m3u files */
    public static boolean storageHasStationPlaylistFiles(Context context) {
        DocumentFile collectionDirectory = findCollectionDirectory(context);
        if (!collectionDirectory.isDirectory()) {
            LogHelper.i(LOG_TAG, "Given file object is not a directory.");
            return false;
        }
        DocumentFile[] listOfFiles = collectionDirectory.listFiles();
        for (DocumentFile file : listOfFiles) {
            if (file.getName().endsWith(".m3u")) {
                return true;
            }
        }
        LogHelper.i(LOG_TAG, "External storage does not contain any station playlist files.");
        return false;
    }


    /* Return a write-able sub-directory from external storage  */
    private static DocumentFile findCollectionDirectory(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(!prefs.getBoolean(context.getString(R.string.settings_collection_use_default_key), false)) {
            String s = prefs.getString(context.getString(R.string.settings_collection_custom_key), "");
            if (s != null || s.trim().length() > 0) {
                Uri uri = Uri.parse(s);
                try {
                    DocumentFile doc = DocumentFile.fromTreeUri(context, uri);
                    if (doc != null && doc.exists() && doc.isDirectory()) {
                        return doc;
                    } else {
                        Log.e("Storage", "Unable to read uri " + s + " ... network error ?");
                    }
                }
                catch(Throwable t) {
                    Log.e("CCF", "Erreur sur l'URI " + s + " : " + t.getMessage());
                }
            }
        }

        String subDirectory = "Collection";
        File[] storage = context.getExternalFilesDirs(subDirectory);
        for (File file : storage) {
            if (file != null) {
                String state = EnvironmentCompat.getStorageState(file);
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    LogHelper.v(LOG_TAG, "External storage: " + file.toString());
                    if(!file.exists())
                        file.mkdirs();
                    return DocumentFile.fromFile(file);
                }
            }
        }
        return null;
    }

}
