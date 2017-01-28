package com.didekindroid.incidencia.activity;

import android.view.View;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import static com.didekindroid.util.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 23/03/16
 * Time: 14:21
 */
class IncidClosedSeeVwHolder extends IncidSeeItemVwHolder {

    private final TextView mFechaCierreView;

    IncidClosedSeeVwHolder(View convertView)
    {
        super(convertView);
        mFechaCierreView = (TextView) convertView.findViewById(R.id.incid_fecha_cierre_view);
    }

    void initializeTextInViews(IncidenciaUser incidenciaUser)
    {
        super.initializeTextInViews(incidenciaUser);
        mFechaCierreView.setText(formatTimeStampToString(incidenciaUser.getIncidencia().getFechaCierre()));
    }
}
