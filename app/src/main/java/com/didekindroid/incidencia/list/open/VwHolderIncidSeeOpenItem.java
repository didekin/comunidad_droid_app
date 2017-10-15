package com.didekindroid.incidencia.list.open;

import android.view.View;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.VwHolderIncidSeeItem;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import static com.didekindroid.util.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 09/06/16
 * Time: 19:58
 */
class VwHolderIncidSeeOpenItem extends VwHolderIncidSeeItem {

    private final TextView mFechaAltaResolucion;

    VwHolderIncidSeeOpenItem(View convertView)
    {
        super(convertView);
        mFechaAltaResolucion = convertView.findViewById(R.id.incid_see_fecha_alta_resolucion_view);
    }

    public void initializeTextInViews(IncidenciaUser incidenciaUser)
    {
        super.initializeTextInViews(incidenciaUser);
        mFechaAltaResolucion.setText(formatTimeStampToString(incidenciaUser.getFechaAltaResolucion()));
    }
}
