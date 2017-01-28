package com.didekindroid.incidencia.activity.utils;

import android.widget.Spinner;

import com.didekindroid.incidencia.dominio.IncidImportanciaBean;
import com.didekinlib.model.incidencia.dominio.Incidencia;

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
