package com.didekindroid.router;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuDataAc;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.comment.IncidCommentSeeAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.router.MnRouterActionIf;
import com.didekindroid.lib_one.usuario.UserDataAc;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.userMnItemMap;
import static com.didekindroid.router.DidekinContextAction.regNewComment;
import static com.didekindroid.router.DidekinContextAction.regNewIncidencia;


/**
 * User: pedro@didekin
 * Date: 05/02/2018
 * Time: 17:02
 */

public enum DidekinMnAction implements MnRouterActionIf {

    // INCIDENCIAS.
    incid_comment_reg_mn(R.id.incid_comment_reg_ac_mn, regNewComment.getAcToGo()),
    incid_comments_see_mn(R.id.incid_comments_see_ac_mn, IncidCommentSeeAc.class),
    incid_reg_mn(R.id.incid_reg_ac_mn, regNewIncidencia.getAcToGo()),
    incid_see_closed_by_comu_mn(R.id.incid_see_closed_by_comu_ac_mn, IncidSeeByComuAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle)
        {
            Timber.d("initActivity(), incid_see_closed_by_comu_mn");
            if (bundle == null || !bundle.containsKey(INCID_CLOSED_LIST_FLAG.key)) {
                bundle = INCID_CLOSED_LIST_FLAG.getBundleForKey(false);
            }
            super.initActivity(activity, bundle);
        }
    },
    incid_see_open_by_comu_mn(R.id.incid_see_open_by_comu_ac_mn, IncidSeeByComuAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle)
        {
            Timber.d("initActivity(), incid_see_open_by_comu_mn");
            if (bundle == null || !bundle.containsKey(INCID_CLOSED_LIST_FLAG.key)) {
                bundle = INCID_CLOSED_LIST_FLAG.getBundleForKey(true);
            }
            super.initActivity(activity, bundle);
        }
    },
    // COMUNIDAD.
    comu_data_mn(R.id.comu_data_ac_mn, ComuDataAc.class),
    comu_search_mn(R.id.comu_search_ac_mn, ComuSearchAc.class),
    // USUARIO
    user_data_mn(R.id.user_data_ac_mn, UserDataAc.class), // TODO: testar
    // USUARIO_COMUNIDAD.
    reg_nueva_comunidad_mn(R.id.reg_nueva_comunidad_ac_mn, RegComuAndUserComuAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            Timber.d("initActivity(), reg_nueva_comunidad_mn");
            // for not registered users
            if (!secInitializer.get().getTkCacher().isUserRegistered()) {
                activity.startActivity(new Intent(activity, RegComuAndUserAndUserComuAc.class));
            } else {
                super.initActivity(activity);
            }
        }
    },
    see_usercomu_by_comu_mn(R.id.see_usercomu_by_comu_ac_mn, SeeUserComuByComuAc.class),
    see_usercomu_by_user_mn(R.id.see_usercomu_by_user_ac_mn, SeeUserComuByUserAc.class),;

    // ==========================  Static members ============================

    @SuppressLint("UseSparseArrays")
    public static final Map<Integer, MnRouterActionIf> didekinMnItemMap = new HashMap<>(values().length + userMnItemMap.size());

    static {
        didekinMnItemMap.putAll(userMnItemMap);
        for (DidekinMnAction menuItem : values()) {
            didekinMnItemMap.put(menuItem.mnItemRsId, menuItem);
        }
    }

    // ==========================  Instance members ============================

    private final Class<? extends Activity> acToGo;
    private final int mnItemRsId;

    DidekinMnAction(int menuItemRsIdIn, Class<? extends Activity> classToGo)
    {
        mnItemRsId = menuItemRsIdIn;
        acToGo = classToGo;
    }

    @Override
    public int getMnItemRsId()
    {
        return mnItemRsId;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return acToGo;
    }
}
