package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekindroid.R;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
public class IncidSeeByUserAdapter extends ArrayAdapter<IncidUserComu> {

    private static final String TAG = IncidSeeByUserAdapter.class.getCanonicalName();

    public IncidSeeByUserAdapter(Context context)
    {
        super(context, R.layout.incid_see_by_user_list_item, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView()");
        IncidenciaViewHolder viewHolder;

        if (convertView == null){
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_user_list_item, parent, false);
            viewHolder = new IncidenciaViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (IncidenciaViewHolder) convertView.getTag();
        final IncidUserComu incidUserComu = getItem(position);
        viewHolder.initializeTextInViews(incidUserComu);
        return convertView;
    }

    private class IncidenciaViewHolder {

        final TextView mNombreComunidadView;
        final TextView mDescripcionView;
        final TextView mFechaAltaView;
        final TextView mAmbitoView;
        final TextView mImportanciaUserView;
        final TextView mImportanciaComuView;


        public IncidenciaViewHolder(View convertView)
        {
            mNombreComunidadView = (TextView) convertView.findViewById(R.id.nombreComunidad_view);
            mDescripcionView = (TextView) convertView.findViewById(R.id.incid_descripcion_view);
            mFechaAltaView = (TextView) convertView.findViewById(R.id.incid_fecha_alta_view);
            mAmbitoView = (TextView) convertView.findViewById(R.id.incid_ambito_view);
            mImportanciaUserView = (TextView) convertView.findViewById(R.id.incid_importancia_user_view);
            mImportanciaComuView = (TextView) convertView.findViewById(R.id.incid_importancia_comunidad_view);
        }

        void initializeTextInViews(IncidUserComu incidUserComu)
        {
            Log.d(TAG, "initializeTextInViews()");
            mNombreComunidadView.setText(incidUserComu.getUsuarioComunidad().getComunidad().getNombreComunidad());
            mDescripcionView.setText(incidUserComu.getIncidencia().getDescripcion());
            mFechaAltaView.setText(UIutils.formatTimeStampToString(incidUserComu.getFechaAlta()));
            short ambitoPk = incidUserComu.getIncidencia().getAmbitoIncidencia().getAmbitoId();
            mAmbitoView.setText(new IncidenciaDataDbHelper(getContext()).getAmbitoDescByPk(ambitoPk));
            mImportanciaUserView.setText(incidUserComu.getImportancia());
            // TODO: mImportanciaComunidadView.
        }
    }
}
