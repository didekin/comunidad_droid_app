package com.didekindroid.usuario.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.Comunidad;

/**
 * User: pedro@didekin
 * Date: 12/05/15
 * Time: 17:22
 */
public class ComuListAdapter extends ArrayAdapter<Comunidad> {

    private static final String TAG = "ComuListAdapter";

    public ComuListAdapter(Context context)
    {
        super(context, R.layout.comu_list_adapter_view, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView(), position= " + position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ComunidadViewHolder viewHolder;

        if (convertView == null) {

            Log.d(TAG, "getView(), convertView == null");

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.comu_list_adapter_view, parent, false);
            viewHolder = doComunidadViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ComunidadViewHolder) convertView.getTag();
        final Comunidad comunidad = getItem(position);
        initializeComuViewHolder(viewHolder, comunidad);

        return convertView;
    }

    static void initializeComuViewHolder(ComunidadViewHolder viewHolder, Comunidad comunidad)
    {
        viewHolder.mTipoViaView.setText(comunidad.getTipoVia());
        viewHolder.mNombreViaView.setText(comunidad.getNombreVia());
        viewHolder.mNumeroEnViaView
                .setText(String.valueOf(comunidad.getNumero()) + " " + comunidad.getSufijoNumero());
        viewHolder.mMunicipioView.setText(comunidad.getMunicipio().getNombre());
        viewHolder.mProvinciaView.setText(comunidad.getMunicipio().getProvincia().getNombre());
    }

    static ComunidadViewHolder doComunidadViewHolder(View convertView)
    {
        ComunidadViewHolder viewHolder;
        viewHolder = new ComunidadViewHolder();
        viewHolder.mNombreViaView = (TextView) convertView.findViewById(R.id.nombreVia_view);
        viewHolder.mMunicipioView = (TextView) convertView.findViewById(R.id.municipio_view);
        viewHolder.mNumeroEnViaView = (TextView) convertView.findViewById(R.id.numeroEnVia_view);
        viewHolder.mTipoViaView = (TextView) convertView.findViewById(R.id.tipoVia_view);
        viewHolder.mProvinciaView = (TextView) convertView.findViewById(R.id.provincia_view);
        return viewHolder;
    }


    // ......... Inner classes .................

    public static class ComunidadViewHolder {

        TextView mNombreViaView;
        TextView mNumeroEnViaView;
        TextView mTipoViaView;
        TextView mMunicipioView;
        TextView mProvinciaView;
    }
}
