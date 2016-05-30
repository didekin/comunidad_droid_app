package com.didekindroid.incidencia.activity;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.common.activity.UiException;

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 12:16
 */
public interface IncidSeeListListener {

    void onIncidenciaSelected(Incidencia incidencia, int position);

    void onComunidadSpinnerSelected(Comunidad comunidadSelected);

    ArrayAdapter<IncidenciaUser> getAdapter(Activity activity);

    List<IncidenciaUser> getListFromService(long comunidadId) throws UiException;

    long getComunidadSelected();
}
