package org.y20k.transistor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_COLLECTION_CUSTOM_DIRECTORY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String s = prefs.getString(getString(R.string.settings_collection_custom_key), "");
        if(s == null || s.trim().length() == 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getString(R.string.settings_collection_use_default_key), true);
            editor.commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if(preference.getKey().equals(getString(R.string.settings_collection_custom_key))) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(i, getString(R.string.settings_collection_custom)),
                        REQUEST_COLLECTION_CUSTOM_DIRECTORY);
                return true;
            }
            boolean b = super.onPreferenceTreeClick(preference);
            if(preference.getKey().equals(getString(R.string.settings_collection_use_default_key))) {
                if(((CheckBoxPreference)preference).isChecked()) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.settings_collection_custom_key), "");
                    editor.commit();
                }
            }
            return b;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == REQUEST_COLLECTION_CUSTOM_DIRECTORY) {
                Uri uri = data.getData();
                if(uri != null) {
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    // Check for the freshest data.
                    this.getContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.settings_collection_custom_key), uri.toString());
                    editor.putBoolean(getString(R.string.settings_collection_use_default_key), false);
                    editor.commit();
                }
            }
        }
    }
}