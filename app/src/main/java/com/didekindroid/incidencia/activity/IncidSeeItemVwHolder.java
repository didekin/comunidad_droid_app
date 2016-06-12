package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekindroid.R;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 23/03/16
 * Time: 14:11
 */
public class IncidSeeItemVwHolder {

    final TextView mDescripcionView;
    final TextView mFechaAltaView;
    final TextView mIniciador;
    final TextView mAmbitoView;
    final TextView mImportanciaComuView;

    final Context context;

    public IncidSeeItemVwHolder(View convertView)
    {
        mDescripcionView = (TextView) convertView.findViewById(R.id.incid_descripcion_view);
        mFechaAltaView = (TextView) convertView.findViewById(R.id.incid_fecha_alta_view);
        mIniciador = (TextView) convertView.findViewById(R.id.incid_see_iniciador_view);
        mAmbitoView = (TextView) convertView.findViewById(R.id.incid_ambito_view);
        mImportanciaComuView = (TextView) convertView.findViewById(R.id.incid_importancia_comunidad_view);
        context = convertView.getContext();
    }

    void initializeTextInViews(IncidenciaUser incidenciaUser)
    {
        mDescripcionView.setText(incidenciaUser.getIncidencia().getDescripcion());
        mFechaAltaView.setText(formatTimeStampToString(incidenciaUser.getIncidencia().getFechaAlta()));
        mIniciador.setText(incidenciaUser.getUsuario() != null ? incidenciaUser.getUsuario().getAlias() : incidenciaUser.getIncidencia().getUserName());
        short ambitoPk = incidenciaUser.getIncidencia().getAmbitoIncidencia().getAmbitoId();

        IncidenciaDataDbHelper dbHelper = new IncidenciaDataDbHelper(context);
        mAmbitoView.setText(dbHelper.getAmbitoDescByPk(ambitoPk));
        dbHelper.close();

        int mImportanciaAvg = Math.round(incidenciaUser.getIncidencia().getImportanciaAvg());
        String importanciAvgStr = mImportanciaAvg == 0 ? "" : context.getResources().getStringArray(R.array.IncidImportanciaArray)[mImportanciaAvg];
        mImportanciaComuView.setText(importanciAvgStr);
    }
}
