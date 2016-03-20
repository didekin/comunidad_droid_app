package com.didekindroid.incidencia.activity;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Resolucion;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 15:25
 */
interface IncidenciaDataSupplier {
    IncidImportancia getIncidImportancia();
    Resolucion getResolucion();
    boolean getFlagResolucion();

}
