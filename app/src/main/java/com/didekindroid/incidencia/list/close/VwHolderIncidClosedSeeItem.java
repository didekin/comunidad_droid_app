package com.didekindroid.incidencia.list.close;

import android.view.View;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.VwHolderIncidSeeItem;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import static com.didekindroid.util.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 23/03/16
 * Time: 14:21
 */
class VwHolderIncidClosedSeeItem extends VwHolderIncidSeeItem {

    private final TextView mFechaCierreView;

    VwHolderIncidClosedSeeItem(View convertView)
    {
        super(convertView);
        mFechaCierreView = (TextView) convertView.findViewById(R.id.incid_fecha_cierre_view);
    }

    public void initializeTextInViews(IncidenciaUser incidenciaUser)
    {
        super.initializeTextInViews(incidenciaUser);
        mFechaCierreView.setText(formatTimeStampToString(incidenciaUser.getIncidencia().getFechaCierre()));
    }
}
