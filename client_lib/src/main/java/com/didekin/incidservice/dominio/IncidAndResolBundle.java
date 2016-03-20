package com.didekin.incidservice.dominio;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:34
 */
public final class IncidAndResolBundle {

    final IncidImportancia incidImportancia;
    final boolean hasResolucion;

    public IncidAndResolBundle(IncidImportancia incidImportancia, boolean hasResolucion)
    {
        this.incidImportancia = incidImportancia;
        this.hasResolucion = hasResolucion;
    }

    public IncidImportancia getIncidImportancia()
    {
        return incidImportancia;
    }

    public boolean hasResolucion()
    {
        return hasResolucion;
    }
}
