package com.didekindroid.incidencia.activity;

import android.widget.CursorAdapter;
import android.widget.Spinner;

import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 10:21
 */
public  interface AmbitoSpinnerSettable {
    IncidenciaDataDbHelper getDbHelper();
    Spinner getAmbitoSpinner();
    IncidenciaBean getIncidenciaBean();
    void onAmbitoIncidSpinnerLoaded();
    void setAmbitoSpinnerAdapter(CursorAdapter cursorAdapter);
}
