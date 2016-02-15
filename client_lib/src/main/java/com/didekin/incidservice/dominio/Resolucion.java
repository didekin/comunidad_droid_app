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
public final class Resolucion {

    private final long resolucionId;
    private final String descripcion;
    private final int costeEstimado;
    private final int costeFinal;
    private final String moraleja;
    //    private final Proveedor proveedor;
    private final Timestamp fechaPrevista;
    private final Timestamp fechaResolucion;
    private final Incidencia incidencia;

    private Resolucion(ResolucionBuilder builder)
    {
        resolucionId = builder.resolucionId;
        descripcion = builder.descripcion;
        costeEstimado = builder.costeEstimado;
        costeFinal = builder.costeFinal;
        moraleja = builder.moraleja;
//        proveedor = builder.proveedor;
        fechaPrevista = builder.fechaPrevista;
        fechaResolucion = builder.fechaResolucion;
        incidencia = builder.incidencia;
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

    public Timestamp getFechaPrevista()
    {
        return fechaPrevista;
    }

    public Timestamp getFechaResolucion()
    {
        return fechaResolucion;
    }

    //    ===============================  BUILDER  ============================

    public static final class ResolucionBuilder implements BeanBuilder<Resolucion> {

        public long resolucionId;
        private String descripcion;
        private int costeEstimado;
        private int costeFinal;
        //        private Proveedor proveedor;
        private Timestamp fechaPrevista;
        private Timestamp fechaResolucion;
        public String moraleja;
        public Incidencia incidencia;

        public ResolucionBuilder(Incidencia incidencia)
        {
            this.incidencia = incidencia;
        }

        public ResolucionBuilder resolucionId(long initValue){
            resolucionId = initValue;
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
            fechaPrevista = initValue;
            return this;
        }

        public ResolucionBuilder fechaResolucion(Timestamp initValue)
        {
            fechaResolucion = initValue;
            return this;
        }

        @Override
        public Resolucion build()
        {
            Resolucion resolucion = new Resolucion(this);
            if (resolucion.resolucionId <= 0 && (resolucion.descripcion == null || resolucion.fechaPrevista == null)){
                throw new IllegalStateException(RESOLUCION_WRONG_INIT.toString());
            }
            return resolucion;
        }
    }
}
