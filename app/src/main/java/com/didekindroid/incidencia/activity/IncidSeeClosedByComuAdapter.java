package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
public class IncidSeeClosedByComuAdapter extends ArrayAdapter<IncidenciaUser> {

    private static final String TAG = IncidSeeClosedByComuAdapter.class.getCanonicalName();

    public IncidSeeClosedByComuAdapter(Context context)
    {
        super(context, R.layout.incid_see_by_comu_list_item, R.id.incid_see_apertura_block);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView()");
        IncidClosedSeeItemVwHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_comu_list_item, parent, false);
            // Hacemos visible el bloque de la fecha de cierre.
            convertView.findViewById(R.id.incid_see_cierre_block).setVisibility(View.VISIBLE);
            viewHolder = new IncidClosedSeeItemVwHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (IncidClosedSeeItemVwHolder) convertView.getTag();
        final IncidenciaUser incidencia = getItem(position);
        viewHolder.initializeTextInViews(incidencia);
        return convertView;
    }
}
