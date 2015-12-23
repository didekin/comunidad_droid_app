package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.AmbitoIncidencia;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;

import static com.didekin.incidservice.domain.IncidDataPatterns.INCID_DESC;
import static com.didekin.serviceone.domain.UserDataPatterns.LINE_BREAK;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 11:19
 */
public class IncidenciaBean {

    private short importanciaIncid;
    private short codAmbitoIncid;
    private String descripcion;

    public IncidenciaBean()
    {
    }

    public IncidenciaBean setImportanciaIncid(short importanciaIncid)
    {
        this.importanciaIncid = importanciaIncid;
        return this;
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

    public IncidUserComu makeIncidUserComu(final View mFragmentView, StringBuilder errorMsg, long comunidadId)
    {
        setDescripcion(((EditText) mFragmentView.findViewById(R.id.incid_reg_desc_ed)).getText().toString());

        if (validateBean(errorMsg)) {
            Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                    .ambitoIncid(new AmbitoIncidencia(codAmbitoIncid))
                    .descripcion(descripcion).build();
            UsuarioComunidad userComu = new UsuarioComunidad
                    .UserComuBuilder(new Comunidad.ComunidadBuilder().c_id(comunidadId).build(), null)
                    .build();
            return new IncidUserComu(incidencia, userComu, importanciaIncid, null);
        } else {
            return null;
        }
    }

    boolean validateBean(StringBuilder errorMsg)
    {
        Resources resources = getContext().getResources();
        return validateImportancia(errorMsg, resources) & validateCodAmbito(errorMsg, resources) & validateDescripcion(errorMsg, resources);
    }

    private boolean validateDescripcion(StringBuilder errorMsg, Resources resources)
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

    private boolean validateImportancia(StringBuilder errorMsg, Resources resources)
    {
        short upperBound = (short) resources.getStringArray(R.array.IncidImportanciaArray).length;
        if (!(importanciaIncid > 0 && importanciaIncid < upperBound)) {
            errorMsg.append(resources.getString(R.string.incid_reg_importancia)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }
}