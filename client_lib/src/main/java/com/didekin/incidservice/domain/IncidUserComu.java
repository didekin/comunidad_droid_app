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
    private final float importanciaAvgInComu;
    private final Timestamp fechaAlta;

    public IncidUserComu(Incidencia incidencia, UsuarioComunidad usuarioComunidad, short importancia, float importanciaAvgInComu, Timestamp fechaAlta)
    {
        this.incidencia = incidencia;
        this.usuarioComunidad = usuarioComunidad;
        this.importancia = importancia;
        this.importanciaAvgInComu = importanciaAvgInComu;
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

    public float getImportanciaAvgInComu() {
        return importanciaAvgInComu;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }
}
