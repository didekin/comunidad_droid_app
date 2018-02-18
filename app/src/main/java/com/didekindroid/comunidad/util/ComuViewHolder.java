package com.didekindroid.comunidad.util;

import android.view.View;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/09/17
 * Time: 14:43
 */
public class ComuViewHolder {

    private TextView mNombreComunidadView;
    private TextView mMunicipioView;
    private TextView mProvinciaView;

    public ComuViewHolder(View convertView)
    {
        mNombreComunidadView = convertView.findViewById(R.id.nombreComunidad_view);
        mMunicipioView = convertView.findViewById(R.id.municipio_view);
        mProvinciaView = convertView.findViewById(R.id.provincia_view);
    }

    public void initializeTextInViews(Comunidad comunidad)
    {
        Timber.d("initializeTextInViews()");

        mNombreComunidadView.setText(comunidad.getNombreComunidad());
        mMunicipioView.setText(comunidad.getMunicipio().getNombre());
        mProvinciaView.setText(comunidad.getMunicipio().getProvincia().getNombre());
    }
}
