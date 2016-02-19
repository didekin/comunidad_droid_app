package com.didekindroid.incidencia.activity;

import android.app.Activity;

import com.didekin.incidservice.dominio.IncidenciaUser;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 15:25
 */
interface IncidUserDataSupplier {
    IncidenciaUser getIncidenciaUser();
}
