package com.didekindroid.incidencia.dominio;

import android.content.res.Resources;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekindroid.util.FechaPickerBean;
import com.didekindroid.R;

import java.text.ParseException;

import static com.didekin.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekin.incidencia.dominio.IncidDataPatterns.INCID_RESOLUCION_DESC;
import static com.didekin.incidencia.dominio.IncidDataPatterns.INCID_RES_AVANCE_DESC;
import static com.didekindroid.util.UIutils.formatTimeToString;
import static com.didekindroid.util.UIutils.getIntFromStringDecimal;

/**
 * User: pedro@didekin
 * Date: 10/03/16
 * Time: 11:46
 */
public class ResolucionBean implements FechaPickerBean {

    private long fechaPrevista;
    private String fechaPrevistaText;
    private String plan;
    private String avanceDesc;
    private String costePrevText;
    private int costePrev;

    public ResolucionBean()
    {
    }

    public long getFechaPrevista()
    {
        return fechaPrevista;
    }

    @Override
    public void setFechaPrevista(long fechaPrevista)
    {
        this.fechaPrevista = fechaPrevista;
    }

    public String getPlan()
    {
        return plan;
    }

    public void setAvanceDesc(String avanceDesc)
    {
        this.avanceDesc = avanceDesc;
    }

    public String getAvanceDesc()
    {
        return avanceDesc;
    }

    public void setPlan(String plan)
    {
        this.plan = plan;
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

    public boolean validateBeanPlan(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
    {
        return validateFechaPrev(errorMsg, resources, incidImportancia)
                & validatePlan(errorMsg, resources)
                & validateCostePrev(errorMsg, resources);
    }

    public boolean validateBeanAvance(StringBuilder errorMsg, Resources resources, IncidImportancia incidImportancia)
    {
        return validateFechaPrev(errorMsg, resources, incidImportancia)
                & validateAvaceDesc(errorMsg, resources)
                & validateCostePrev(errorMsg, resources);
    }

    private boolean validateAvaceDesc(StringBuilder errorMsg, Resources resources)
    {
        if(!INCID_RES_AVANCE_DESC.isPatternOk(avanceDesc)){
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
