package com.didekindroid.incidencia.activity;

import android.view.View;
import android.widget.TextView;

import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekindroid.R;

import static com.didekindroid.util.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 09/06/16
 * Time: 19:58
 */
class IncidSeeOpenVwHolder extends IncidSeeItemVwHolder {

    private final TextView mFechaAltaResolucion;

    IncidSeeOpenVwHolder(View convertView)
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
