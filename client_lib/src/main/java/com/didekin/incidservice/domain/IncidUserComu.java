package com.didekin.incidservice.domain;

import com.didekin.serviceone.domain.UsuarioComunidad;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:08
 */
public class IncidUserComu {

    private final Incidencia incidencia;
    private final UsuarioComunidad usuarioComunidad;
    private final short importancia;
    private final Timestamp fechaAlta;

    public IncidUserComu(Incidencia incidencia, UsuarioComunidad usuarioComunidad, short importancia, Timestamp fechaAlta)
    {
        this.incidencia = incidencia;
        this.usuarioComunidad = usuarioComunidad;
        this.importancia = importancia;
        this.fechaAlta = fechaAlta;
    }

    public Incidencia getIncidencia()
    {
        return incidencia;
    }

    public UsuarioComunidad getUsuarioComunidad()
    {
        return usuarioComunidad;
    }

    public short getImportancia()
    {
        return importancia;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }
}
