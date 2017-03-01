package com.didekindroid.incidencia.resolucion;

import android.content.res.Resources;

import com.didekindroid.R;
import com.didekindroid.util.FechaPickerBean;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.text.ParseException;

import static com.didekindroid.util.UIutils.formatTimeToString;
import static com.didekindroid.util.UIutils.getIntFromStringDecimal;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinlib.model.incidencia.dominio.IncidDataPatterns.INCID_RESOLUCION_DESC;
import static com.didekinlib.model.incidencia.dominio.IncidDataPatterns.INCID_RES_AVANCE_DESC;

/**
 * User: pedro@didekin
 * Date: 10/03/16
 * Time: 11:46
 */
class ResolucionBean implements FechaPickerBean {

    private long fechaPrevista;
    private String fechaPrevistaText;
    private String plan;
    private String avanceDesc;
    private String costePrevText;
    private int costePrev;

    ResolucionBean()
    {
    }

    long getFechaPrevista()
    {
        return fechaPrevista;
    }

    @Override
    public void setFechaPrevista(long fechaPrevista)
    {
        this.fechaPrevista = fechaPrevista;
    }

    String getPlan()
    {
        return plan;
    }

    void setPlan(String plan)
    {
        this.plan = plan;
    }

    String getAvanceDesc()
    {
        return avanceDesc;
    }

    void setAvanceDesc(String avanceDesc)
    {
        this.avanceDesc = avanceDesc;
    }

    int getCostePrev()
    {
        return costePrev;
    }

    void setCostePrevText(String costePrevText)
    {
        this.costePrevText = costePrevText;
    }

    void setFechaPrevistaText(String fechaPrevistaText)
    {
        this.fechaPrevistaText = fechaPrevistaText;
    }

    //  ====================================== Validation methods ======================================

    boolean validateBeanPlan(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
    {
        return validateFechaPrev(errorMsg, resources, incidImportancia)
                & validatePlan(errorMsg, resources)
                & validateCostePrev(errorMsg, resources);
    }

    boolean validateBeanAvance(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
    {
        return validateFechaPrev(errorMsg, resources, incidImportancia)
                & validateAvaceDesc(errorMsg, resources)
                & validateCostePrev(errorMsg, resources);
    }

    private boolean validateAvaceDesc(StringBuilder errorMsg, Resources resources)
    {
        if (!INCID_RES_AVANCE_DESC.isPatternOk(avanceDesc)) {
            errorMsg.append(resources.getString(R.string.incid_resolucion_avance_rot)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    private boolean validateFechaPrev(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
    {
        if (fechaPrevista < incidImportancia.getIncidencia().getFechaAlta().getTime()) {
            errorMsg.append(resources.getString(R.string.incid_resolucion_fecha_prev_msg)).append(LINE_BREAK.getRegexp());
            return false;
        }
        // Aquí controlamos que no ha dejado el texto por defecto en fechaPrevista view.
        return fechaPrevistaText.equals(formatTimeToString(fechaPrevista));
    }

    private boolean validatePlan(StringBuilder errorMsg, Resources resources)
    {
        if (!INCID_RESOLUCION_DESC.isPatternOk(plan)) {
            errorMsg.append(resources.getString(R.string.incid_resolucion_descrip_msg)).append(LINE_BREAK.getRegexp());
            return false;
        }
        return true;
    }

    private boolean validateCostePrev(StringBuilder errorMsg, Resources resources)
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
