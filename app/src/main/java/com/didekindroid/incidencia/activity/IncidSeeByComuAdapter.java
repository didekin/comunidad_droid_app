package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.incidservice.domain.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 18/12/15
 * Time: 13:21
 */
public class IncidSeeByComuAdapter extends ArrayAdapter<Incidencia> {

    private static final String TAG = IncidSeeByComuAdapter.class.getCanonicalName();

    public IncidSeeByComuAdapter(Context context)
    {
        super(context, R.layout.incid_see_by_comu_list_item, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView()");
        IncidenciaViewHolder viewHolder;

        if (convertView == null){
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.incid_see_by_comu_list_item, parent, false);
            viewHolder = new IncidenciaViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (IncidenciaViewHolder) convertView.getTag();
        final Incidencia incidencia = getItem(position);
        viewHolder.initializeTextInViews(incidencia);
        return convertView;
    }

    private class IncidenciaViewHolder {
        final TextView mDescripcionView;
        final TextView mFechaAltaView;
        final TextView mAmbitoView;
        final TextView mImportanciaComuView;

        final Resources resources;

        public IncidenciaViewHolder(View convertView)
        {
            mDescripcionView = (TextView) convertView.findViewById(R.id.incid_descripcion_view);
            mFechaAltaView = (TextView) convertView.findViewById(R.id.incid_fecha_alta_view);
            mAmbitoView = (TextView) convertView.findViewById(R.id.incid_ambito_view);
            mImportanciaComuView = (TextView) convertView.findViewById(R.id.incid_importancia_comunidad_view);

            resources = convertView.getContext().getResources();
        }

        void initializeTextInViews(Incidencia incidencia)
        {
            Log.d(TAG, "initializeTextInViews()");
            mDescripcionView.setText(incidencia.getDescripcion());
            mFechaAltaView.setText(formatTimeStampToString(incidencia.getFechaAlta()));
            short ambitoPk = incidencia.getAmbitoIncidencia().getAmbitoId();
            mAmbitoView.setText(new IncidenciaDataDbHelper(getContext()).getAmbitoDescByPk(ambitoPk));
            int mImportanciaAvg = Math.round(incidencia.getImportanciaAvg());
            mImportanciaComuView.setText(resources.getStringArray(R.array.IncidImportanciaArray)[mImportanciaAvg]);
        }
    }
}
