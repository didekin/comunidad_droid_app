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
public class IncidSeeOpenByComuAdapter extends ArrayAdapter<IncidenciaUser> {

    private static final String TAG = IncidSeeOpenByComuAdapter.class.getCanonicalName();

    public IncidSeeOpenByComuAdapter(Context context)
    {
        super(context, R.layout.incid_see_by_comu_list_item, R.id.incid_see_apertura_block);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView()");
        IncidOpenSeeItemVwHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_comu_list_item, parent, false);
            viewHolder = new IncidOpenSeeItemVwHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (IncidOpenSeeItemVwHolder) convertView.getTag();
        final IncidenciaUser incidencia = getItem(position);
        viewHolder.initializeTextInViews(incidencia);
        return convertView;
    }
}
