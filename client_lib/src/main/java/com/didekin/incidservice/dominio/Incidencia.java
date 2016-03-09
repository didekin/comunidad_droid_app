package com.didekin.incidservice.dominio;

import com.didekin.common.BeanBuilder;
import com.didekin.common.dominio.SerialNumber;
import com.didekin.usuario.dominio.Comunidad;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:07
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class Incidencia implements Serializable {

    private final long incidenciaId;
    private final Comunidad comunidad;
    private final String userName;
    private final String descripcion;
    private final AmbitoIncidencia ambitoIncidencia;
    private final Timestamp fechaAlta;
    private final Timestamp fechaCierre;
    private final float importanciaAvg;

    protected Incidencia(IncidenciaBuilder incidenciaBuilder)
    {
        incidenciaId = incidenciaBuilder.incidenciaId;
        comunidad = incidenciaBuilder.comunidad;
        userName = incidenciaBuilder.userName;
        descripcion = incidenciaBuilder.descripcion;
        ambitoIncidencia = incidenciaBuilder.ambitoIncidencia;
        fechaAlta = incidenciaBuilder.fechaAlta;
        fechaCierre = incidenciaBuilder.fechaCierre;
        importanciaAvg = incidenciaBuilder.importanciaAvg;
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

    public String getUserName()
    {
        return userName;
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

    // .................................... Serializable ...........................

    /**
     * Return an InnerSerial object that will replace the current IncidenciaIntent object during serialization.
     * In the deserialization the readResolve() method of the InnerSerial object will be used.
     */
    private Object writeReplace()
    {
        return new InnerSerial(this);
    }

    private void readObject(ObjectInputStream inputStream) throws InvalidObjectException
    {
        throw new InvalidObjectException("Use innerSerial to serialize");
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
        private String userName;
        private String descripcion;
        private AmbitoIncidencia ambitoIncidencia;
        private Timestamp fechaAlta;
        private Timestamp fechaCierre;
        private float importanciaAvg;

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

        public IncidenciaBuilder userName(String userName)
        {
            this.userName = userName;
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

        public IncidenciaBuilder copyIncidencia(Incidencia incidencia)
        {
            incidenciaId(incidencia.incidenciaId);
            comunidad(incidencia.comunidad);
            descripcion(incidencia.descripcion);
            userName(incidencia.userName);
            ambitoIncid(incidencia.ambitoIncidencia);
            fechaAlta(incidencia.fechaAlta);
            fechaCierre(incidencia.fechaCierre);
            importanciaAvg(incidencia.importanciaAvg);
            return this;
        }

        @Override
        public Incidencia build()
        {
            Incidencia incidencia = new Incidencia(this);
            if (incidencia.getIncidenciaId() <= 0) {
                if (incidencia.getComunidad() == null || incidencia.getDescripcion() == null || incidencia.getAmbitoIncidencia() == null) {
                    throw new IllegalStateException(INCIDENCIA_WRONG_INIT.toString());
                }
            }
            return incidencia;
            // TODO: meter validaciones de los campos en este método.
        }
    }

    /**
     * Example of serialization proxy.
     */
    private static class InnerSerial implements Serializable {

        private static final long serialVersionUID = SerialNumber.INCIDENCIA.number;

        private final long incidenciaId;
        private final Comunidad comunidad;
        private final String descripcion;
        private final String userName;
        private final AmbitoIncidencia ambitoIncidencia;
        private final Timestamp fechaAlta;
        private final Timestamp fechaCierre;
        private final float importanciaAvg;

        public InnerSerial(Incidencia incidencia)
        {
            incidenciaId = incidencia.incidenciaId;
            comunidad = incidencia.comunidad;
            descripcion = incidencia.descripcion;
            userName = incidencia.userName;
            ambitoIncidencia = incidencia.ambitoIncidencia;
            fechaAlta = incidencia.fechaAlta;
            fechaCierre = incidencia.fechaCierre;
            importanciaAvg = incidencia.importanciaAvg;
        }

        /**
         * Returns a logically equivalent InnerSerial instance of the enclosing class instance,
         * that will replace it during deserialization.
         */
        private Object readResolve()
        {
            return new IncidenciaBuilder()
                    .incidenciaId(incidenciaId)
                    .comunidad(comunidad)
                    .descripcion(descripcion)
                    .userName(userName)
                    .ambitoIncid(ambitoIncidencia)
                    .fechaAlta(fechaAlta)
                    .fechaCierre(fechaCierre)
                    .importanciaAvg(importanciaAvg)
                    .build();
        }
    }
}
