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
            viewHolder = initViewsInComuVwHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ComuViewHolder) convertView.getTag();
        final Comunidad comunidad = getItem(position);
        initTextsInComuVwHolder(viewHolder, comunidad);

        return convertView;
    }

    static void initTextsInComuVwHolder(ComuViewHolder viewHolder, Comunidad comunidad)
    {
        viewHolder.mTipoViaView.setText(comunidad.getTipoVia());
        viewHolder.mNombreViaView.setText(comunidad.getNombreVia());
        viewHolder.mNumeroEnViaView
                .setText(String.valueOf(comunidad.getNumero()) + " " + comunidad.getSufijoNumero());
        viewHolder.mMunicipioView.setText(comunidad.getMunicipio().getNombre());
        viewHolder.mProvinciaView.setText(comunidad.getMunicipio().getProvincia().getNombre());
    }

    static ComuViewHolder initViewsInComuVwHolder(View convertView)
    {
        ComuViewHolder viewHolder;
        viewHolder = new ComuViewHolder();
        viewHolder.mTipoViaView = (TextView) convertView.findViewById(R.id.tipoVia_view);
        viewHolder.mNombreViaView = (TextView) convertView.findViewById(R.id.nombreVia_view);
        viewHolder.mNumeroEnViaView = (TextView) convertView.findViewById(R.id.numeroEnVia_view);
        viewHolder.mMunicipioView = (TextView) convertView.findViewById(R.id.municipio_view);
        viewHolder.mProvinciaView = (TextView) convertView.findViewById(R.id.provincia_view);
        return viewHolder;
    }


    // ......... Inner classes .................

    static class ComuViewHolder {

        TextView mNombreViaView;
        TextView mNumeroEnViaView;
        TextView mTipoViaView;
        TextView mMunicipioView;
        TextView mProvinciaView;
    }
}
