package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
class IncidSeeOpenByComuAdapter extends ArrayAdapter<IncidenciaUser> {

    IncidSeeOpenByComuAdapter(Context context)
    {
        super(context, R.layout.incid_see_by_comu_list_item, R.id.incid_see_apertura_block);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getView()");
        IncidSeeItemVwHolder viewHolder;
        final IncidenciaUser incidencia = getItem(position);

        if (convertView == null) {
            Timber.d("getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_comu_list_item, parent, false);
            // Hacemos visible el bloque de la fecha de alta de la resolución.
            if (incidencia.getFechaAltaResolucion() != null) {
                convertView.findViewById(R.id.incid_see_resolucion_block).setVisibility(View.VISIBLE);
                viewHolder = new IncidSeeOpenVwHolder(convertView);
            } else {
                viewHolder = new IncidSeeItemVwHolder(convertView);
            }
            convertView.setTag(viewHolder);
        }
        viewHolder = (IncidSeeItemVwHolder) convertView.getTag();
        viewHolder.initializeTextInViews(incidencia);
        return convertView;
    }
}
