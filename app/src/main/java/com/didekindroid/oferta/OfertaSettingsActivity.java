package com.didekindroid.oferta;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * User: pedro
 * Date: 17/02/15
 * Time: 19:39
 */
public class OfertaSettingsActivity extends Activity {

    private static final String TAG = "OfertaSettingsActivity";
    private PreferenceFragment maxOfferFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new OfertasMaxFragment()).commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        Log.d(TAG,"onAttachFragment(");
        super.onAttachFragment(fragment);
        maxOfferFragment = (PreferenceFragment) fragment;
    }

    public PreferenceFragment getMaxOfferFragment()
    {
        return maxOfferFragment;
    }
}