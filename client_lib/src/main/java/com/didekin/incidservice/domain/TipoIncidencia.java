package com.didekin.incidservice.domain;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 18:20
 */
public class TipoIncidencia {

    private final int incidTipoId;
    private final String descTipo;

    public TipoIncidencia(int incidTipoId, String descTipo)
    {
        this.incidTipoId = incidTipoId;
        this.descTipo = descTipo;
    }

    public int getIncidTipoId()
    {
        return incidTipoId;
    }

    public String getDescTipo()
    {
        return descTipo;
    }
}
