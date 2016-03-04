package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import com.didekin.incidservice.dominio.AmbitoIncidencia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;

import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;
import static com.didekin.incidservice.dominio.IncidDataPatterns.INCID_DESC;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 11:19
 */
public class IncidenciaBean {

    private short codAmbitoIncid;
    private String descripcion;
    private long comunidadId;

    public IncidenciaBean()
    {
    }

    public IncidenciaBean setCodAmbitoIncid(short codAmbitoIncid)
    {
        this.codAmbitoIncid = codAmbitoIncid;
        return this;
    }

    public IncidenciaBean setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
        return this;
    }

    public IncidenciaBean setComunidadId(long comunidadId)
    {
        this.comunidadId = comunidadId;
        return this;
    }

    public Incidencia makeIncidencia(final View mFragmentView, StringBuilder errorMsg, Resources resources)
    {
        setDescripcion(((EditText) mFragmentView.findViewById(R.id.incid_reg_desc_ed)).getText().toString());

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

    boolean validateDescripcion(StringBuilder errorMsg, Resources resources)
    {
        if (!INCID_DESC.isPatternOk(descripcion)) {
            errorMsg.append(resources.getString(R.string.incid_reg_descripcion)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    boolean validateCodAmbito(StringBuilder errorMsg, Resources resources)
    {
        if (codAmbitoIncid <= 0 || codAmbitoIncid > AMBITO_INCID_COUNT) {
            errorMsg.append(resources.getString(R.string.incid_reg_ambitoIncidencia)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    boolean validateComunidadId(StringBuilder errorMsg, Resources resources)
    {
        if (comunidadId <= 0) {
            errorMsg.append(resources.getString(R.string.reg_usercomu_comunidad_null)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }
}
