package com.didekindroid.incidencia.core.resolucion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.Avance;

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
        Timber.d("getViewInViewer()");
        AvanceViewHolder viewHolder;

        if (convertView == null) {
            Timber.d("getViewInViewer(), convertView == null");
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
            mFechaAltaView = convertView.findViewById(R.id.incid_avance_fecha_view);
            mAliasView = convertView.findViewById(R.id.incid_avance_aliasUser_view);
            mDescripcionView = convertView.findViewById(R.id.incid_avance_desc_view);
        }

        void initializeTextInViews(Avance avance)
        {
            mFechaAltaView.setText(formatTimeStampToString(avance.getFechaAlta()));
            mAliasView.setText(avance.getAlias() == null ? avance.getUserName() : avance.getAlias());
            mDescripcionView.setText(avance.getAvanceDesc());
        }
    }
}
