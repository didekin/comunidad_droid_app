package com.didekin.incidservice.domain;

import com.didekin.common.BeanBuilder;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:07
 */
@SuppressWarnings("FieldCanBeLocal")
public final class Incidencia {

    private final long incidenciaId;
    private final String descripcion;
    private final AmbitoIncidencia ambitoIncidencia;
    private final Timestamp fechaAlta;
    private final Timestamp fechaCierre;
    private final ResolucionIncid resolucionIncid;

    private Incidencia(IncidenciaBuilder incidenciaBuilder)
    {
        incidenciaId = incidenciaBuilder.incidenciaId;
        descripcion = incidenciaBuilder.descripcion;
        ambitoIncidencia = incidenciaBuilder.ambitoIncidencia;
        fechaAlta = incidenciaBuilder.fechaAlta;
        fechaCierre = incidenciaBuilder.fechaCierre;
        resolucionIncid = incidenciaBuilder.resolucionIncid;
    }

    public long getIncidenciaId()
    {
        return incidenciaId;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public AmbitoIncidencia getAmbitoIncidencia()
    {
        return ambitoIncidencia;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Incidencia that = (Incidencia) o;

        return incidenciaId == that.incidenciaId;

    }

    @Override
    public int hashCode()
    {
        return (int) (incidenciaId ^ (incidenciaId >>> 32));
    }

//    ==================== BUILDER ====================

    public final static class IncidenciaBuilder implements BeanBuilder<Incidencia> {

        private long incidenciaId;
        private String descripcion;
        private AmbitoIncidencia ambitoIncidencia;
        private Timestamp fechaAlta;
        private Timestamp fechaCierre;
        private ResolucionIncid resolucionIncid;

        public IncidenciaBuilder()
        {
        }

        public IncidenciaBuilder incidenciaId(long initValue)
        {
            incidenciaId = initValue;
            return this;
        }

        public IncidenciaBuilder descripcion(String initValue)
        {
            descripcion = initValue;
            return this;
        }

        public IncidenciaBuilder ambitoIncid(AmbitoIncidencia initValue)
        {
            ambitoIncidencia = initValue;
            return this;
        }

        public IncidenciaBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public IncidenciaBuilder fechaCierre(Timestamp initValue){
            fechaCierre = initValue;
            return this;
        }

        public IncidenciaBuilder resolucion(ResolucionIncid initValue)
        {
            resolucionIncid = initValue;
            return this;
        }

        @Override
        public Incidencia build()
        {
            return new Incidencia(this);
        }
    }
}
