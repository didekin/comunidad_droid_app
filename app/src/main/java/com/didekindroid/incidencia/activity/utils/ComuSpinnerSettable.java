package com.didekindroid.incidencia.activity.utils;

import android.widget.ArrayAdapter;

import com.didekinlib.model.comunidad.Comunidad;


/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 14:14
 */
public interface ComuSpinnerSettable {
    void setComunidadSpinnerAdapter(ArrayAdapter<Comunidad> comunidades);
    void onComunidadSpinnerLoaded();
}
