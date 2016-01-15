package com.didekin.incidservice.domain;

import com.didekin.common.BeanBuilder;
import com.didekin.serviceone.domain.Comunidad;

import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:07
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class Incidencia {

    private final long incidenciaId;
    private final Comunidad comunidad;
    private final String descripcion;
    private final AmbitoIncidencia ambitoIncidencia;
    private final Timestamp fechaAlta;
    private final Timestamp fechaCierre;
    private final float importanciaAvg;
    private final ResolucionIncid resolucionIncid;

    protected Incidencia(IncidenciaBuilder incidenciaBuilder)
    {
        incidenciaId = incidenciaBuilder.incidenciaId;
        comunidad = incidenciaBuilder.comunidad;
        descripcion = incidenciaBuilder.descripcion;
        ambitoIncidencia = incidenciaBuilder.ambitoIncidencia;
        fechaAlta = incidenciaBuilder.fechaAlta;
        fechaCierre = incidenciaBuilder.fechaCierre;
        importanciaAvg = incidenciaBuilder.importanciaAvg;
        resolucionIncid = incidenciaBuilder.resolucionIncid;
    }

    public long getIncidenciaId()
    {
        return incidenciaId;
    }

    public Comunidad getComunidad()
    {
        return comunidad;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public AmbitoIncidencia getAmbitoIncidencia()
    {
        return ambitoIncidencia;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    public Timestamp getFechaCierre()
    {
        return fechaCierre;
    }

    public float getImportanciaAvg()
    {
        return importanciaAvg;
    }

    public ResolucionIncid getResolucionIncid()
    {
        return resolucionIncid;
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
        private Comunidad comunidad;
        private String descripcion;
        private AmbitoIncidencia ambitoIncidencia;
        private Timestamp fechaAlta;
        private Timestamp fechaCierre;
        private float importanciaAvg;
        private ResolucionIncid resolucionIncid;

        public IncidenciaBuilder()
        {
        }

        public IncidenciaBuilder incidenciaId(long initValue)
        {
            incidenciaId = initValue;
            return this;
        }

        public IncidenciaBuilder comunidad(Comunidad initValue)
        {
            comunidad = initValue;
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

        public IncidenciaBuilder fechaCierre(Timestamp initValue)
        {
            fechaCierre = initValue;
            return this;
        }

        public IncidenciaBuilder importanciaAvg(float initValue)
        {
            importanciaAvg = initValue;
            return this;
        }

        public IncidenciaBuilder resolucion(ResolucionIncid initValue)
        {
            resolucionIncid = initValue;
            return this;
        }

        public IncidenciaBuilder copyIncidencia(Incidencia incidencia)
        {
            incidenciaId(incidencia.getIncidenciaId());
            comunidad(incidencia.getComunidad());
            descripcion(incidencia.getDescripcion());
            ambitoIncid(incidencia.getAmbitoIncidencia());
            fechaAlta(incidencia.getFechaAlta());
            fechaCierre(incidencia.getFechaCierre());
            importanciaAvg(incidencia.getImportanciaAvg());
            resolucion(incidencia.getResolucionIncid());
            return this;
        }

        @Override
        public Incidencia build()
        {
            Incidencia incidencia = new Incidencia(this);
            if (incidencia.getComunidad() == null ||
                    (incidencia.getIncidenciaId() <= 0 &&
                            (incidencia.getDescripcion() == null || incidencia.getAmbitoIncidencia() == null))) {
                throw new IllegalStateException(INCIDENCIA_WRONG_INIT.toString());
            }
            return incidencia;
        }
    }
}
