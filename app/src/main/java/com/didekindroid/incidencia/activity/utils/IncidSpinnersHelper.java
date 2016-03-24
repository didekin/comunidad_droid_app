package com.didekindroid.incidencia.activity.utils;

import android.app.Fragment;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;

import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 10:49
 */
public enum IncidSpinnersHelper {

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

    private static final String TAG = IncidSpinnersHelper.class.getCanonicalName();
    public abstract <T extends Fragment & AmbitoSpinnerSettable> void doAmbitoIncidenciaSpinner(final T fragment);
    public abstract  <T extends Fragment & ImportanciaSpinnerSettable> void doImportanciaSpinner(final T fragment);
}
