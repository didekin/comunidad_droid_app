package com.didekin.incidservice.domain;

import com.didekin.common.BeanBuilder;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 18:30
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ResolucionIncid {

    private final String descripcion;
    private final String faseResolucion;
    private final int costeEstimado;
    private final int costeFinal;
    private final Proveedor proveedor;
    private final Timestamp fechaPlanificada;
    private final Timestamp fechaResolucion;

    // TODO. añadir un campo recomendación para otras comunidades o ella misma, en casos futuros similares. Puede ser un texto.

    private ResolucionIncid(ResolucionBuilder builder)
    {
        descripcion = builder.descripcion;
        faseResolucion = builder.faseResolucion;
        costeEstimado = builder.costeEstimado;
        costeFinal = builder.costeReal;
        proveedor = builder.proveedor;
        fechaPlanificada = builder.fechaPlanificada;
        fechaResolucion = builder.fechaResolucion;
    }

    public static final class ResolucionBuilder implements BeanBuilder<ResolucionIncid> {

        private String descripcion;
        private String faseResolucion;
        private int costeEstimado;
        private int costeReal;
        private Proveedor proveedor;
        private Timestamp fechaPlanificada;
        private Timestamp fechaResolucion;

        public ResolucionBuilder()
        {
        }

        public ResolucionBuilder descripcion(String initValue)
        {
            descripcion = initValue;
            return this;
        }

        public ResolucionBuilder faseResolucion(String initValue)
        {
            faseResolucion = initValue;
            return this;
        }

        public ResolucionBuilder costeEstimado(int initValue)
        {
            costeEstimado = initValue;
            return this;
        }

        public ResolucionBuilder costeReal(int initValue){
            costeReal = initValue;
            return this;
        }

        public ResolucionBuilder proveedor(Proveedor initValue)
        {
            proveedor = initValue;
            return this;
        }

        public ResolucionBuilder fechaPlanificada(Timestamp initValue)
        {
            fechaPlanificada = initValue;
            return this;
        }

        public ResolucionBuilder fechaResolucion(Timestamp initValue)
        {
            fechaResolucion = initValue;
            return this;
        }

        @Override
        public ResolucionIncid build()
        {
            return new ResolucionIncid(this);
        }
    }
}
