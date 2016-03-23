package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekindroid.R;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
public class IncidSeeClosedByComuAdapter extends ArrayAdapter<IncidenciaUser> {

    private static final String TAG = IncidSeeClosedByComuAdapter.class.getCanonicalName();

    public IncidSeeClosedByComuAdapter(Context context)
    {
        super(context, R.layout.incid_see_by_comu_list_item, R.id.incid_descripcion_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView()");
        IncidenciaUserViewHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_comu_list_item, parent, false);
            viewHolder = new IncidenciaUserViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (IncidenciaUserViewHolder) convertView.getTag();
        final IncidenciaUser incidencia = getItem(position);
        viewHolder.initializeTextInViews(incidencia);
        return convertView;
    }

    private class IncidenciaUserViewHolder {
        final TextView mDescripcionView;
        final TextView mFechaAltaView;
        final TextView mIniciador;
        final TextView mAmbitoView;
        final TextView mImportanciaComuView;

        final Resources resources;

        public IncidenciaUserViewHolder(View convertView)
        {
            mDescripcionView = (TextView) convertView.findViewById(R.id.incid_descripcion_view);
            mFechaAltaView = (TextView) convertView.findViewById(R.id.incid_fecha_alta_view);
            mIniciador = (TextView) convertView.findViewById(R.id.incid_see_iniciador_view);
            mAmbitoView = (TextView) convertView.findViewById(R.id.incid_ambito_view);
            mImportanciaComuView = (TextView) convertView.findViewById(R.id.incid_importancia_comunidad_view);

            resources = convertView.getContext().getResources();
        }

        void initializeTextInViews(IncidenciaUser incidenciaUser)
        {
            Log.d(TAG, "initializeTextInViews()");
            mDescripcionView.setText(incidenciaUser.getIncidencia().getDescripcion());
            mFechaAltaView.setText(formatTimeStampToString(incidenciaUser.getIncidencia().getFechaAlta()));
            mIniciador.setText(incidenciaUser.getUsuario() != null ? incidenciaUser.getUsuario().getAlias() : incidenciaUser.getIncidencia().getUserName());
            short ambitoPk = incidenciaUser.getIncidencia().getAmbitoIncidencia().getAmbitoId();
            mAmbitoView.setText(new IncidenciaDataDbHelper(getContext()).getAmbitoDescByPk(ambitoPk));
            int mImportanciaAvg = Math.round(incidenciaUser.getIncidencia().getImportanciaAvg());
            String importanciAvgStr = mImportanciaAvg == 0 ? "" : resources.getStringArray(R.array.IncidImportanciaArray)[mImportanciaAvg];
            mImportanciaComuView.setText(importanciAvgStr);
        }
    }
}
