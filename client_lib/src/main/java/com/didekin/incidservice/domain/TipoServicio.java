package com.didekin.incidservice.domain;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 18:09
 */
public class TipoServicio {

    private final int servicioTipoId;
    private final String descServicio;

    public TipoServicio(int servicioTipoId, String descServicio)
    {
        this.servicioTipoId = servicioTipoId;
        this.descServicio = descServicio;
    }

    public int getServicioTipoId()
    {
        return servicioTipoId;
    }

    public String getDescServicio()
    {
        return descServicio;
    }
}
