package com.didekin.incidservice.dominio;

import com.didekin.common.BeanBuilder;
import com.didekin.usuario.dominio.UsuarioComunidad;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_USER_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:08
 */
@SuppressWarnings("unused")
public class IncidenciaUser implements Serializable{

    private final Incidencia incidencia;
    private final UsuarioComunidad usuario;
    private final short importancia;

    /**
     * A flag to signal that ...
     * 1. The incidencia has no IncidenciaUser instances with importancia > 1 associated,
     *    with the possible exception of that one corresponding to the primal incidenciaUser (iniciador).
     * 2. This usuarioComunidad corresponds to the user who initiated the incidencia.
     *
     * True if both conditions are met.
     */
    private final boolean isYetIniciador;
    private final Timestamp fechaAlta;

    public IncidenciaUser(IncidenciaUserBuilder builder) throws IllegalStateException
    {
        incidencia = builder.incidencia;
        usuario = builder.usuario;
        importancia = builder.importancia;
        isYetIniciador = builder.isYetIniciador;
        fechaAlta = builder.fechaAlta;
    }

    public Incidencia getIncidencia()
    {
        return incidencia;
    }

    public UsuarioComunidad getUsuarioComunidad()
    {
        return usuario;
    }

    public short getImportancia()
    {
        return importancia;
    }

    public boolean isYetIniciador()
    {
        return isYetIniciador;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    // ............................ Serializable ...............................

    /**
     * Return an InnerSerial object that will replace the current IncicenciaUser object during serialization.
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
        if (!(o instanceof IncidenciaUser)) return false;

        IncidenciaUser that = (IncidenciaUser) o;

        return incidencia.equals(that.incidencia) && usuario.equals(that.usuario);
    }

    @Override
    public int hashCode()
    {
        int result = incidencia.hashCode();
        result = 31 * result + usuario.hashCode();
        return result;
    }

    //    ==================== BUILDER ====================

    @SuppressWarnings("unused")
    public final static class IncidenciaUserBuilder implements BeanBuilder<IncidenciaUser> {

        private Incidencia incidencia;
        private UsuarioComunidad usuario;
        private short importancia;
        private boolean isYetIniciador;
        private Timestamp fechaAlta;

        public IncidenciaUserBuilder(Incidencia incidencia)
        {
            this.incidencia = incidencia;
        }

        public IncidenciaUserBuilder(IncidenciaUser incidenciaUser)
        {
            copyIncidUser(incidenciaUser);
        }

        public IncidenciaUserBuilder importancia(short initValue)
        {
            importancia = initValue;
            return this;
        }

        public IncidenciaUserBuilder isYetIniciador(boolean initValue){
            isYetIniciador = initValue;
            return this;
        }

        public IncidenciaUserBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public IncidenciaUserBuilder usuario(UsuarioComunidad initValue)
        {
            usuario = initValue;
            return this;
        }

        public IncidenciaUserBuilder copyIncidUser(IncidenciaUser initValue)
        {
            importancia = initValue.importancia;
            isYetIniciador = initValue.isYetIniciador;
            usuario = initValue.usuario;
            fechaAlta = initValue.fechaAlta;
            return this;
        }

        @Override
        public IncidenciaUser build()
        {
            IncidenciaUser incidenciaUser = new IncidenciaUser(this);
            if (hasConditionOne(incidenciaUser) || hasConditionTwo(incidenciaUser)) {
                throw new IllegalStateException(INCIDENCIA_USER_WRONG_INIT.toString());
            }
            return new IncidenciaUser(this);
        }

        private boolean hasConditionOne(IncidenciaUser incidenciaUser)
        {
            return incidenciaUser.incidencia == null;
        }

        private boolean hasConditionTwo(IncidenciaUser incidenciaUser)
        {
            return incidenciaUser.incidencia != null
                    && usuario != null
                    && (!incidenciaUser.incidencia.getComunidad().equals(usuario.getComunidad()));
        }
    }

    //    ================================== SERIALIZATION PROXY ==================================

    private static class InnerSerial implements Serializable {

        private final Incidencia incidencia;
        private final UsuarioComunidad usuario;
        private final short importancia;
        private final boolean isYetIniciador;
        private final Timestamp fechaAlta;

        public InnerSerial(IncidenciaUser incidenciaUser)
        {
            incidencia = incidenciaUser.incidencia;
            usuario = incidenciaUser.usuario;
            importancia = incidenciaUser.importancia;
            isYetIniciador = incidenciaUser.isYetIniciador;
            fechaAlta = incidenciaUser.fechaAlta;
        }

        private Object readResolve()
        {
            return (new IncidenciaUserBuilder(incidencia)
                    .usuario(usuario)
                    .importancia(importancia)
                    .isYetIniciador(isYetIniciador)
                    .fechaAlta(fechaAlta)
                    .build());
        }
    }
}
