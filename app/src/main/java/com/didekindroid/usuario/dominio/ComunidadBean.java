package com.didekindroid.usuario.dominio;

import android.content.res.Resources;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekindroid.R;

import static com.didekindroid.uiutils.CommonPatterns.LINE_BREAK;
import static com.didekindroid.uiutils.CommonPatterns.SELECT;

/**
 * User: pedro@didekin
 * Date: 12/05/15
 * Time: 16:55
 */
public class ComunidadBean {

    private long comunidadId;
    private String tipoVia;
    private String nombreVia;
    private String numeroString;
    private short numero;
    private String sufijoNumero;
    private Comunidad comunidad;
    private Municipio municipio;

    public ComunidadBean()
    {
    }

    public ComunidadBean(String tipoVia, String nombreVia, String numeroEnVia,
                         String sufijoNumero, Municipio municipio)
    {
        this.tipoVia = tipoVia;
        this.nombreVia = nombreVia;
        this.numeroString = numeroEnVia;
        this.sufijoNumero = sufijoNumero;
        this.municipio = municipio;
        comunidadId = 0L;
    }

    public ComunidadBean(long comunidadId, String tipoVia, String nombreVia, String numeroEnVia,
                         String sufijoNumero, Municipio municipio)
    {
        this(tipoVia, nombreVia, numeroEnVia, sufijoNumero, municipio);
        this.comunidadId = comunidadId;
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        boolean isValid;

        if ((tipoVia == null || tipoVia.isEmpty())
                && (nombreVia == null || nombreVia.isEmpty())
                && (numeroString == null || numeroString.isEmpty())
                && (sufijoNumero == null || sufijoNumero.isEmpty())
                && (municipio == null)
                && comunidadId > 0) {
            return(isValid = true);
        }

        isValid = validateTipoVia(resources, errorMsg)
                & validateNombreVia(resources.getText(R.string.nombre_via), errorMsg)
                & validateNumeroEnVia(resources.getText(R.string.numero_en_via), errorMsg)
                & validateSufijo(resources.getText(R.string.sufijo_numero), errorMsg)
                & validateMunicipio(resources, errorMsg);

        if (isValid) {
            comunidad = new Comunidad.ComunidadBuilder()
                    .tipoVia(tipoVia)
                    .nombreVia(nombreVia)
                    .numero(numero)
                    .sufijoNumero(sufijoNumero)
                    .municipio(municipio)
                    .build();
        }

        return isValid;
    }

    boolean validateTipoVia(Resources resources, StringBuilder errorMsg)
    {
        if (tipoVia == null || tipoVia.trim().equals(resources.getString(R.string.tipo_via_spinner))) {
            errorMsg.append(resources.getString(R.string.tipo_via) + LINE_BREAK.literal);
            return false;
        }
        return true;
    }

    public boolean validateNombreVia(CharSequence resources, StringBuilder errorMsg)
    {
        boolean isValid = UserPatterns.NOMBRE_VIA.pattern.matcher(nombreVia).matches()
                && !SELECT.pattern.matcher(nombreVia).find();
        if (!isValid) {
            errorMsg.append(resources + LINE_BREAK.literal);
        }
        return isValid;
    }

    public boolean validateNumeroEnVia(CharSequence resources, StringBuilder errorMsg)
    {
        // Si no se cubre en el formulario, numeroString es "". Ponemos 0 como valor por defecto.
        if (numeroString == null || numeroString.trim().isEmpty()) {
            numeroString = String.valueOf((short) 0);
            return true;
        }

        boolean isValid = true;
        try {
            numero = Short.parseShort(numeroString);
        } catch (NumberFormatException ne) {
            errorMsg.append(resources + LINE_BREAK.literal);
            isValid = false;
        }
        return isValid;
    }

    public boolean validateSufijo(CharSequence resources, StringBuilder errorMsg)
    {
        if (sufijoNumero == null || sufijoNumero.trim().isEmpty()) return true;

        boolean isValid = UserPatterns.SUFIJO_NUMERO.pattern.matcher(sufijoNumero).matches()
                && !SELECT.pattern.matcher(sufijoNumero).find();
        if (!isValid) {
            errorMsg.append(resources + LINE_BREAK.literal);
        }
        return isValid;
    }

    public boolean validateMunicipio(Resources resources, StringBuilder errorMsg)
    {
        if (municipio == null) {
            return false;
        }
        Provincia provincia = municipio.getProvincia();
        boolean isValid = (provincia == null || municipio.getCodInProvincia() == 0 || provincia.getProvinciaId() ==
                0) ? false : true;
        if (!isValid) {
            errorMsg.append(resources.getString(R.string.municipio) + LINE_BREAK.literal);
        }
        return isValid;
    }

    public long getComunidadId()
    {
        return comunidadId;
    }

    public Comunidad getComunidad()
    {
        return comunidad;
    }

    public Municipio getMunicipio()
    {
        return municipio;
    }

    public String getNombreVia()
    {
        return nombreVia;
    }

    public short getNumero()
    {
        return numero;
    }

    public String getNumeroString()
    {
        return numeroString;
    }

    public String getSufijoNumero()
    {
        return sufijoNumero;
    }

    public String getTipoVia()
    {
        return tipoVia;
    }

    public Provincia getProvincia()
    {
        if (municipio == null) {
            return null;
        }
        return municipio.getProvincia();
    }

    public void setMunicipio(Municipio municipio)
    {
        this.municipio = municipio;
    }

    public void setNombreVia(String nombreVia)
    {
        this.nombreVia = nombreVia;
    }

    public void setNumeroString(String numeroString)
    {
        this.numeroString = numeroString;
    }

    public void setSufijoNumero(String sufijoNumero)
    {
        this.sufijoNumero = sufijoNumero;
    }

    public void setTipoVia(String tipoVia)
    {
        this.tipoVia = tipoVia;
    }
}
