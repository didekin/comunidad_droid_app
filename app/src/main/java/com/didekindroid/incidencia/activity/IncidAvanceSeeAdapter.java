package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.incidencia.dominio.Avance;
import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
class IncidAvanceSeeAdapter extends ArrayAdapter<Avance> {

    IncidAvanceSeeAdapter(Context context)
    {
        super(context, R.layout.incid_see_avances_list_item, R.id.incid_avance_fecha_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getView()");
        AvanceViewHolder viewHolder;

        if (convertView == null) {
            Timber.d("getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_avances_list_item, parent, false);
            viewHolder = new AvanceViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (AvanceViewHolder) convertView.getTag();
        final Avance avance = getItem(position);
        viewHolder.initializeTextInViews(avance);
        return convertView;
    }

    private class AvanceViewHolder {

        final TextView mFechaAltaView;
        final TextView mAliasView;
        final TextView mDescripcionView;

        AvanceViewHolder(View convertView)
        {
            mFechaAltaView = (TextView) convertView.findViewById(R.id.incid_avance_fecha_view);
            mAliasView = (TextView) convertView.findViewById(R.id.incid_avance_aliasUser_view);
            mDescripcionView = (TextView) convertView.findViewById(R.id.incid_avance_desc_view);
        }

        void initializeTextInViews(Avance avance)
        {
            mFechaAltaView.setText(formatTimeStampToString(avance.getFechaAlta()));
            mAliasView.setText(avance.getUserName());
            mDescripcionView.setText(avance.getAvanceDesc());
        }
    }
}
