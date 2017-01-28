package com.didekindroid.incidencia.activity;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 12:16
 */
interface IncidSeeListListener {

    void onIncidenciaSelected(Incidencia incidencia, int position);

    void onComunidadSpinnerSelected(Comunidad comunidadSelected);

    ArrayAdapter<IncidenciaUser> getAdapter(Activity activity);

    List<IncidenciaUser> getListFromService(long comunidadId) throws UiException;

    /**
     * Implementations provide fragments witn a comunidadId passed in a Notification PendingIntent.
     */
    long getComunidadSelected();
}
