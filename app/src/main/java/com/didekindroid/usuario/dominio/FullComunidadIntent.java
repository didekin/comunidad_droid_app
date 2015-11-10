package com.didekindroid.usuario.dominio;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;

import java.io.Serializable;

import static com.didekindroid.common.utils.SerialNumber.COMUNIDAD_FULL_INTENT;

/**
 * User: pedro@didekin
 * Date: 09/11/15
 * Time: 17:22
 */
public final class FullComunidadIntent implements Serializable {

    private static final long serialVersionUID = COMUNIDAD_FULL_INTENT.number;

    private ComunidadIntent comunidadIntent;
    private final long c_Id;
    private final String nombreMunicipio;
    private final String nombreProvincia;

    public FullComunidadIntent(Comunidad comunidad)
    {
        comunidadIntent = new ComunidadIntent(comunidad);
        c_Id = comunidad.getC_Id();
        nombreMunicipio = comunidad.getMunicipio().getNombre();
        nombreProvincia = comunidad.getMunicipio().getProvincia().getNombre();
    }

    public final Comunidad getComunidad(){
        return new Comunidad.ComunidadBuilder()
                .c_id(c_Id)
                .tipoVia(comunidadIntent.getTipoVia())
                .nombreVia(comunidadIntent.getNombreVia())
                .numero(comunidadIntent.getNumero())
                .sufijoNumero(comunidadIntent.getSufijoNumero())
                .municipio(
                        new Municipio(
                                comunidadIntent.getCodMunicipioInProvincia(),
                                nombreMunicipio,
                                new Provincia(
                                        comunidadIntent.getProvinciaId(),
                                        nombreProvincia)))
                .build();
    }

    public long getC_Id()
    {
        return c_Id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullComunidadIntent that = (FullComunidadIntent) o;

        return c_Id == that.c_Id;

    }

    @Override
    public int hashCode()
    {
        return (int) (c_Id ^ (c_Id >>> 32));
    }
}
