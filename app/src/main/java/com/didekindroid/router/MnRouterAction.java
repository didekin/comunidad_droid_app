package com.didekindroid.router;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.R;
import com.didekindroid.accesorio.ConfidencialidadAc;
import com.didekindroid.comunidad.ComuDataAc;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.comment.IncidCommentSeeAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.usuario.delete.DeleteMeAc;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuario.password.PasswordChangeAc;
import com.didekindroid.usuario.userdata.UserDataAc;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static android.support.v4.app.NavUtils.getParentActivityIntent;
import static android.support.v4.app.NavUtils.navigateUpTo;
import static android.support.v4.app.NavUtils.shouldUpRecreateTask;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.router.LeadRouter.defaultAcForNoRegUser;
import static com.didekindroid.router.LeadRouter.defaultAcForRegUser;
import static com.didekindroid.router.LeadRouter.writeNewComment;
import static com.didekindroid.router.LeadRouter.writeNewIncidencia;

/**
 * User: pedro@didekin
 * Date: 05/02/2018
 * Time: 17:02
 */

public enum MnRouterAction implements RouterActionIf {

    // UP.
    navigateUp(android.R.id.home, null) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            Timber.d("doUpMenuActivity()");
            // To check if the user presses the Up button after entering your activity from another app's task.
            if (shouldUpRecreateTask(activity, getParentActivityIntent(activity))) {
                Intent intent =
                        new Intent(
                                activity,
                                TKhandler.isRegisteredUser() ? defaultAcForRegUser.activityToGo : defaultAcForNoRegUser.activityToGo
                        );
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                return;
            }

            Intent intent = getParentActivityIntent(activity);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_NEW_TASK);
            navigateUpTo(activity, intent);
        }
    },
    // ACCESORIOS.
    confidencialidad_mn(R.id.confidencialidad_ac_mn, ConfidencialidadAc.class),
    // INCIDENCIAS.
    incid_comment_reg_mn(R.id.incid_comment_reg_ac_mn, writeNewComment.activityToGo),
    incid_comments_see_mn(R.id.incid_comments_see_ac_mn, IncidCommentSeeAc.class),
    incid_reg_mn(R.id.incid_reg_ac_mn, writeNewIncidencia.activityToGo),
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
    // USUARIO.
    delete_me_mn(R.id.delete_me_ac_mn, DeleteMeAc.class),
    login_mn(R.id.login_ac_mn, LoginAc.class),
    password_change_mn(R.id.password_change_ac_mn, PasswordChangeAc.class),
    user_data_mn(R.id.user_data_ac_mn, UserDataAc.class),
    // USUARIO_COMUNIDAD.
    reg_nueva_comunidad_mn(R.id.reg_nueva_comunidad_ac_mn, RegComuAndUserComuAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            Timber.d("initActivity(), reg_nueva_comunidad_mn");
            // for not registered users
            if (!TKhandler.isRegisteredUser()) {
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
    public static final Map<Integer, MnRouterAction> resourceIdToMnItem = new HashMap<>();

    static {
        for (MnRouterAction menuItem : values()) {
            resourceIdToMnItem.put(menuItem.mnItemRsId, menuItem);
        }
    }

    // ==========================  Instance members ============================

    private final int mnItemRsId;
    private final Class<? extends Activity> acToGo;

    MnRouterAction(int menuItemRsIdIn, Class<? extends Activity> classToGo)
    {
        mnItemRsId = menuItemRsIdIn;
        acToGo = classToGo;
    }

    public int getMnItemRsId()
    {
        return mnItemRsId;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return acToGo;
    }
}
