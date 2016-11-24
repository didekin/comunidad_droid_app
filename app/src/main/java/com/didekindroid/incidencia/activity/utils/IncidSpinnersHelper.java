package com.didekindroid.incidencia.activity.utils;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidSeeUserComuImportanciaAc;

import timber.log.Timber;

import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_OBJECT;

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
        Timber.d("doAmbitoIncidenciaSpinner()");

        new AmbitoIncidenciaSpinnerSetter<>(fragment).execute();

        fragment.getAmbitoSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("onItemSelected()");
                Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
                fragment.getIncidenciaBean().setCodAmbitoIncid(cursor.getShort(0)); // _ID.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Timber.d("onNothingSelected()");
            }
        });
    }

    public <T extends Fragment & ImportanciaSpinnerSettable> void doImportanciaSpinner(final T fragment)
    {
        Timber.d("doImportanciaSpinner()");

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
                Timber.d("mImportanciaSpinner.onItemSelected()");
                fragment.getIncidImportanciaBean().setImportancia((short) position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Timber.d("mImportanciaSpinner.onNothingSelected()");
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public <T extends Fragment & ImportanciaSpinnerSettable> void initUserComusImportanciaView(final T fragment)
    {
        Timber.d("initUserComusImportanciaView()");

        TextView mSeeImportanciaView = (TextView) fragment.getView().findViewById(R.id.incid_importancia_otros_view);
        mSeeImportanciaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mSeeImportanciaView.onClick()");
                Intent intent = new Intent(fragment.getContext(), IncidSeeUserComuImportanciaAc.class);
                intent.putExtra(INCIDENCIA_OBJECT.key, fragment.getIncidencia());
                fragment.getActivity().startActivity(intent);
            }
        });
    }
}
