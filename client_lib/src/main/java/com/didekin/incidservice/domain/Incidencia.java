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
    private final TipoIncidencia tipoIncid;
    private final Timestamp fechaAlta;
    private final Timestamp fechaCierre;
    private final ResolucionIncid resolucionIncid;

    private Incidencia(IncidenciaBuilder incidenciaBuilder)
    {
        incidenciaId = incidenciaBuilder.incidenciaId;
        descripcion = incidenciaBuilder.descripcion;
        tipoIncid = incidenciaBuilder.tipoIncid;
        fechaAlta = incidenciaBuilder.fechaAlta;
        fechaCierre = incidenciaBuilder.fechaCierre;
        resolucionIncid = incidenciaBuilder.resolucionIncid;
    }

    public final static class IncidenciaBuilder implements BeanBuilder<Incidencia> {

        private long incidenciaId;
        private String descripcion;
        private TipoIncidencia tipoIncid;
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

        public IncidenciaBuilder tipoIncid(TipoIncidencia initValue)
        {
            tipoIncid = initValue;
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
