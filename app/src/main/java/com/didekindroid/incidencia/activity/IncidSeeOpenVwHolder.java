package com.didekindroid.incidencia.activity;

import android.view.View;
import android.widget.TextView;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekindroid.R;

import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 09/06/16
 * Time: 19:58
 */
public class IncidSeeOpenVwHolder extends IncidSeeItemVwHolder {

    final TextView mFechaAltaResolucion;

    public IncidSeeOpenVwHolder(View convertView)
    {
        super(convertView);
        mFechaAltaResolucion = (TextView) convertView.findViewById(R.id.incid_see_fecha_alta_resolucion_view);
    }

    void initializeTextInViews(IncidenciaUser incidenciaUser)
    {
        super.initializeTextInViews(incidenciaUser);
        mFechaAltaResolucion.setText(formatTimeStampToString(incidenciaUser.getFechaAltaResolucion()));
    }
}
