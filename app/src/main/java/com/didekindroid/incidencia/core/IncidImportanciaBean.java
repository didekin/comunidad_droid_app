package com.didekindroid.incidencia.core;

import android.content.res.Resources;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.incidencia.IncidenciaBean;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.io.Serializable;

import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;


/**
 * User: pedro@didekin
 * Date: 24/02/16
 * Time: 10:01
 */
public class IncidImportanciaBean implements Serializable {

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

    boolean validateRange(StringBuilder errorMsg, Resources resources)
    {
        short upperBound = (short) resources.getStringArray(R.array.IncidImportanciaArray).length;
        if (!(importancia >= 0 && importancia < upperBound)) {
            errorMsg.append(resources.getString(R.string.incid_reg_importancia)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    public IncidImportancia makeIncidImportancia(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
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
            return null;
        }
    }

    public IncidImportancia makeIncidImportancia(StringBuilder errorMsg, Resources resources, View fragmentView, IncidenciaBean incidenciaBean, Incidencia oldIncidencia)
    {
        final Incidencia newIncidencia = incidenciaBean.makeIncidenciaFromView(fragmentView, errorMsg, resources);

        if (newIncidencia != null & validateRange(errorMsg, resources)) {
            return new IncidImportancia.IncidImportanciaBuilder(
                    new Incidencia.IncidenciaBuilder()
                            .copyIncidencia(oldIncidencia)
                            .ambitoIncid(newIncidencia.getAmbitoIncidencia())
                            .descripcion(newIncidencia.getDescripcion())
                            .build())
                    .importancia(importancia)
                    .build();
        } else {
            throw new IllegalStateException(errorMsg.toString());
        }
    }
}
