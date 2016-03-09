package com.didekin.incidservice.dominio;

import com.didekin.common.BeanBuilder;
import com.didekin.common.dominio.SerialNumber;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 18:30
 */

@SuppressWarnings("unused")
public final class Resolucion implements Serializable {

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
                    || resolucion.descripcion == null || resolucion.fechaPrev == null) {
                throw new IllegalStateException(RESOLUCION_WRONG_INIT.toString());
            }
            return resolucion;
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

    private static class InnerSerial implements Serializable {

        private static final long serialVersionUID = SerialNumber.INCID_RESOLUCION.number;

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

        public InnerSerial(Resolucion resolucion)
        {
            userName = resolucion.userName;
            descripcion = resolucion.descripcion;
            costeEstimado = resolucion.costeEstimado;
            costeFinal = resolucion.costeFinal;
            moraleja = resolucion.moraleja;
            fechaPrev = resolucion.fechaPrev;
            fechaAlta = resolucion.fechaAlta;
            fechaReal = resolucion.fechaReal;
            incidencia = resolucion.incidencia;
        }

        /**
         * Returns a logically equivalent InnerSerial instance of the enclosing class instance,
         * that will replace it during deserialization.
         */
        private Object readResolve()
        {
            return new ResolucionBuilder(incidencia)
                    .userName(userName)
                    .descripcion(descripcion)
                    .costeEstimado(costeEstimado)
                    .costeReal(costeFinal)
                    .moraleja(moraleja)
                    .fechaPrevista(fechaPrev)
                    .fechaAlta(fechaAlta)
                    .fechaReal(fechaReal)
                    .build();
        }
    }
}
