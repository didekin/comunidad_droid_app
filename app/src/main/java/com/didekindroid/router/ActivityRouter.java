package com.didekindroid.router;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.didekindroid.R;
import com.didekindroid.accesorio.ConfidencialidadAc;
import com.didekindroid.api.RouterListener;
import com.didekindroid.comunidad.ComuDataAc;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.incidencia.comment.IncidCommentRegAc;
import com.didekindroid.incidencia.comment.IncidCommentSeeAc;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.close.IncidSeeClosedByComuAc;
import com.didekindroid.incidencia.list.open.IncidSeeOpenByComuAc;
import com.didekindroid.incidencia.resolucion.IncidResolucionRegEditSeeAc;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.usuario.delete.DeleteMeAc;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuario.password.PasswordChangeAc;
import com.didekindroid.usuario.userdata.UserDataAc;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserComuAc;

import java.util.Map;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static android.support.v4.app.NavUtils.getParentActivityIntent;
import static android.support.v4.app.NavUtils.navigateUpTo;
import static android.support.v4.app.NavUtils.shouldUpRecreateTask;
import static com.didekindroid.router.ActivityRouter.RouterToActivity.defaultNoRegUser;
import static com.didekindroid.router.ActivityRouter.RouterToActivity.defaultRegUser;
import static com.didekindroid.router.ActivityRouter.RouterToActivity.writeNewComment;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 16:28
 */

public class ActivityRouter implements ActivityRouterIf {

    // Singleton instance.
    static final ActivityRouter acRouter = new ActivityRouter();

    private static final Map<Integer, Class<? extends Activity>> menuIdMap = new ArrayMap<>();
    private static final Map<Integer, Class<? extends Activity>> noUserRegMenuIdMap = new ArrayMap<>();
    private static final Map<Class<? extends Activity>, Class<? extends Activity>> acRouterMap = new ArrayMap<>();

    // Activity --> activity mapping (usually the default or the only option).
    static {
        acRouterMap.put(ComuDataAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(ComuSearchAc.class, ComuSearchResultsAc.class);
        acRouterMap.put(DeleteMeAc.class, ComuSearchAc.class);
        acRouterMap.put(IncidCommentRegAc.class, IncidCommentSeeAc.class);
        acRouterMap.put(IncidEditAc.class, IncidSeeOpenByComuAc.class);
        acRouterMap.put(IncidRegAc.class, IncidSeeOpenByComuAc.class);
        acRouterMap.put(IncidSeeOpenByComuAc.class, IncidEditAc.class);
        acRouterMap.put(LoginAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(RegComuAndUserAndUserComuAc.class, LoginAc.class);
        acRouterMap.put(RegComuAndUserComuAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(RegUserAndUserComuAc.class, LoginAc.class);
        acRouterMap.put(RegUserComuAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(UserComuDataAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(UserDataAc.class, SeeUserComuByUserAc.class);
    }

    // Menu options --> activity mapping
    static {
        // ACCESORIO
        menuIdMap.put(R.id.confidencialidad_ac_mn, ConfidencialidadAc.class);
        // INCIDENCIAS.
        menuIdMap.put(R.id.incid_comment_reg_ac_mn, writeNewComment.activityToGo);
        menuIdMap.put(R.id.incid_comments_see_ac_mn, IncidCommentSeeAc.class);
        menuIdMap.put(R.id.incid_reg_ac_mn, IncidRegAc.class);
        menuIdMap.put(R.id.incid_resolucion_reg_ac_mn, IncidResolucionRegEditSeeAc.class);
        menuIdMap.put(R.id.incid_see_closed_by_comu_ac_mn, IncidSeeClosedByComuAc.class);
        menuIdMap.put(R.id.incid_see_open_by_comu_ac_mn, IncidSeeOpenByComuAc.class);
        // USUARIO REGISTRADO.
        menuIdMap.put(R.id.comu_data_ac_mn, ComuDataAc.class);
        menuIdMap.put(R.id.comu_search_ac_mn, ComuSearchAc.class);
        menuIdMap.put(R.id.delete_me_ac_mn, DeleteMeAc.class);
        menuIdMap.put(R.id.login_ac_mn, LoginAc.class);
        menuIdMap.put(R.id.password_change_ac_mn, PasswordChangeAc.class);
        menuIdMap.put(R.id.reg_nueva_comunidad_ac_mn, RegComuAndUserComuAc.class);
        menuIdMap.put(R.id.see_usercomu_by_comu_ac_mn, SeeUserComuByComuAc.class);
        menuIdMap.put(R.id.see_usercomu_by_user_ac_mn, SeeUserComuByUserAc.class);
        menuIdMap.put(R.id.user_data_ac_mn, UserDataAc.class);

        /* USUARIO NO REGISTRADO.*/
        noUserRegMenuIdMap.put(R.id.reg_nueva_comunidad_ac_mn, RegComuAndUserAndUserComuAc.class);
    }

    // =====================  Instance fields and methods ======================

    private final IdentityCacher identityCacher;

    public ActivityRouter()
    {
        this(TKhandler);
    }

    public ActivityRouter(IdentityCacher identityCacher)
    {
        this.identityCacher = identityCacher;
    }

    @SuppressWarnings("ConstantConditions")
    public static void doUpMenu(@NonNull Activity activity)
    {
        Timber.d("doUpMenu()");
        // To check if the user presses the Up button after entering your activity from another app's task.
        if (shouldUpRecreateTask(activity, getParentActivityIntent(activity))) {
            Intent intent =
                    new Intent(activity, acRouter.identityCacher.isRegisteredUser() ? defaultRegUser.activityToGo : defaultNoRegUser.activityToGo);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            return;
        }

        Intent intent = getParentActivityIntent(activity);
        // We need both flags to reuse the parent activity.
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        activity.setIntent(intent);
        // Using navigateUpTo() is suitable only when your app is the owner of the current task (the user began this task from your app)
        navigateUpTo(activity, intent);
    }

    @Override
    public Class<? extends Activity> nextActivityFromMn(int resourceId)
    {
        boolean isRegistered = identityCacher.isRegisteredUser();
        Timber.d("nextActivityFromMn(), isRegisteredUser = %b", isRegistered);

        if (isRegistered) {
            return menuIdMap.get(resourceId) != null ? menuIdMap.get(resourceId) : defaultRegUser.activityToGo;
        } else {
            return noUserRegMenuIdMap.get(resourceId) != null ? noUserRegMenuIdMap.get(resourceId) : defaultNoRegUser.activityToGo;
        }
    }

    @Override
    public Class<? extends Activity> nextActivity(Class<? extends Activity> previousActivity)
    {
        Timber.d("nextActivity()");
        return acRouterMap.get(previousActivity);
    }

    /* =========================  HELPERS CLASSES  =========================*/

    public enum RouterToActivity implements RouterListener {

        // General defaults.
        defaultRegUser(LoginAc.class),
        defaultNoRegUser(ComuSearchAc.class),
        // Search comunidad.
        comunidadFound_regUserComu(UserComuDataAc.class),
        comunidadFound_regUser(RegUserComuAc.class),
        comunidadFound_noRegUser(RegUserAndUserComuAc.class),
        noComunidadFound_regUser(RegComuAndUserComuAc.class),
        noComunidadFound_noRegUser(RegComuAndUserAndUserComuAc.class),
        // Password.
        modifyPswd(SeeUserComuByUserAc.class),
        notSendNewPswd(ComuSearchAc.class),
        sendNewPswd(LoginAc.class),
        // Incidencia
        writeNewComment(IncidCommentRegAc.class),
        // Resolución.
        regResolucion(IncidEditAc.class),
        regResolucionDuplicate(regResolucion.activityToGo),
        modifyResolucion(IncidEditAc.class),
        modifyResolucionError(IncidSeeOpenByComuAc.class),
        closeIncidencia(IncidSeeClosedByComuAc.class),
        closeIncidenciaError(IncidSeeOpenByComuAc.class),;

        final Class<? extends Activity> activityToGo;

        RouterToActivity(Class<? extends Activity> activityToGo)
        {
            this.activityToGo = activityToGo;
        }

        @Override
        public Class<? extends Activity> getActivityToGo()
        {
            return activityToGo;
        }
    }
}