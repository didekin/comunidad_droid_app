package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekindroid.R;

import java.text.ParseException;

import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;
import static com.didekin.incidservice.dominio.IncidDataPatterns.INCID_RESOLUCION_DESC;
import static com.didekindroid.common.utils.UIutils.formatTimeToString;
import static com.didekindroid.common.utils.UIutils.getIntFromStringDecimal;

/**
 * User: pedro@didekin
 * Date: 10/03/16
 * Time: 11:46
 */
public class ResolucionBean {

    long fechaPrevista;
    String fechaPrevistaText;
    String planOrAvance;
    String costePrevText;
    int costePrev;

    public ResolucionBean()
    {
    }

    public long getFechaPrevista()
    {
        return fechaPrevista;
    }

    public void setFechaPrevista(long fechaPrevista)
    {
        this.fechaPrevista = fechaPrevista;
    }

    public String getPlanOrAvance()
    {
        return planOrAvance;
    }

    public void setPlanOrAvance(String planOrAvance)
    {
        this.planOrAvance = planOrAvance;
    }

    public int getCostePrev()
    {
        return costePrev;
    }

    public void setCostePrevText(String costePrevText)
    {
        this.costePrevText = costePrevText;
    }

    public void setFechaPrevistaText(String fechaPrevistaText)
    {
        this.fechaPrevistaText = fechaPrevistaText;
    }

    //  ====================================== Validation methods ======================================

    public boolean validateBean(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
    {
        return validateFechaPrev(errorMsg, resources, incidImportancia)
                & validateDescripcion(errorMsg, resources)
                & validateCostePrev(errorMsg, resources);
    }

    boolean validateFechaPrev(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
    {
        if (fechaPrevistaText.equals(resources.getString(R.string.incid_resolucion_fecha_default_txt))
                || fechaPrevista < incidImportancia.getIncidencia().getFechaAlta().getTime()) {
            errorMsg.append(resources.getString(R.string.incid_resolucion_fecha_prev_msg)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return fechaPrevistaText.equals(formatTimeToString(fechaPrevista));
    }

    boolean validateDescripcion(StringBuilder errorMsg, Resources resources)
    {
        if (!INCID_RESOLUCION_DESC.isPatternOk(planOrAvance)) {
            errorMsg.append(resources.getString(R.string.incid_resolucion_descrip_msg)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    boolean validateCostePrev(StringBuilder errorMsg, Resources resources)
    {
        if (costePrevText.isEmpty()) {
            return true;
        }
        try {
            costePrev = getIntFromStringDecimal(costePrevText);
        } catch (ParseException e) {
            errorMsg.append(resources.getString(R.string.incid_resolucion_coste_prev_msg)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }
}
