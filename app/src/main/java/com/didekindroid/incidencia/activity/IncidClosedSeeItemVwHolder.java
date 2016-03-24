package com.didekindroid.incidencia.activity;

import android.view.View;
import android.widget.TextView;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekindroid.R;

import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 23/03/16
 * Time: 14:21
 */
public class IncidClosedSeeItemVwHolder extends IncidOpenSeeItemVwHolder{

    final TextView mFechaCierreView;

    public IncidClosedSeeItemVwHolder(View convertView)
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
