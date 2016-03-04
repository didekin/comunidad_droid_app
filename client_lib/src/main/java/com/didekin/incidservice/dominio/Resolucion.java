package com.didekin.incidservice.dominio;

import com.didekin.common.BeanBuilder;

import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 18:30
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class Resolucion {   // TODO: serial.

    private final String userName;
    private final String descripcion;
    private final int costeEstimado;
    private final int costeFinal;
    private final String moraleja;
    //    private final Proveedor proveedor;
    private final Timestamp fechaPrev;
    private final Timestamp fechaReal;
    private final Timestamp fechaAlta;
    private final Incidencia incidencia;

    private Resolucion(ResolucionBuilder builder)
    {
        userName = builder.userName;
        descripcion = builder.descripcion;
        costeEstimado = builder.costeEstimado;
        costeFinal = builder.costeFinal;
        moraleja = builder.moraleja;
//        proveedor = builder.proveedor;
        fechaPrev = builder.fechaPrev;
        fechaReal = builder.fechaReal;
        fechaAlta = builder.fechaAlta;
        incidencia = builder.incidencia;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public int getCosteEstimado()
    {
        return costeEstimado;
    }

    public int getCosteFinal()
    {
        return costeFinal;
    }

    public String getMoraleja()
    {
        return moraleja;
    }

    public Timestamp getFechaPrev()
    {
        return fechaPrev;
    }

    public Timestamp getFechaReal()
    {
        return fechaReal;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    public Incidencia getIncidencia()
    {
        return incidencia;
    }

    //    ===============================  BUILDER  ============================

    public static final class ResolucionBuilder implements BeanBuilder<Resolucion> {

        private String userName;
        private String descripcion;
        private int costeEstimado;
        private int costeFinal;
        //        private Proveedor proveedor;
        private Timestamp fechaPrev;
        private Timestamp fechaReal;
        public String moraleja;
        public Incidencia incidencia;
        public Timestamp fechaAlta;

        public ResolucionBuilder(Incidencia incidencia)
        {
            this.incidencia = incidencia;
        }

        public ResolucionBuilder userName(String initValue)
        {
            userName = initValue;
            return this;
        }

        public ResolucionBuilder descripcion(String initValue)
        {
            descripcion = initValue;
            return this;
        }

        public ResolucionBuilder costeEstimado(int initValue)
        {
            costeEstimado = initValue;
            return this;
        }

        public ResolucionBuilder costeReal(int initValue)
        {
            costeFinal = initValue;
            return this;
        }

        public ResolucionBuilder moraleja(String initValue)
        {
            moraleja = initValue;
            return this;
        }

        /*public ResolucionBuilder proveedor(Proveedor initValue)
        {
            proveedor = initValue;
            return this;
        }*/

        public ResolucionBuilder fechaPrevista(Timestamp initValue)
        {
            fechaPrev = initValue;
            return this;
        }

        public ResolucionBuilder fechaReal(Timestamp initValue)
        {
            fechaReal = initValue;
            return this;
        }

        public ResolucionBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public ResolucionBuilder copyResolucion(Resolucion initValue)
        {
            userName = initValue.userName;
            descripcion = initValue.descripcion;
            costeEstimado = initValue.costeEstimado;
            costeFinal = initValue.costeFinal;
            fechaPrev = initValue.fechaPrev;
            fechaReal = initValue.fechaReal;
            moraleja = initValue.moraleja;
            fechaAlta = initValue.fechaAlta;
            return this;
        }

        @Override
        public Resolucion build()
        {
            Resolucion resolucion = new Resolucion(this);
            if (resolucion.incidencia == null || resolucion.incidencia.getIncidenciaId() <= 0
                    || resolucion.descripcion == null || resolucion.fechaPrev == null){
                throw new IllegalStateException(RESOLUCION_WRONG_INIT.toString());
            }
            return resolucion;
        }
    }
}
