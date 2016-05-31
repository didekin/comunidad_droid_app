package com.didekin.usuario.dominio;

import com.didekin.usuario.dominio.Municipio;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 19:55
 */
public final class Address {  // TODO: borrar o mover al módulo de proveedor.

    private final String tipoVia;
    private final String nombreVia;
    private final short numero;
    private final String sufijoNumero;
    private final Municipio municipio;

    public Address(String tipoVia, String nombreVia, short numero, String sufijoNumero, Municipio municipio)
    {
        this.tipoVia = tipoVia;
        this.nombreVia = nombreVia;
        this.numero = numero;
        this.sufijoNumero = sufijoNumero;
        this.municipio = municipio;
    }

    public String getTipoVia()
    {
        return tipoVia;
    }

    public String getNombreVia()
    {
        return nombreVia;
    }

    public short getNumero()
    {
        return numero;
    }

    public String getSufijoNumero()
    {
        return sufijoNumero;
    }

    public Municipio getMunicipio()
    {
        return municipio;
    }
}
