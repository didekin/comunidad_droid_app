package com.didekindroid.usuariocomunidad;

import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 18:16
 */
public class UserComuVwHolder {

    private final LinearLayout mPortalEscaleraBlock;
    private final TextView mPortalRotView;
    private final TextView mPortalView;
    private final TextView mEscaleraRotView;
    private final TextView mEscaleraView;
    private final LinearLayout mPlantaPuertaBlock;
    private final TextView mPlantaRotView;
    private final TextView mPlantaView;
    private final TextView mPuertaRotView;
    private final TextView mPuertaView;
    private final TextView mRolesView;
    private final Resources resources;

    public UserComuVwHolder(View convertView, Resources resources)
    {
        mPortalEscaleraBlock = convertView.findViewById(R.id.usercomu_portal_escalera_block);
        mPlantaPuertaBlock = convertView.findViewById(R.id.usercomu_planta_puerta_block);

        mPortalRotView = convertView.findViewById(R.id.usercomu_item_portal_rot);
        mPortalView = convertView.findViewById(R.id.usercomu_item_portal_txt);
        mEscaleraRotView = convertView.findViewById(R.id.usercomu_item_escalera_rot);
        mEscaleraView = convertView.findViewById(R.id.usercomu_item_escalera_txt);
        mPlantaRotView = convertView.findViewById(R.id.usercomu_item_planta_rot);
        mPlantaView = convertView.findViewById(R.id.usercomu_item_planta_txt);
        mPuertaRotView = convertView.findViewById(R.id.usercomu_item_puerta_rot);
        mPuertaView = convertView.findViewById(R.id.usercomu_item_puerta_txt);
        mRolesView = convertView.findViewById(R.id.usercomu_item_roles_txt);

        this.resources = resources;
    }

    public void initializeTextInViews(UsuarioComunidad userComu)
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
