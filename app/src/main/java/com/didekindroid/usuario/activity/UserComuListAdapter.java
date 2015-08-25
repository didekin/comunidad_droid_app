package com.didekindroid.usuario.activity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.UsuarioComunidad;

import static com.didekindroid.common.dominio.Rol.getResourceStringId;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 17:50
 */
public class UserComuListAdapter extends ArrayAdapter<UsuarioComunidad> {

    private static final String TAG = UserComuListAdapter.class.getCanonicalName();

    public UserComuListAdapter(Context context)
    {
        super(context, R.layout.usercomu_list_item_view, R.id.usercomu_item_portal_rot);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView(), position= " + position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        UserComuVwHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.usercomu_list_item_view, parent, false);
            viewHolder = new UserComuVwHolder(convertView, getContext().getResources());
            convertView.setTag(viewHolder);
        }

        viewHolder = (UserComuVwHolder) convertView.getTag();
        final UsuarioComunidad userComu = getItem(position);
        viewHolder.initializeTextInViews(userComu);

        return convertView;
    }

    // ......... Inner classes .................

    static class UserComuVwHolder {

        final TextView mPortalRotView;
        final TextView mPortalView;
        final TextView mEscaleraRotView;
        final TextView mEscaleraView;
        final TextView mPlantaRotView;
        final TextView mPlantaView;
        final TextView mPuertaRotView;
        final TextView mPuertaView;
        final TextView mRolesRotView;
        final TextView mRolesView;
        final Resources resources;


        public UserComuVwHolder(View convertView, Resources resources)
        {
            mPortalRotView = (TextView) convertView.findViewById(R.id.usercomu_item_portal_rot);
            mPortalView = (TextView) convertView.findViewById(R.id.usercomu_item_portal_txt);
            mEscaleraRotView = (TextView) convertView.findViewById(R.id.usercomu_item_escalera_rot);
            mEscaleraView = (TextView) convertView.findViewById(R.id.usercomu_item_escalera_txt);
            mPlantaRotView = (TextView) convertView.findViewById(R.id.usercomu_item_planta_rot);
            mPlantaView = (TextView) convertView.findViewById(R.id.usercomu_item_planta_txt);
            mPuertaRotView = (TextView) convertView.findViewById(R.id.usercomu_item_puerta_rot);
            mPuertaView = (TextView) convertView.findViewById(R.id.usercomu_item_puerta_txt);
            mRolesRotView = (TextView) convertView.findViewById(R.id.usercomu_item_roles_rotulo);
            mRolesView = (TextView) convertView.findViewById(R.id.usercomu_item_roles_txt);
            this.resources = resources;

        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            if (userComu.getPortal() != null && !userComu.getPortal().isEmpty()) {
                mPortalView.setVisibility(View.VISIBLE);
                mPortalView.setText(userComu.getPortal());
            }

            if (userComu.getEscalera() != null && !userComu.getEscalera().isEmpty()) {
                mEscaleraView.setVisibility(View.VISIBLE);
                mEscaleraView.setText(userComu.getEscalera());
            }

            if (userComu.getPlanta() != null && !userComu.getPlanta().isEmpty()) {
                mPlantaView.setVisibility(View.VISIBLE);
                mPlantaView.setText(userComu.getPlanta());
            }

            if (userComu.getPuerta() != null && !userComu.getPuerta().isEmpty()) {
                mPuertaView.setVisibility(View.VISIBLE);
                mPuertaView.setText(userComu.getPuerta());
            }

            mRolesView.setText(formatRol(userComu.getRoles()));
        }

        private String formatRol(String rolesString)
        {
            String[] rolesPieces = rolesString.split(",");
            StringBuilder builder = new StringBuilder();
            String resourceString;

            for (int i = 0; i < rolesPieces.length - 1; i++) {
                resourceString = resources.getString(getResourceStringId(rolesPieces[i]));
                builder.append(resourceString).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        }
    }
}
