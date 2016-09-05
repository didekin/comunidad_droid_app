package com.didekindroid.usuario.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/05/15
 * Time: 17:22
 */
public class ComuSearchResultsListAdapter extends ArrayAdapter<Comunidad> {

    public ComuSearchResultsListAdapter(Context context)
    {
        super(context, R.layout.comu_include, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getView(), position= %d%n", position);

        ComuViewHolder viewHolder;

        if (convertView == null) {
            Timber.d("getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comu_include, parent, false);
            viewHolder = new ComuViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ComuViewHolder) convertView.getTag();
        final Comunidad comunidad = getItem(position);
        viewHolder.initializeTextInViews(comunidad);

        return convertView;
    }

    // ......... Inner classes .................

    static class ComuViewHolder {

        TextView mNombreComunidadView;
        TextView mMunicipioView;
        TextView mProvinciaView;

        public ComuViewHolder(View convertView)
        {
            mNombreComunidadView = (TextView) convertView.findViewById(R.id.nombreComunidad_view);
            mMunicipioView = (TextView) convertView.findViewById(R.id.municipio_view);
            mProvinciaView = (TextView) convertView.findViewById(R.id.provincia_view);
        }

        void initializeTextInViews(Comunidad comunidad)
        {
            Timber.d("initializeTextInViews()");

            mNombreComunidadView.setText(comunidad.getNombreComunidad());
            mMunicipioView.setText(comunidad.getMunicipio().getNombre());
            mProvinciaView.setText(comunidad.getMunicipio().getProvincia().getNombre());
        }
    }
}
