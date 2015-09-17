package com.didekindroid.usuario.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 12/05/15
 * Time: 17:22
 */
public class ComuSearchResultsListAdapter extends ArrayAdapter<Comunidad> {

    private static final String TAG = ComuSearchResultsListAdapter.class.getCanonicalName();

    public ComuSearchResultsListAdapter(Context context)
    {
        super(context, R.layout.comu_list_item_view, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView(), position= " + position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ComuViewHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comu_list_item_view, parent, false);
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

        private static final String TAG = ComuViewHolder.class.getCanonicalName();

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
            Log.d(TAG, "initializeTextInViews()");

            mNombreComunidadView.setText(comunidad.getNombreComunidad());
            mMunicipioView.setText(comunidad.getMunicipio().getNombre());
            mProvinciaView.setText(comunidad.getMunicipio().getProvincia().getNombre());
        }
    }
}
