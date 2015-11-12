package com.didekin.serviceone.domain;


import com.didekin.common.BeanBuilder;
import com.didekin.common.exception.DidekinExceptionMsg;

import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_HASHABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_WRONG_INIT;


/**
 * User: pedro
 * Date: 29/03/15
 * Time: 12:02
 */
public final class Comunidad implements Comparable<Comunidad> {

    private final long c_Id;
    private final String tipoVia;     // not null.
    private final String nombreVia;   // not null.
    private final short numero;
    private final String sufijoNumero;
    private final Municipio municipio;  // not null.
    private final Timestamp fechaAlta;
    private final Timestamp fechaMod;

    private Comunidad(ComunidadBuilder builder)
    {
        c_Id = builder.c_Id;
        tipoVia = builder.tipoVia;
        nombreVia = builder.nombreVia;
        numero = builder.numero;
        sufijoNumero = builder.sufijoNumero;
        municipio = builder.municipio;
        fechaAlta = builder.fechaAlta;
        fechaMod = builder.fechaMod;
    }


    public long getC_Id()
    {
        return c_Id;
    }


    public String getNombreComunidad()
    {
        StringBuilder nombreBuilder = new StringBuilder();

        if (tipoVia != null && tipoVia.trim().length() > 0) {
            nombreBuilder.append(tipoVia).append(" ");
        }
        if (nombreVia != null && nombreVia.trim().length() > 0) {
            nombreBuilder.append(nombreVia).append(" ");
        }
        nombreBuilder.append(numero);
        if (sufijoNumero != null && !sufijoNumero.trim().isEmpty()) {
            nombreBuilder.append(" ").append(sufijoNumero.trim());
        }
        return nombreBuilder.toString();
    }


    public String getNombreVia()
    {
        return nombreVia;
    }


    public String getSufijoNumero()
    {
        return sufijoNumero;
    }


    @SuppressWarnings("unused")
    public Timestamp getFechaAlta()
    {
        return new Timestamp(fechaAlta.getTime());
    }

    @SuppressWarnings("unused")
    public Timestamp getFechaMod()
    {
        return new Timestamp(fechaMod.getTime());
    }


    public Municipio getMunicipio()
    {
        return municipio;
    }


    public short getNumero()
    {
        return numero;
    }


    public String getTipoVia()
    {
        return tipoVia;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Comunidad comunidad = (Comunidad) o;

        // Comunidad is initialized with a PK.
        if (c_Id > 0 && comunidad.getC_Id() > 0) {
            return c_Id == comunidad.getC_Id();
        }

        return tipoVia.equals(comunidad.tipoVia)
                && nombreVia.equals(comunidad.nombreVia)
                && numero == comunidad.numero
                && sufijoNumero.equals(comunidad.sufijoNumero)
                && municipio.equals(comunidad.municipio);
    }

    @Override
    public int hashCode()
    {
        if (tipoVia == null || nombreVia == null || municipio == null) {
            throw new UnsupportedOperationException(COMUNIDAD_NOT_HASHABLE.toString());
        }

        int result = tipoVia.hashCode();
        result = 31 * result + nombreVia.hashCode();
        result = 31 * result + numero;
        result = 31 * result + sufijoNumero.hashCode();
        result = 31 * result + municipio.hashCode();
        return result;
    }

    @Override
    public int compareTo(Comunidad o)
    {
        if (municipio == null || nombreVia == null || tipoVia == null) {
            throw new UnsupportedOperationException(COMUNIDAD_NOT_COMPARABLE.toString());
        }

        int result;

        if ((result = municipio.compareTo(o.getMunicipio())) != 0) {
            return result;
        }
        if ((result = nombreVia.compareToIgnoreCase(o.getNombreVia())) != 0) {
            return result;
        }
        if ((result = tipoVia.compareToIgnoreCase(o.getTipoVia())) != 0) {
            return result;
        }
        if (numero < o.getNumero()) {
            return -1;
        }
        if (numero > o.getNumero()) {
            return 1;
        }
        return sufijoNumero.compareToIgnoreCase(o.getSufijoNumero());
    }

    public static class ComunidadBuilder implements BeanBuilder<Comunidad> {

        private long c_Id = 0L;
        private String tipoVia = null;
        private String nombreVia = null;
        private short numero = (short) 0;
        private String sufijoNumero = "";
        private Municipio municipio = null;
        private Timestamp fechaAlta = null;
        private Timestamp fechaMod = null;

        public ComunidadBuilder()
        {
        }

        public ComunidadBuilder c_id(long initValue)
        {
            c_Id = initValue;
            return this;
        }

        public ComunidadBuilder tipoVia(String initValue)
        {
            tipoVia = initValue;
            return this;
        }

        public ComunidadBuilder nombreVia(String initValue)
        {
            nombreVia = initValue;
            return this;
        }

        public ComunidadBuilder numero(short initValue)
        {
            numero = initValue;
            return this;
        }

        public ComunidadBuilder sufijoNumero(String initValue)
        {
            sufijoNumero = initValue;
            return this;
        }

        public ComunidadBuilder municipio(Municipio initValue)
        {
            municipio = initValue;
            return this;
        }

        @SuppressWarnings("unused")
        public ComunidadBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = new Timestamp(initValue.getTime());
            return this;
        }

        @SuppressWarnings("unused")
        public ComunidadBuilder fechaModificacion(Timestamp initValue)
        {
            fechaMod = new Timestamp(initValue.getTime());
            return this;
        }

        @Override
        public Comunidad build()
        {
            Comunidad comunidad = new Comunidad(this);

            if (comunidad.c_Id <= 0 && (comunidad.tipoVia == null || comunidad.nombreVia == null || comunidad
                    .municipio == null)) {
                throw new IllegalStateException(COMUNIDAD_WRONG_INIT.toString());
            }
            if (comunidad.sufijoNumero == null) {
                throw new IllegalStateException(DidekinExceptionMsg.SUFIJO_NUM_IN_COMUNIDAD_NULL.toString());
            }
            return comunidad;
        }
    }
}
