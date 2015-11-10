package com.didekindroid.usuario.dominio;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;

import java.io.Serializable;

import static com.didekindroid.common.utils.SerialNumber.COMUNIDAD_INTENT;

/**
 * User: pedro@didekin
 * Date: 09/11/15
 * Time: 16:26
 */
public final class ComunidadIntent implements Serializable {

    private static final long serialVersionUID = COMUNIDAD_INTENT.number;

    private final String tipoVia;
    private final String nombreVia;
    private final short numero;
    private final String sufijoNumero;
    private final short codMunicipioInProvincia;
    private final short provinciaId;

    public ComunidadIntent(final Comunidad comunidad)
    {
        tipoVia = comunidad.getTipoVia();
        nombreVia = comunidad.getNombreVia();
        numero = comunidad.getNumero();
        sufijoNumero = comunidad.getSufijoNumero();
        codMunicipioInProvincia = comunidad.getMunicipio().getCodInProvincia();
        provinciaId = comunidad.getMunicipio().getProvincia().getProvinciaId();
    }

    public final Comunidad getComunidad(){
        return new Comunidad.ComunidadBuilder()
                .tipoVia(tipoVia)
                .nombreVia(nombreVia)
                .numero(numero)
                .sufijoNumero(sufijoNumero)
                .municipio(new Municipio(codMunicipioInProvincia,new Provincia(provinciaId)))
                .build();
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

    public short getCodMunicipioInProvincia()
    {
        return codMunicipioInProvincia;
    }

    public short getProvinciaId()
    {
        return provinciaId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComunidadIntent that = (ComunidadIntent) o;

        if (numero != that.numero) return false;
        if (codMunicipioInProvincia != that.codMunicipioInProvincia) return false;
        if (provinciaId != that.provinciaId) return false;
        if (!tipoVia.equals(that.tipoVia)) return false;
        if (!nombreVia.equals(that.nombreVia)) return false;
        return !(sufijoNumero != null ? !sufijoNumero.equals(that.sufijoNumero) : that.sufijoNumero != null);

    }

    @Override
    public int hashCode()
    {
        int result = tipoVia.hashCode();
        result = 31 * result + nombreVia.hashCode();
        result = 31 * result + (int) numero;
        result = 31 * result + (sufijoNumero != null ? sufijoNumero.hashCode() : 0);
        result = 31 * result + (int) codMunicipioInProvincia;
        result = 31 * result + (int) provinciaId;
        return result;
    }
}
