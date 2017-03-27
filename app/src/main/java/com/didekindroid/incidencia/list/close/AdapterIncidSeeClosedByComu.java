package com.didekindroid.incidencia.list.close;

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
class AdapterIncidSeeClosedByComu extends ArrayAdapter<IncidenciaUser> {

    AdapterIncidSeeClosedByComu(Context context)
    {
        super(context, R.layout.incid_see_by_comu_list_item, R.id.incid_see_apertura_block);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getViewInViewer()");
        VwHolderIncidClosedSeeItem viewHolder;

        if (convertView == null) {
            Timber.d("getViewInViewer(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_comu_list_item, parent, false);
            // Hacemos visible el bloque de la fecha de cierre.
            convertView.findViewById(R.id.incid_see_cierre_block).setVisibility(View.VISIBLE);
            viewHolder = new VwHolderIncidClosedSeeItem(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (VwHolderIncidClosedSeeItem) convertView.getTag();
        final IncidenciaUser incidencia = getItem(position);
        viewHolder.initializeTextInViews(incidencia);
        return convertView;
    }
}
