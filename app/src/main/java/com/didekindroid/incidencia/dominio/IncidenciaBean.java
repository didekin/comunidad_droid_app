package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.TipoIncidencia;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;

import static com.didekin.incidservice.domain.IncidDataPatterns.INCID_DESC;
import static com.didekin.serviceone.domain.UserDataPatterns.LINE_BREAK;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.TipoIncidencia.TIPOINCID_COUNT;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 11:19
 */
public class IncidenciaBean {

    private short importanciaIncid;
    private short codTipoIncid;
    private String descripcion;

    public IncidenciaBean()
    {
    }

    public void setImportanciaIncid(short importanciaIncid)
    {
        this.importanciaIncid = importanciaIncid;
    }

    public void setCodTipoIncid(short codTipoIncid)
    {
        this.codTipoIncid = codTipoIncid;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public IncidUserComu makeIncidUserComu(final View mFragmentView, StringBuilder errorMsg, long comunidadId)
    {
        setDescripcion(((EditText) mFragmentView.findViewById(R.id.incid_reg_desc_ed)).getText().toString());

        validateBean(errorMsg);
        if (validateBean(errorMsg)) {
            Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                    .tipoIncid(new TipoIncidencia(codTipoIncid, null))
                    .descripcion(descripcion).build();
            UsuarioComunidad userComu = new UsuarioComunidad
                    .UserComuBuilder(new Comunidad.ComunidadBuilder().c_id(comunidadId).build(), null)
                    .build();
            return new IncidUserComu(incidencia, userComu, importanciaIncid);
        } else {
            return null;
        }
    }

    private boolean validateBean(StringBuilder errorMsg)
    {
        Resources resources = getContext().getResources();
        return validateImportancia(errorMsg, resources) & validateCodTipo(errorMsg, resources) & validateDescripcion(errorMsg, resources);
    }

    private boolean validateDescripcion(StringBuilder errorMsg, Resources resources)
    {
        if (!INCID_DESC.isPatternOk(descripcion)) {
            errorMsg.append(resources.getString(R.string.incid_reg_descripcion)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    private boolean validateCodTipo(StringBuilder errorMsg, Resources resources)
    {
        if (codTipoIncid > 0 && codTipoIncid < TIPOINCID_COUNT) {
            errorMsg.append(resources.getString(R.string.incid_reg_tipoIncidencia)).append(LINE_BREAK.getRegexp());
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
