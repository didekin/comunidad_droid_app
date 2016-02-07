package com.didekindroid.incidencia.activity;

import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.serviceone.domain.Comunidad;

/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 12:16
 */
public interface IncidListListener {
    void onIncidenciaSelected(Incidencia incidencia, int position);
    void onComunidadSpinnerSelected(Comunidad comunidadSelected);
}
