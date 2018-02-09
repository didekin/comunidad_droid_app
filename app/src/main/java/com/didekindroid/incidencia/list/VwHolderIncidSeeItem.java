package com.didekindroid.incidencia.list;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import static com.didekindroid.lib_one.util.UIutils.formatTimeStampToString;

/**
 * User: pedro@didekin
 * Date: 23/03/16
 * Time: 14:11
 */
public class VwHolderIncidSeeItem {

    private final Context context;
    private final TextView mDescripcionView;
    private final TextView mFechaAltaView;
    private final TextView mIniciador;
    private final TextView mAmbitoView;
    private final TextView mImportanciaComuView;

    public VwHolderIncidSeeItem(View convertView)
    {
        mDescripcionView = convertView.findViewById(R.id.incid_descripcion_view);
        mFechaAltaView = convertView.findViewById(R.id.incid_fecha_alta_view);
        mIniciador = convertView.findViewById(R.id.incid_see_iniciador_view);
        mAmbitoView = convertView.findViewById(R.id.incid_ambito_view);
        mImportanciaComuView = convertView.findViewById(R.id.incid_importancia_comunidad_view);
        context = convertView.getContext();
    }

    public void initializeTextInViews(IncidenciaUser incidenciaUser)
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
