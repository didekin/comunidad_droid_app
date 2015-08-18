package com.didekindroid.oferta;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import com.didekindroid.R;

/**
 * User: pedro
 * Date: 18/02/15
 * Time: 11:11
 */
public class OfertasMaxFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "OfertasMaxFragment";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.d(TAG, "onSharedPreferenceChanged()");

        String keyPrefMaxOffers = getString(R.string.maxOffers_keyPref);
        if (keyPrefMaxOffers.equals(key)) {
            Preference maxOffers = findPreference(key);
            maxOffers.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause");
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}