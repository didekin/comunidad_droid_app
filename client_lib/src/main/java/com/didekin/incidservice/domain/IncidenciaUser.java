package com.didekin.incidservice.domain;

import com.didekin.common.BeanBuilder;
import com.didekin.serviceone.domain.Usuario;

import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:08
 */
public class IncidenciaUser {

    private final Incidencia incidencia;
    private final Usuario usuario;
    private final short importancia;
    private final Timestamp fechaAlta;

    public IncidenciaUser(IncidenciaUserBuilder builder) throws IllegalStateException
    {
        incidencia = builder.incidencia;
        usuario = builder.usuario;
        importancia = builder.importancia;
        fechaAlta = builder.fechaAlta;
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

        public IncidenciaUserBuilder(Incidencia incidencia)
        {
            this.incidencia = incidencia;
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

        public IncidenciaUserBuilder usuario(Usuario initValue){
            usuario = initValue;
            return this;
        }

        @Override
        public IncidenciaUser build()
        {
            IncidenciaUser incidenciaUser = new IncidenciaUser(this);
            if (incidenciaUser.incidencia == null){
                throw new IllegalStateException(INCIDENCIA_WRONG_INIT.toString());
            }
            return new IncidenciaUser(this);
        }
    }
}
