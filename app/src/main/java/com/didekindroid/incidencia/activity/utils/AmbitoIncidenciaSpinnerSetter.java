package com.didekindroid.incidencia.activity.utils;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.incidencia.IncidenciaDataDb.AmbitoIncidencia.ambito;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 10:00
 */
class AmbitoIncidenciaSpinnerSetter<T extends Fragment & AmbitoSpinnerSettable> extends AsyncTask<Void, Void, Cursor> {

    private T mFragment;

    AmbitoIncidenciaSpinnerSetter(T mFragment)
    {
        this.mFragment = mFragment;
    }

    @Override
    protected Cursor doInBackground(Void... params)
    {
        Timber.d("doInBackground()");
        return mFragment.getDbHelper().doAmbitoIncidenciaCursor();
    }

    @Override
    protected void onPostExecute(Cursor cursor)
    {
        if (checkPostExecute(mFragment.getActivity())) return;

        Timber.d("onPostExecute()");

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
