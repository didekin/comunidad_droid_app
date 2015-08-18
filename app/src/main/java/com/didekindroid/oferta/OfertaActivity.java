package com.didekindroid.oferta;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.didekindroid.R;

public class OfertaActivity extends Activity implements OfertasSummaryFragment.OnOfferSelectedListener {

    private static final String TAG = "OfertaActivity";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ofertas_layout);

        // If small layout:
        if (findViewById(R.id.ofertas_ac_one_pane_frg_container) != null) {

            Log.d(TAG, "onCreate(). fragment_one_pane_container != null");

            // If we're being restored from a previous state, then we don't need to do anything.
            if (savedInstanceState != null) {
                return;
            }
            OfertasSummaryFragment firstFragment = new OfertasSummaryFragment();
            firstFragment.setArguments(getIntent().getExtras());

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.ofertas_ac_one_pane_frg_container, firstFragment) //ViewGroup where the fragment should be placed.
                    .commit();
        }
    }

    public void onOfferSelected(OfertaBean ofertaBean)
    {
        Log.d(TAG, "onOfferSelected().");

        FragmentManager fragmentManager = getFragmentManager();
        OfertaFragment ofertaFragment = (OfertaFragment) fragmentManager.findFragmentById(R.id.oferta_fragment);

        if (ofertaFragment != null) {

            Log.d(TAG, "onOfferSelected(), large layout.");
            ofertaFragment.updateOfferView(ofertaBean.getDescription());

        } else {

            Log.d(TAG, "onOfferSelected(), small layout.");

            // Create fragment and give it an argument for the selected offer.
            OfertaFragment newFragment = new OfertaFragment();
            Bundle args = new Bundle();
            args.putString(OfertaFragment.ARG_DESCRIP, ofertaBean.getDescription());
            newFragment.setArguments(args);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.ofertas_ac_one_pane_frg_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /*
   Called after the activity has been stopped, just prior to it being started again.
   Always followed by onStart().
   You should usually use onStart() as the counterpart to the onStop() method, because the system calls onStart()
   both when it creates your activity and when it restarts the activity from the stopped state.
   */
    @Override
    protected void onRestart()
    {
        Log.d(TAG, "onRestart()");
        super.onRestart();
    }

    /*
    Between onStart() and onStop(), you can maintain resources that are needed to show the
    activity to the user. For example, you can register a BroadcastReceiver in onStart()
    to monitor changes that impact your UI, and unregister it in onStop() when the user
    can no longer see what you are displaying
    */
    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    /*
    Instead of restoring the state during onCreate() you may choose to implement onRestoreInstanceState(), which
    the system calls after the onStart() method. The system calls onRestoreInstanceState() only if there is a
    saved state to restore, so you do not need to check whether the Bundle is null.
    */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    /*
    You should implement onResume() to initialize components that you release during onPause() and perform any
    other initializations that must occur each time the activity enters the Resumed state
    */
    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    /*
    This method is typically used to commit unsaved changes to persistent data (no DB), stop
    animations and other things that may be consuming CPU. Release system resources, such as broadcast receivers, handles to
    sensors (like GPS), or any resources that may affect battery life.
    */
    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause().");
        super.onPause();
    }

    /*
    The system calls onSaveInstanceState() before making the activity vulnerable to
    destruction. The system passes this method a Bundle in which you can save state
    information about the activity as name-value pairs.
    */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    /*
    You should use it to perform larger, more CPU intensive shut-down operations, such as writing
    information to a database.
    */
    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    /*It is your last chance to clean out resources that could lead to a memory leak, so you should be
    sure that additional threads are destroyed and other long-running actions like method tracing are also stopped.
    */
    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "Enters onDestroy()");
        super.onDestroy();
    }

//    ..... ACTION BAR ....

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "Enters onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_general, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "Enters onOptionsItemSelected()");

        switch (item.getItemId()) {
            case R.id.mn_settings:
                Log.d(TAG, "Enters onOptionsItemSelected(), mn_settings");
                Intent intent = new Intent(getBaseContext(), OfertaSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.mn_budgets:
                return true;
            case R.id.mn_incidents:
                return true;
            case R.id.mn_alerts:
                return true;
            case R.id.mn_data:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
