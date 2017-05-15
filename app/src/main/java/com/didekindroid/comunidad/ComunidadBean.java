package com.didekindroid.comunidad;

import android.content.res.Resources;

import com.didekindroid.R;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.NOMBRE_VIA;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.SUFIJO_NUMERO;


/**
 * User: pedro@didekin
 * Date: 12/05/15
 * Time: 16:55
 */
public class ComunidadBean implements Serializable {

    private long comunidadId;
    private TipoViaValueObj tipoVia;
    private String nombreVia;
    private String numeroString;
    private short numero;
    private String sufijoNumero;
    private Comunidad comunidad;
    private Municipio municipio;

    public ComunidadBean()
    {
    }

    public ComunidadBean(TipoViaValueObj tipoVia, String nombreVia, String numeroEnVia,
                         String sufijoNumero, Municipio municipio)
    {
        this.tipoVia = tipoVia;
        this.nombreVia = nombreVia;
        numeroString = numeroEnVia;
        this.sufijoNumero = sufijoNumero;
        this.municipio = municipio;
        comunidadId = 0L;
    }

    public ComunidadBean(long comunidadId, TipoViaValueObj tipoVia, String nombreVia, String numeroEnVia,
                         String sufijoNumero, Municipio municipio)
    {
        this(tipoVia, nombreVia, numeroEnVia, sufijoNumero, municipio);
        this.comunidadId = comunidadId;
    }

    public boolean validate(Resources resources, StringBuilder errorMsg)
    {
        Timber.d("validate()");

        boolean isValid = validateTipoVia(resources, errorMsg)
                & validateNombreVia(resources.getText(R.string.nombre_via), errorMsg)
                & validateNumeroEnVia(resources.getText(R.string.numero_en_via), errorMsg)
                & validateSufijo(resources.getText(R.string.sufijo_numero), errorMsg)
                & validateMunicipio(resources, errorMsg);

        if (isValid) {
            comunidad = new Comunidad.ComunidadBuilder()
                    .tipoVia(tipoVia.getTipoViaDesc())
                    .nombreVia(nombreVia)
                    .numero(numero)
                    .sufijoNumero(sufijoNumero)
                    .municipio(municipio)
                    .build();
        }

        return isValid;
    }

    private boolean validateTipoVia(Resources resources, StringBuilder errorMsg)
    {
        if (tipoVia == null || tipoVia.getTipoViaDesc().trim().equals(resources.getString(R.string.tipo_via_spinner))) {
            errorMsg.append(resources.getString(R.string.tipo_via)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    boolean validateNombreVia(CharSequence resources, StringBuilder errorMsg)
    {
        boolean isValid = NOMBRE_VIA.isPatternOk(nombreVia);
        if (!isValid) {
            errorMsg.append(resources).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean validateNumeroEnVia(CharSequence resources, StringBuilder errorMsg)
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
            errorMsg.append(resources).append(LINE_BREAK.getRegexp());
            isValid = false;
        }
        return isValid;
    }

    boolean validateSufijo(CharSequence resources, StringBuilder errorMsg)
    {
        if (sufijoNumero == null || sufijoNumero.trim().isEmpty()) return true;

        boolean isValid = SUFIJO_NUMERO.isPatternOk(sufijoNumero);
        if (!isValid) {
            errorMsg.append(resources).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    boolean validateMunicipio(Resources resources, StringBuilder errorMsg)
    {
        if (municipio == null) {
            return false;
        }
        Provincia provincia = municipio.getProvincia();
        boolean isValid = !(provincia == null || municipio.getCodInProvincia() == 0
                || provincia.getProvinciaId() == 0);
        if (!isValid) {
            errorMsg.append(resources.getString(R.string.municipio)).append(LINE_BREAK.getRegexp());
        }
        return isValid;
    }

    public Comunidad getComunidad()
    {
        return comunidad;
    }

    public void setComunidadId(long comunidadId)
    {
        this.comunidadId = comunidadId;
    }

    public long getComunidadId()
    {
        return comunidadId;
    }

    public Municipio getMunicipio()
    {
        return municipio;
    }

    public void setMunicipio(Municipio municipio)
    {
        this.municipio = municipio;
    }

    public String getNombreVia()
    {
        return nombreVia;
    }

    public void setNombreVia(String nombreVia)
    {
        this.nombreVia = nombreVia;
    }

    public short getNumero()
    {
        return numero;
    }

    public String getNumeroString()
    {
        return numeroString;
    }

    void setNumeroString(String numeroString)
    {
        this.numeroString = numeroString;
    }

    public String getSufijoNumero()
    {
        return sufijoNumero;
    }

    public void setSufijoNumero(String sufijoNumero)
    {
        this.sufijoNumero = sufijoNumero;
    }

    public TipoViaValueObj getTipoVia()
    {
        return tipoVia;
    }

    public void setTipoVia(TipoViaValueObj tipoVia)
    {
        this.tipoVia = tipoVia;
    }

    public Provincia getProvincia()
    {
        if (municipio == null) {
            return null;
        }
        return municipio.getProvincia();
    }
}
