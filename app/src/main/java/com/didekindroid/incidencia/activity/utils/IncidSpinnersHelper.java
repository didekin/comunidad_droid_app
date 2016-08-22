package com.didekindroid.incidencia.activity.utils;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidSeeUserComuImportanciaFr;

import static com.didekindroid.common.activity.BundleKey.INCID_ACTIVITY_VIEW_ID;
import static com.didekindroid.common.activity.FragmentTags.incid_see_usercomus_importancia_fr_tag;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 10:49
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public enum IncidSpinnersHelper {

    HELPER,;

    public <T extends Fragment & AmbitoSpinnerSettable> void doAmbitoIncidenciaSpinner(final T fragment)
    {
        Log.d(TAG, "doAmbitoIncidenciaSpinner()");

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
        Log.d(TAG, "doImportanciaSpinner()");

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

    @SuppressWarnings("ConstantConditions")
    public <T extends Fragment & ImportanciaSpinnerSettable> void initUserComusImportanciaView(final T fragment)
    {
        Log.d(TAG, "setUserComusImportanciaView()");

        TextView mSeeImportanciaView = (TextView) fragment.getView().findViewById(R.id.incid_importancia_otros_view);
        mSeeImportanciaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mSeeImportanciaView.onClick()");
                IncidSeeUserComuImportanciaFr importanciaSeeByUsersFr = IncidSeeUserComuImportanciaFr.newInstance(fragment.getIncidencia());
                fragment.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(fragment.getArguments().getInt(INCID_ACTIVITY_VIEW_ID.key), importanciaSeeByUsersFr, incid_see_usercomus_importancia_fr_tag)
                        .addToBackStack(importanciaSeeByUsersFr.getClass().getName())
                        .commit();
            }
        });
    }

    private static final String TAG = IncidSpinnersHelper.class.getCanonicalName();
}
