package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;

import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;

/**
 * User: pedro@didekin
 * Date: 24/02/16
 * Time: 10:01
 */
public class IncidImportanciaBean {

    private short importancia;

    public IncidImportanciaBean()
    {
    }

    public void setImportancia(short importancia)
    {
        this.importancia = importancia;
    }

    public short getImportancia()
    {
        return importancia;
    }

    boolean validateBean(StringBuilder errorMsg, Resources resources)
    {
        return validateImportancia(errorMsg, resources);
    }

    boolean validateImportancia(StringBuilder errorMsg, Resources resources)
    {
        short upperBound = (short) resources.getStringArray(R.array.IncidImportanciaArray).length;
        if (!(importancia >= 0 && importancia < upperBound)) {
            errorMsg.append(resources.getString(R.string.incid_reg_importancia)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    public IncidImportancia makeIncidImportancia(StringBuilder errorMsg, Resources resources, Incidencia incidencia)
    {
        if (validateBean(errorMsg, resources)) {
            return new IncidImportancia.IncidImportanciaBuilder(incidencia).importancia(importancia).build();
        } else {
            throw new IllegalStateException(errorMsg.toString());
        }
    }
}
