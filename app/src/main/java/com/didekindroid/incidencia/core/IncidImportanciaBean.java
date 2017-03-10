package com.didekindroid.incidencia.core;

import android.content.res.Resources;
import android.view.View;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;


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

    public short getImportancia()
    {
        return importancia;
    }

    public void setImportancia(short importancia)
    {
        this.importancia = importancia;
    }

    private boolean validateRange(StringBuilder errorMsg, Resources resources)
    {
        short upperBound = (short) resources.getStringArray(R.array.IncidImportanciaArray).length;
        if (!(importancia >= 0 && importancia < upperBound)) {
            errorMsg.append(resources.getString(R.string.incid_reg_importancia)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    IncidImportancia makeIncidImportancia(StringBuilder errorMsg, Resources resources, View fragmentView, IncidImportancia incidImportancia)
    {
        if (validateRange(errorMsg, resources)) {
            return new IncidImportancia.IncidImportanciaBuilder(
                    new Incidencia.IncidenciaBuilder()
                            .copyIncidencia(incidImportancia.getIncidencia())
                            .build())
                    .importancia(importancia)
                    .build();
        } else {
            throw new IllegalStateException(errorMsg.toString());
        }
    }

    public IncidImportancia makeIncidImportancia(StringBuilder errorMsg, Resources resources, View fragmentView, IncidenciaBean incidenciaBean)
    {
        final Incidencia incidencia = incidenciaBean.makeIncidenciaFromView(fragmentView, errorMsg, resources);

        if (incidencia != null & validateRange(errorMsg, resources)) {
            return new IncidImportancia.IncidImportanciaBuilder(incidencia)
                    .importancia(importancia)
                    .build();
        } else {
            throw new IllegalStateException(errorMsg.toString());
        }
    }

    IncidImportancia makeIncidImportancia(StringBuilder errorMsg, Resources resources, View fragmentView, IncidenciaBean incidenciaBean, IncidImportancia incidImportancia)
    {
        final Incidencia incidencia = incidenciaBean.makeIncidenciaFromView(fragmentView, errorMsg, resources);

        if (incidencia != null & validateRange(errorMsg, resources)) {
            return new IncidImportancia.IncidImportanciaBuilder(
                    new Incidencia.IncidenciaBuilder()
                            .copyIncidencia(incidImportancia.getIncidencia())
                            .ambitoIncid(incidencia.getAmbitoIncidencia())
                            .descripcion(incidencia.getDescripcion())
                            .build())
                    .importancia(importancia)
                    .build();
        } else {
            throw new IllegalStateException(errorMsg.toString());
        }
    }
}
