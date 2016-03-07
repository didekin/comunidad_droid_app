package com.didekin.incidservice.dominio;

import com.didekin.common.BeanBuilder;
import com.didekin.common.exception.DidekinExceptionMsg;
import com.didekin.usuario.dominio.UsuarioComunidad;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 19/02/16
 * Time: 19:01
 */

/**
 * Holder object for an incidencia and the importancia assigned by an user.
 * Integrity constraint: incidencia.comunidad == userComu.comunidad
 */
@SuppressWarnings("unused")
public class IncidImportancia implements Serializable {

    private final Incidencia incidencia;
    private final UsuarioComunidad userComu;
    private final short importancia;
    private final Timestamp fechaAlta;

    public IncidImportancia(IncidImportanciaBuilder builder)
    {
        incidencia = builder.incidencia;
        userComu = builder.userComu;
        importancia = builder.importancia;
        fechaAlta = builder.fechaAlta;
    }

    public Incidencia getIncidencia()
    {
        return incidencia;
    }

    public UsuarioComunidad getUserComu()
    {
        return userComu;
    }

    /**
     * A flag to signal that this.usuarioComunidad corresponds to the user who initiated the incidencia.
     */
    public boolean isIniciadorIncidencia()
    {
        return incidencia.getUserName().equals(userComu.getUsuario().getUserName());
    }

    public short getImportancia()
    {
        return importancia;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof IncidImportancia)) return false;

        IncidImportancia that = (IncidImportancia) o;

        return incidencia.equals(that.incidencia)
                && !(userComu != null ? !userComu.equals(that.userComu) : that.userComu != null);
    }

    @Override
    public int hashCode()
    {
        int result = incidencia.hashCode();
        result = 31 * result + (userComu != null ? userComu.hashCode() : 0);
        return result;
    }

    //    ============================== BUILDER ==================================

    public final static class IncidImportanciaBuilder implements BeanBuilder<IncidImportancia> {

        private Incidencia incidencia;
        private UsuarioComunidad userComu;
        private short importancia;
        private Timestamp fechaAlta;

        public IncidImportanciaBuilder(Incidencia incidencia)
        {
            this.incidencia = incidencia;
        }

        public IncidImportanciaBuilder usuarioComunidad(UsuarioComunidad initValue)
        {
            userComu = initValue;
            return this;
        }

        public IncidImportanciaBuilder importancia(short initValue)
        {
            importancia = initValue;
            return this;
        }

        public IncidImportanciaBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public IncidImportanciaBuilder copyIncidImportancia(IncidImportancia initValue)
        {
            userComu = initValue.userComu;
            importancia = initValue.importancia;
            fechaAlta = initValue.fechaAlta;
            return this;
        }

        @Override
        public IncidImportancia build()
        {
            IncidImportancia incidImportancia = new IncidImportancia(this);
            if (incidImportancia.incidencia == null) {
                throw new IllegalStateException(DidekinExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT.toString());
            }
            if (incidImportancia.userComu != null
                    && (! incidImportancia.userComu.getComunidad().equals(incidImportancia.incidencia.getComunidad()))){
                throw new IllegalStateException(DidekinExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT.toString());
            }
            return incidImportancia;
        }
    }

    //    ============================== SERIALIZATION PROXY ==================================

    /**
     * Return an InnerSerial object that will replace the current IncidImportancia instance during serialization.
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

    private static class InnerSerial implements Serializable{

        private final Incidencia incidencia;
        private final UsuarioComunidad userComu;
        private final short importancia;
        private final Timestamp fechaAlta;

        public InnerSerial(IncidImportancia incidImportancia)
        {
            incidencia = incidImportancia.incidencia;
            userComu = incidImportancia.getUserComu();
            importancia = incidImportancia.importancia;
            fechaAlta = incidImportancia.fechaAlta;
        }

        /**
         * Returns a logically equivalent InnerSerial instance of the enclosing class instance,
         * that will replace it during deserialization.
         */
        private Object readResolve()
        {
           return new IncidImportanciaBuilder(incidencia)
                   .usuarioComunidad(userComu)
                   .importancia(importancia)
                   .fechaAlta(fechaAlta)
                   .build();
        }
    }
}
