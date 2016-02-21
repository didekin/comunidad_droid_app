package com.didekin.incidservice.dominio;

import com.didekin.common.BeanBuilder;
import com.didekin.common.exception.DidekinExceptionMsg;
import com.didekin.usuario.dominio.UsuarioComunidad;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 19/02/16
 * Time: 19:01
 */
@SuppressWarnings("unused")
public class IncidImportancia {

    private final Incidencia incidencia;
    private final UsuarioComunidad userComu;
    private final short importancia;
    private final Timestamp fechaAlta;

    public IncidImportancia(IncidImportanciaBuilder builder)
    {
        incidencia = builder.incidencia;
        userComu =builder.usuario;
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

    public final static class IncidImportanciaBuilder implements BeanBuilder<IncidImportancia>{

        private Incidencia incidencia;
        private UsuarioComunidad usuario;
        private short importancia;
        private Timestamp fechaAlta;

        public IncidImportanciaBuilder(Incidencia incidencia)
        {
            this.incidencia = incidencia;
        }

        public IncidImportanciaBuilder usuarioComunidad(UsuarioComunidad initValue){
            usuario = initValue;
            return this;
        }

        public IncidImportanciaBuilder importancia(short initValue){
            importancia = initValue;
            return this;
        }

        public IncidImportanciaBuilder fechaAlta(Timestamp initValue){
            fechaAlta = initValue;
            return this;
        }

        @Override
        public IncidImportancia build()
        {
            IncidImportancia incidImportancia = new IncidImportancia(this);
            if (incidImportancia.incidencia == null){
                throw new IllegalStateException(DidekinExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT.toString());
            }
            return incidImportancia;
        }
    }
}
