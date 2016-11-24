package com.didekindroid.incidencia.activity.utils;

import android.widget.Spinner;

import com.didekin.incidencia.dominio.Incidencia;
import com.didekindroid.incidencia.dominio.IncidImportanciaBean;

/**
 * User: pedro@didekin
 * Date: 27/01/16
 * Time: 17:04
 */
public interface ImportanciaSpinnerSettable {

    IncidImportanciaBean getIncidImportanciaBean();

    Spinner getImportanciaSpinner();

    void onImportanciaSpinnerLoaded();

    Incidencia getIncidencia();
}
