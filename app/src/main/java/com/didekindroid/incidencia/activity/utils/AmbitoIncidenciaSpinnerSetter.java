package com.didekindroid.incidencia.activity.utils;

import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.didekindroid.R;

import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.ambito;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 10:00
 */
public class AmbitoIncidenciaSpinnerSetter<T extends Fragment & AmbitoSpinnerSettable> extends AsyncTask<Void, Void, Cursor> {

    private T mFragment;
    private final String TAG = AmbitoIncidenciaSpinnerSetter.class.getCanonicalName();

    public AmbitoIncidenciaSpinnerSetter(T mFragment)
    {
        this.mFragment = mFragment;
    }

    @Override
    protected Cursor doInBackground(Void... params)
    {
        Log.d(TAG, "doInBackground()");
        return mFragment.getDbHelper().doAmbitoIncidenciaCursor();
    }

    @Override
    protected void onPostExecute(Cursor cursor)
    {
        Log.d(TAG, "onPostExecute()");

        String[] fromColDB = new String[]{ambito};
        int[] toViews = new int[]{R.id.app_spinner_1_dropdown_item};
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(
                mFragment.getActivity(),
                R.layout.app_spinner_1_dropdown_item,
                cursor,
                fromColDB,
                toViews,
                0);
        mFragment.setAmbitoSpinnerAdapter(cursorAdapter);
        mFragment.onAmbitoIncidSpinnerLoaded();
    }
}
