package com.didekindroid.incidencia.activity.utils;

import android.app.Fragment;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;

import com.didekindroid.R;
import com.didekindroid.incidencia.activity.AmbitoIncidenciaSpinnerSetter;
import com.didekindroid.incidencia.activity.AmbitoSpinnerSettable;
import com.didekindroid.incidencia.activity.ImportanciaSpinnerSettable;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 10:49
 */
public enum IncidRegEditHelper {

    HELPER {
        public <T extends Fragment & AmbitoSpinnerSettable> void doAmbitoIncidenciaSpinner(final T fragment)
        {
            new AmbitoIncidenciaSpinnerSetter<>(fragment).execute();

            fragment.getAmbitoSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.d(TAG, "onItemSelected()");
                    Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
                    fragment.getIncidenciaBean().setCodAmbitoIncid(cursor.getShort(0)); // _ID.
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                    Log.d(TAG, "onNothingSelected()");
                }
            });
        }

        public <T extends Fragment & ImportanciaSpinnerSettable> void doImportanciaSpinner(final T fragment)
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    fragment.getActivity(),
                    R.array.IncidImportanciaArray,
                    R.layout.app_spinner_1_dropdown_item);
            adapter.setDropDownViewResource(R.layout.app_spinner_1_dropdown_item);
            fragment.getImportanciaSpinner().setAdapter(adapter);
            fragment.onImportanciaSpinnerLoaded();

            fragment.getImportanciaSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.d(TAG, "mImportanciaSpinner.onItemSelected()");
                    fragment.getIncidImportanciaBean().setImportancia((short) position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                    Log.d(TAG, "mImportanciaSpinner.onNothingSelected()");
                }
            });
        }
    },;

    private static final String TAG = IncidRegEditHelper.class.getCanonicalName();
    public abstract <T extends Fragment & AmbitoSpinnerSettable> void doAmbitoIncidenciaSpinner(final T fragment);
    public abstract  <T extends Fragment & ImportanciaSpinnerSettable> void doImportanciaSpinner(final T fragment);
}
