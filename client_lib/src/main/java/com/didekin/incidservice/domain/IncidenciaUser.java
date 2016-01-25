package com.didekin.incidservice.domain;

import com.didekin.common.BeanBuilder;
import com.didekin.serviceone.domain.Usuario;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;
import static com.didekin.common.oauth2.Rol.hasFunctionAdmonPowers;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:08
 */
@SuppressWarnings("unused")
public class IncidenciaUser implements Serializable{

    private final Incidencia incidencia;
    private final Usuario usuario;
    private final short importancia;
    private final Timestamp fechaAlta;
    private final boolean modifyDescOrEraseIncid;

    public IncidenciaUser(IncidenciaUserBuilder builder) throws IllegalStateException
    {
        incidencia = builder.incidencia;
        usuario = builder.usuario;
        importancia = builder.importancia;
        fechaAlta = builder.fechaAlta;
        modifyDescOrEraseIncid = builder.modifyDescOrEraseIncid;
    }

    public Incidencia getIncidencia()
    {
        return incidencia;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }

    public short getImportancia()
    {
        return importancia;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    public boolean isModifyDescOrEraseIncid()
    {
        return modifyDescOrEraseIncid;
    }

    /**
     * This method returns an IncidenciaUser whose power to modify the description of an incidencia or to erase it depends on:
     * 1. If it has more incidenciaUsers with importancia greater than 1, only functional roles with "admon" authority can modify or erase.
     * 2. If not, the user with the oldest incidenciaUser date and "admon" roles can both modify and erase as well.
     */
    public static IncidenciaUser checkPowers(boolean hasOthersIncid, String functionRol, IncidenciaUser incidenciaUser)
    {
        if (!hasOthersIncid) {
            return new IncidenciaUserBuilder(incidenciaUser.incidencia)
                    .copyIncidUser(incidenciaUser)
                    .modifyDescOrEraseIncid(hasFunctionAdmonPowers(functionRol) || incidenciaUser.isModifyDescOrEraseIncid())
                    .build();
        }
        return new IncidenciaUserBuilder(incidenciaUser.incidencia)
                .copyIncidUser(incidenciaUser)
                .modifyDescOrEraseIncid(hasFunctionAdmonPowers(functionRol))
                .build();
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
        private Usuario usuario;
        private short importancia;
        private Timestamp fechaAlta;
        private boolean modifyDescOrEraseIncid;

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

        public IncidenciaUserBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public IncidenciaUserBuilder modifyDescOrEraseIncid(boolean initValue)
        {
            modifyDescOrEraseIncid = initValue;
            return this;
        }

        public IncidenciaUserBuilder usuario(Usuario initValue)
        {
            usuario = initValue;
            return this;
        }

        public IncidenciaUserBuilder copyIncidUser(IncidenciaUser initValue)
        {
            importancia = initValue.importancia;
            usuario = initValue.usuario;
            fechaAlta = initValue.fechaAlta;
            modifyDescOrEraseIncid = initValue.modifyDescOrEraseIncid;
            return this;
        }

        @Override
        public IncidenciaUser build()
        {
            IncidenciaUser incidenciaUser = new IncidenciaUser(this);
            if (incidenciaUser.incidencia == null) {
                throw new IllegalStateException(INCIDENCIA_WRONG_INIT.toString());
            }
            return new IncidenciaUser(this);
        }
    }

    //    ================================== SERIALIZATION PROXY ==================================

    private static class InnerSerial implements Serializable {

        private final Incidencia incidencia;
        private final Usuario usuario;
        private final short importancia;
        private final Timestamp fechaAlta;
        private boolean modifyDescOrEraseIncid;

        public InnerSerial(IncidenciaUser incidenciaUser)
        {
            incidencia = incidenciaUser.incidencia;
            usuario = incidenciaUser.usuario;
            importancia = incidenciaUser.importancia;
            fechaAlta = incidenciaUser.fechaAlta;
            modifyDescOrEraseIncid = incidenciaUser.modifyDescOrEraseIncid;
        }

        private Object readResolve()
        {
            return (new IncidenciaUserBuilder(incidencia)
                    .usuario(usuario)
                    .importancia(importancia)
                    .fechaAlta(fechaAlta)
                    .modifyDescOrEraseIncid(modifyDescOrEraseIncid)
                    .build());
        }
    }
}
