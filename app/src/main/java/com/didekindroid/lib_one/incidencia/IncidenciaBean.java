package com.didekindroid.lib_one.incidencia;

import android.content.res.Resources;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.io.Serializable;

import static com.didekindroid.lib_one.incidencia.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinlib.model.incidencia.dominio.IncidDataPatterns.INCID_DESC;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 11:19
 */
public class IncidenciaBean implements Serializable {

    private short codAmbitoIncid;
    private String descripcion;
    private long comunidadId;

    public IncidenciaBean()
    {
    }

    public short getCodAmbitoIncid()
    {
        return codAmbitoIncid;
    }

    public IncidenciaBean setCodAmbitoIncid(short codAmbitoIncid)
    {
        this.codAmbitoIncid = codAmbitoIncid;
        return this;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public IncidenciaBean setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
        return this;
    }

    public long getComunidadId()
    {
        return comunidadId;
    }

    public IncidenciaBean setComunidadId(long comunidadId)
    {
        this.comunidadId = comunidadId;
        return this;
    }

    public Incidencia makeIncidenciaFromView(final EditText editTxtDesc, StringBuilder errorMsg, Resources resources)
    {
        setDescripcion(editTxtDesc.getText().toString());
        if (validateBean(errorMsg, resources)) {
            return new Incidencia.IncidenciaBuilder()
                    .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                    .ambitoIncid(new AmbitoIncidencia(codAmbitoIncid))
                    .descripcion(descripcion)
                    .build();
        } else {
            return null;
        }
    }

    boolean validateBean(StringBuilder errorMsg, Resources resources)
    {
        return validateCodAmbito(errorMsg, resources)
                & validateDescripcion(errorMsg, resources)
                & validateComunidadId(errorMsg, resources);
    }

    private boolean validateDescripcion(StringBuilder errorMsg, Resources resources)
    {
        if (!INCID_DESC.isPatternOk(descripcion)) {
            errorMsg.append(resources.getString(R.string.incid_reg_descripcion)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    private boolean validateCodAmbito(StringBuilder errorMsg, Resources resources)
    {
        if (codAmbitoIncid <= 0 || codAmbitoIncid > AMBITO_INCID_COUNT) {
            errorMsg.append(resources.getString(R.string.incid_reg_ambitoIncidencia)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    private boolean validateComunidadId(StringBuilder errorMsg, Resources resources)
    {
        if (comunidadId <= 0) {     // TODO: mover este string a strings de librería al igual que los anteriores.
            errorMsg.append(resources.getString(R.string.comunidad_null_in_register)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }
}
