package com.didekindroid.usuario.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.ComuListAdapter.ComunidadViewHolder;
import com.didekindroid.usuario.comunidad.dominio.Comunidad;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;

import static com.didekindroid.usuario.activity.ComuListAdapter.doComunidadViewHolder;
import static com.didekindroid.usuario.activity.ComuListAdapter.initializeComuViewHolder;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 18:01
 */
public class ComusByUserListAdapter extends ArrayAdapter<UsuarioComunidad> {

    private static final String TAG = ComusByUserListAdapter.class.getCanonicalName();

    public ComusByUserListAdapter(Context context)
    {
        super(context, R.layout.usuariocomunidad_list_view, R.id.nombreComunidad_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView(), position= " + position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        UsuarioComunidadViewHolder usuarioComuViewHolder;

        if (convertView == null) {

            Log.d(TAG, "getView(), convertView == null");

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.usuariocomunidad_list_view, parent, false);

            usuarioComuViewHolder = new UsuarioComunidadViewHolder();
            usuarioComuViewHolder.comunidadViewHolder = doComunidadViewHolder(convertView);

            usuarioComuViewHolder.mViviendaRotView = (TextView) convertView.findViewById(R.id.usuariocomunidad_vivienda_rot);
            usuarioComuViewHolder.mPortalRotView = (TextView) convertView.findViewById(R.id.usuariocomunidad_portal_rot);
            usuarioComuViewHolder.mPortalView = (TextView) convertView.findViewById(R.id.usuariocomunidad_portal);
            usuarioComuViewHolder.mEscaleraRotView = (TextView) convertView.findViewById(R.id.usuariocomunidad_escalera_rot);
            usuarioComuViewHolder.mEscaleraView = (TextView) convertView.findViewById(R.id.usuariocomunidad_escalera);
            usuarioComuViewHolder.mPlantaRotView = (TextView) convertView.findViewById(R.id.usuariocomunidad_planta_rot);
            usuarioComuViewHolder.mPlantaView = (TextView) convertView.findViewById(R.id.usuariocomunidad_planta);
            usuarioComuViewHolder.mPuertaRotView = (TextView) convertView.findViewById(R.id.usuariocomunidad_puerta_rot);
            usuarioComuViewHolder.mPuertaView = (TextView) convertView.findViewById(R.id.usuariocomunidad_puerta);
            usuarioComuViewHolder.mRolesRotView = (TextView) convertView.findViewById(R.id.usuariocomunidad_roles_rotulo);
            usuarioComuViewHolder.mRolesView = (TextView) convertView.findViewById(R.id.usuariocomunidad_roles);

            convertView.setTag(usuarioComuViewHolder);
        }

        usuarioComuViewHolder = (UsuarioComunidadViewHolder) convertView.getTag();

        final UsuarioComunidad usuarioComunidad = getItem(position);
        final Comunidad comunidad = usuarioComunidad.getComunidad();
        ComunidadViewHolder comunidadViewHolder = usuarioComuViewHolder.comunidadViewHolder;

        initializeComuViewHolder(comunidadViewHolder, comunidad);

        if (usuarioComunidad.getPortal() != null && !usuarioComunidad.getPortal().isEmpty()) {
            usuarioComuViewHolder.mPortalView.setVisibility(View.VISIBLE);
            usuarioComuViewHolder.mPortalView.setText(usuarioComunidad.getPortal());
        }

        if (usuarioComunidad.getEscalera() != null && !usuarioComunidad.getEscalera().isEmpty()) {
            usuarioComuViewHolder.mEscaleraView.setVisibility(View.VISIBLE);
            usuarioComuViewHolder.mEscaleraView.setText(usuarioComunidad.getEscalera());
        }

        if (usuarioComunidad.getPlanta() != null && !usuarioComunidad.getPlanta().isEmpty()) {
            usuarioComuViewHolder.mPlantaView.setVisibility(View.VISIBLE);
            usuarioComuViewHolder.mPlantaView.setText(usuarioComunidad.getPlanta());
        }

        if (usuarioComunidad.getPuerta() != null && !usuarioComunidad.getPuerta().isEmpty()) {
            usuarioComuViewHolder.mPuertaView.setVisibility(View.VISIBLE);
            usuarioComuViewHolder.mPuertaView.setText(usuarioComunidad.getPuerta());
        }

        usuarioComuViewHolder.mRolesView.setText(usuarioComunidad.getRoles());

        return convertView;
    }


    // ......... Inner classes .................

    private static class UsuarioComunidadViewHolder {

        private ComunidadViewHolder comunidadViewHolder;

        public TextView mViviendaRotView;
        public TextView mPortalRotView;
        public TextView mPortalView;
        public TextView mEscaleraRotView;
        public TextView mEscaleraView;
        public TextView mPlantaRotView;
        public TextView mPlantaView;
        public TextView mPuertaRotView;
        public TextView mPuertaView;
        public TextView mRolesRotView;
        public TextView mRolesView;
    }
}
