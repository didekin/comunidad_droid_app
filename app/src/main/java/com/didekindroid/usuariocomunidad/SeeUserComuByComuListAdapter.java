package com.didekindroid.usuariocomunidad;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekindroid.R;

import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 17:50
 */
class SeeUserComuByComuListAdapter extends ArrayAdapter<UsuarioComunidad> {

    SeeUserComuByComuListAdapter(Context context)
    {
        super(context, R.layout.user_usercomu_list_item, R.id.usercomu_item_alias_txt);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Timber.d("getView(), position= %d%n", position);

        UserAndUserComuVwHolder viewHolder;

        if (convertView == null) {
            Timber.d("getView(), convertView == null");
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

    private static class UserVwHolder {

        final TextView mUserName;
        final TextView mUserAlias;

        UserVwHolder(View convertView)
        {
            mUserName = (TextView) convertView.findViewById(R.id.usercomu_item_username_txt);
            mUserAlias = (TextView) convertView.findViewById(R.id.usercomu_item_alias_txt);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Timber.d("initializeTextInViews()");

            mUserName.setText(userComu.getUsuario().getUserName());
            mUserAlias.setText(userComu.getUsuario().getAlias());
        }
    }

    static class UserComuVwHolder {

        final LinearLayout mPortalEscaleraBlock;
        final TextView mPortalRotView;
        final TextView mPortalView;
        final TextView mEscaleraRotView;
        final TextView mEscaleraView;
        final LinearLayout mPlantaPuertaBlock;
        final TextView mPlantaRotView;
        final TextView mPlantaView;
        final TextView mPuertaRotView;
        final TextView mPuertaView;
        final TextView mRolesView;
        final Resources resources;

        UserComuVwHolder(View convertView, Resources resources)
        {
            mPortalEscaleraBlock = (LinearLayout) convertView.findViewById(R.id.usercomu_portal_escalera_block);
            mPlantaPuertaBlock = (LinearLayout) convertView.findViewById(R.id.usercomu_planta_puerta_block);

            mPortalRotView = (TextView) convertView.findViewById(R.id.usercomu_item_portal_rot);
            mPortalView = (TextView) convertView.findViewById(R.id.usercomu_item_portal_txt);
            mEscaleraRotView = (TextView) convertView.findViewById(R.id.usercomu_item_escalera_rot);
            mEscaleraView = (TextView) convertView.findViewById(R.id.usercomu_item_escalera_txt);
            mPlantaRotView = (TextView) convertView.findViewById(R.id.usercomu_item_planta_rot);
            mPlantaView = (TextView) convertView.findViewById(R.id.usercomu_item_planta_txt);
            mPuertaRotView = (TextView) convertView.findViewById(R.id.usercomu_item_puerta_rot);
            mPuertaView = (TextView) convertView.findViewById(R.id.usercomu_item_puerta_txt);
            mRolesView = (TextView) convertView.findViewById(R.id.usercomu_item_roles_txt);

            this.resources = resources;
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Timber.d("initializeTextInViews()");

            boolean isBlockPortalEscalera = false;
            boolean isPlantaPuertaBlock = false;

            if (userComu.getPortal() == null || userComu.getPortal().isEmpty()) {
                mPortalRotView.setVisibility(GONE);
                mPortalView.setVisibility(GONE);
            } else {
                mPortalView.setText(userComu.getPortal());
                isBlockPortalEscalera = true;
            }

            if (userComu.getEscalera() == null || userComu.getEscalera().isEmpty()) {
                mEscaleraRotView.setVisibility(GONE);
                mEscaleraView.setVisibility(GONE);
            } else {
                mEscaleraView.setText(userComu.getEscalera());
                isBlockPortalEscalera = true;
            }

            if (userComu.getPlanta() == null || userComu.getPlanta().isEmpty()) {
                mPlantaRotView.setVisibility(GONE);
                mPlantaView.setVisibility(GONE);
            } else {
                mPlantaView.setText(userComu.getPlanta());
                isPlantaPuertaBlock = true;
            }

            if (userComu.getPuerta() == null || userComu.getPuerta().isEmpty()) {
                mPuertaRotView.setVisibility(GONE);
                mPuertaView.setVisibility(GONE);
            } else {
                mPuertaView.setText(userComu.getPuerta());
                isPlantaPuertaBlock = true;
            }

            mPortalEscaleraBlock.setVisibility(isBlockPortalEscalera ? VISIBLE : GONE);
            mPlantaPuertaBlock.setVisibility(isPlantaPuertaBlock ? VISIBLE : GONE);

            mRolesView.setText(RolUi.formatRolToString(userComu.getRoles(), resources));
        }
    }

    private static class UserAndUserComuVwHolder {

        final UserVwHolder userVwHolder;
        final UserComuVwHolder userComuVwHolder;

        UserAndUserComuVwHolder(View convertView, Resources resources)
        {
            userVwHolder = new UserVwHolder(convertView);
            userComuVwHolder = new UserComuVwHolder(convertView, resources);
        }

        void initializeTextInViews(UsuarioComunidad userComu)
        {
            Timber.d("initializeTextInViews()");

            userVwHolder.initializeTextInViews(userComu);
            userComuVwHolder.initializeTextInViews(userComu);
        }
    }
}
