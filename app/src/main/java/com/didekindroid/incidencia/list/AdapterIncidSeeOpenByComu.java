package com.didekindroid.incidencia.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import timber.log.Timber;

import static java.util.Objects.requireNonNull;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
class AdapterIncidSeeOpenByComu extends ArrayAdapter<IncidenciaUser> {

    AdapterIncidSeeOpenByComu(Context context)
    {
        super(context, R.layout.incid_see_by_comu_list_item, R.id.incid_see_apertura_block);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        Timber.d("getViewInViewer()");
        VwHolderIncidSeeItem viewHolder;
        final IncidenciaUser incidencia = getItem(position);

        if (convertView == null) {
            Timber.d("getViewInViewer(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_comu_list_item, parent, false);
            // Hacemos visible el bloque de la fecha de alta de la resolución.
            if (requireNonNull(incidencia).getFechaAltaResolucion() != null) {
                convertView.findViewById(R.id.incid_see_resolucion_block).setVisibility(View.VISIBLE);
                viewHolder = new VwHolderIncidSeeOpenItem(convertView);
            } else {
                viewHolder = new VwHolderIncidSeeItem(convertView);
            }
            convertView.setTag(viewHolder);
        }
        viewHolder = (VwHolderIncidSeeItem) convertView.getTag();
        viewHolder.initializeTextInViews(incidencia);
        return convertView;
    }
}
