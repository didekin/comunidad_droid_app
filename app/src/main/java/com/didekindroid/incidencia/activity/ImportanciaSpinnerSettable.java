package com.didekindroid.incidencia.activity;

import android.widget.Spinner;

import com.didekindroid.incidencia.dominio.IncidenciaBean;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 17:04
 */
public interface ImportanciaSpinnerSettable {

    IncidenciaBean getIncidenciaBean();

    Spinner getImportanciaSpinner();

    void onImportanciaSpinnerLoaded();
}
