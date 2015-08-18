package com.didekindroid.usuario.comunidad.dominio;

import android.content.res.Resources;
import com.didekindroid.R;
import com.didekindroid.common.dominio.SerialNumbers;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;

import java.io.Serializable;
import java.sql.Timestamp;

import static com.didekindroid.common.ui.CommonPatterns.LINE_BREAK;
import static com.didekindroid.common.ui.CommonPatterns.SELECT;
import static com.didekindroid.usuario.comunidad.dominio.UserPatterns.NOMBRE_VIA;
import static com.didekindroid.usuario.comunidad.dominio.UserPatterns.SUFIJO_NUMERO;

/**
 * User: pedro@didekin
 * Date: 12/05/15
 * Time: 16:55
 */
public class ComunidadBean implements Serializable {

    private static final long serialVersionUID = SerialNumbers.COMUNIDAD_BEAN.number;

    private transient String numeroString;
    private Comunidad comunidad;

    public ComunidadBean(Comunidad comunidad)
    {
        this.comunidad = comunidad;
    }

    public ComunidadBean(String tipoVia, String nombreVia, String numeroEnVia,
                         String sufijoNumero, Municipio municipio)
    {
        comunidad = new Comunidad(tipoVia, nombreVia, sufijoNumero, municipio);
        this.numeroString = numeroEnVia;
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        return validateTipoVia(resources, errorMsg)
                & validateNombreVia(resources.getText(R.string.nombre_via), errorMsg)
                & validateNumeroEnVia(resources.getText(R.string.numero_en_via), errorMsg)
                & validateSufijo(resources.getText(R.string.sufijo_numero), errorMsg)
                & validateMunicipio(resources, errorMsg);
    }

    boolean validateTipoVia(Resources resources, StringBuilder errorMsg)
    {
        String tipoVia = comunidad.getTipoVia();

        if (tipoVia == null || tipoVia.trim().equals(resources.getString(R.string.tipo_via_spinner))) {
            errorMsg.append(resources.getString(R.string.tipo_via) + LINE_BREAK.literal);
            return false;
        }
        return true;
    }

    public boolean validateNombreVia(CharSequence resources, StringBuilder errorMsg)
    {
        boolean isValid = NOMBRE_VIA.pattern.matcher(comunidad.getNombreVia()).matches()
                && !SELECT.pattern.matcher(comunidad.getNombreVia()).find();
        if (!isValid) {
            errorMsg.append(resources + LINE_BREAK.literal);
        }
        return isValid;
    }

    public boolean validateNumeroEnVia(CharSequence resources, StringBuilder errorMsg)
    {
        // Si no se cubre en el formulario, numeroString es "". Ponemos 0 como valor por defecto.
        if (numeroString == null || numeroString.trim().isEmpty()){
            comunidad.setNumero((short)0);
            return true;
        }

        boolean isValid = true;
        try {
            comunidad.setNumero(Short.parseShort(numeroString));
        } catch (NumberFormatException ne) {
            errorMsg.append(resources + LINE_BREAK.literal);
            isValid = false;
        }
        return isValid;
    }

    public boolean validateSufijo(CharSequence resources, StringBuilder errorMsg)
    {
        if (comunidad.getSufijoNumero().trim().isEmpty()) return true;

        boolean isValid = SUFIJO_NUMERO.pattern.matcher(comunidad.getSufijoNumero()).matches()
                && !SELECT.pattern.matcher(comunidad.getSufijoNumero()).find();
        if (!isValid) {
            errorMsg.append(resources + LINE_BREAK.literal);
        }
        return isValid;
    }

    public boolean validateMunicipio(Resources resources, StringBuilder errorMsg)
    {
        Municipio municipio = comunidad.getMunicipio();
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

    public Comunidad getComunidad()
    {
        return comunidad;
    }

    public long getC_Id()
    {
        return comunidad.getC_Id();
    }

    public String getNombreVia()
    {
        return comunidad.getNombreVia();
    }

    public short getNumeroEnVia()
    {
        return comunidad.getNumero();
    }

    public String getSufijoNumero()
    {
        return comunidad.getSufijoNumero();
    }

    public String getNumeroString()
    {
        return numeroString;
    }

    public String getTipoVia()
    {
        return comunidad.getTipoVia();
    }

    public Municipio getMunicipio()
    {
        return comunidad.getMunicipio();
    }

    public Provincia getProvincia()
    {
        if (comunidad.getMunicipio() == null) {
            return null;
        }
        return comunidad.getMunicipio().getProvincia();
    }

    public void setNumeroEnVia(short numeroEnVia)
    {
        comunidad.setNumero(numeroEnVia);
    }

    public void setNumeroString(String numeroString)
    {
        this.numeroString = numeroString;
    }

    public void setNombreVia(String nombreVia)
    {
        comunidad.setNombreVia(nombreVia);
    }

    public void setSufijoNumero(String sufijoNumero)
    {
        comunidad.setSufijoNumero(sufijoNumero);
    }

    public void setFechaAlta(Timestamp fechaAlta)
    {
        comunidad.setFechaAlta(fechaAlta);
    }

    public void setTipoVia(String tipoVia)
    {
        comunidad.setTipoVia(tipoVia);
    }

    public void setMunicipio(Municipio municipio)
    {
        comunidad.setMunicipio(municipio);
    }

    public void setProvincia(Provincia provincia)
    {
        if (comunidad.getMunicipio() == null) {
            comunidad.setMunicipio(new Municipio());
        }
        comunidad.getMunicipio().setProvincia(provincia);
    }

}
