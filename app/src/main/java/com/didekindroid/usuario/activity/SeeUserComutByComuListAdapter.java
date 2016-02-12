package com.didekindroid.usuario.activity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;

import static com.didekindroid.usuario.activity.utils.RolCheckBox.getResourceStringId;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 17:50
 */
public class SeeUserComutByComuListAdapter extends ArrayAdapter<UsuarioComunidad> {

    private static final String TAG = SeeUserComutByComuListAdapter.class.getCanonicalName();

    public SeeUserComutByComuListAdapter(Context context)
    {
        super(context, R.layout.user_usercomu_list_item, R.id.usercomu_item_portal_rot);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView(), position= " + position);

        UserAndUserComuVwHolder viewHolder;

        if (convertView == null) {
            Log.d(TAG, "getView(), convertView == null");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_usercomu_list_item, parent, false);
            viewHolder = new UserAndUserComuVwHolder(convertView, getContext().getResources());
            convertView.setTag(viewHolder);
        }

        viewHolder = (UserAndUserComuVwHolder) convertView.getTag();
        final UsuarioComunidad userComu = getItem(position);
        viewHolder.initializeTextInViews(userComu);

        return convertView;
    }

    // ......... Inner classes .................

    static class UserVwHolder {

        static final String TAG = UserVwHolder.class.getCanonicalName();

        final TextView mUserName;
        final TextView mUserAlias;

        UserVwHolder(View convertView)
        {
            mUserName = (TextView) convertView.findViewById(R.id.usercomu_item_username_txt);
            mUserAlias = (TextView) convertView.findViewById(R.id.usercomu_item_alias_txt);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Log.d(TAG, "initializeTextInViews()");

            mUserName.setText(userComu.getUsuario().getUserName());
            mUserAlias.setText(userComu.getUsuario().getAlias());
        }
    }

    static class UserComuVwHolder {

        static final String TAG = UserComuVwHolder.class.getCanonicalName();

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
            Log.d(TAG, "initializeTextInViews()");

            if (userComu.getPortal() != null && !userComu.getPortal().isEmpty()) {
                mPortalView.setText(userComu.getPortal());
            }

            if (userComu.getEscalera() != null && !userComu.getEscalera().isEmpty()) {
                mEscaleraView.setText(userComu.getEscalera());
            }

            if (userComu.getPlanta() != null && !userComu.getPlanta().isEmpty()) {
                mPlantaView.setText(userComu.getPlanta());
            }

            if (userComu.getPuerta() != null && !userComu.getPuerta().isEmpty()) {
                mPuertaView.setText(userComu.getPuerta());
            }

            mRolesView.setText(formatRol(userComu.getRoles()));
        }

        private String formatRol(String rolesString)
        {
            Log.d(TAG, "formatRol()");

            String[] rolesPieces = rolesString.split(",");
            StringBuilder builder = new StringBuilder();
            String resourceString;

            for (String rolesPiece : rolesPieces) {
                resourceString = resources.getString(getResourceStringId(rolesPiece));
                builder.append(resourceString).append(", ");
            }
            builder.deleteCharAt(builder.length() - 2);
            return builder.toString().trim();
        }
    }

    static class UserAndUserComuVwHolder {

        static final String TAG = UserAndUserComuVwHolder.class.getCanonicalName();

        final UserVwHolder userVwHolder;
        final UserComuVwHolder userComuVwHolder;

        public UserAndUserComuVwHolder(View convertView, Resources resources)
        {
            userVwHolder = new UserVwHolder(convertView);
            userComuVwHolder = new UserComuVwHolder(convertView, resources);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Log.d(TAG, "initializeTextInViews()");

            userVwHolder.initializeTextInViews(userComu);
            userComuVwHolder.initializeTextInViews(userComu);
        }
    }
}
