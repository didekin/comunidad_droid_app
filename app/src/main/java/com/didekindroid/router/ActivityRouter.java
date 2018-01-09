package com.didekindroid.router;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.didekindroid.R;
import com.didekindroid.accesorio.ConfidencialidadAc;
import com.didekindroid.api.router.ActivityRouterIf;
import com.didekindroid.api.router.IntrospectRouterToAcIf;
import com.didekindroid.comunidad.ComuDataAc;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.incidencia.comment.IncidCommentRegAc;
import com.didekindroid.incidencia.comment.IncidCommentSeeAc;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditAc;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
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
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.defaultNoRegUser;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.defaultRegUser;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.writeNewComment;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.writeNewIncidencia;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 16:28
 */

public class ActivityRouter implements ActivityRouterIf {

    // Singleton instance.
    public static final ActivityRouter acRouter = new ActivityRouter();
    static final Map<Class<? extends Activity>, Class<? extends Activity>> acRouterMap = new ArrayMap<>();
    private static final Map<Integer, Class<? extends Activity>> menuIdMap = new ArrayMap<>();
    private static final Map<Integer, Class<? extends Activity>> noUserRegMenuIdMap = new ArrayMap<>();

    // Activity --> activity mapping (usually the default or the only option).
    static {
        acRouterMap.put(ComuDataAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(ComuSearchAc.class, ComuSearchResultsAc.class);
        acRouterMap.put(DeleteMeAc.class, ComuSearchAc.class);
        acRouterMap.put(IncidCommentRegAc.class, IncidCommentSeeAc.class);
        acRouterMap.put(LoginAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(RegComuAndUserComuAc.class, SeeUserComuByUserAc.class);
        acRouterMap.put(RegUserComuAc.class, SeeUserComuByUserAc.class);
    }

    // Menu options --> activity mapping
    static {
        // ACCESORIO
        menuIdMap.put(R.id.confidencialidad_ac_mn, ConfidencialidadAc.class);
        // INCIDENCIAS.
        menuIdMap.put(R.id.incid_comment_reg_ac_mn, writeNewComment.activityToGo);
        menuIdMap.put(R.id.incid_comments_see_ac_mn, IncidCommentSeeAc.class);
        menuIdMap.put(R.id.incid_reg_ac_mn, writeNewIncidencia.activityToGo);
        menuIdMap.put(R.id.incid_see_closed_by_comu_ac_mn, IncidSeeByComuAc.class);
        menuIdMap.put(R.id.incid_see_open_by_comu_ac_mn, IncidSeeByComuAc.class);
        // USUARIO.
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

    final IdentityCacher identityCacher;

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
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_NEW_TASK);
        navigateUpTo(activity, intent);
    }

    @SuppressWarnings("unused")
    public static void doUpInFragment(Activity activity)
    {
        Timber.d("doUpInFragment()");
        if (activity.getFragmentManager().getBackStackEntryCount() > 0) {
            activity.getFragmentManager().popBackStack();
        }
    }

    @Override
    public Class<? extends Activity> nextActivityFromMn(int resourceId)
    {
        boolean isRegistered = identityCacher.isRegisteredUser();
        Timber.d("nextActivityFromMn(), isRegisteredUser = %b", isRegistered);

        if (isRegistered) {
            return menuIdMap.get(resourceId) != null ? menuIdMap.get(resourceId) : defaultRegUser.activityToGo;
        } else {
            Class<? extends Activity> activityClass = noUserRegMenuIdMap.get(resourceId);
            if (activityClass == null) {
                activityClass = menuIdMap.get(resourceId) != null ? menuIdMap.get(resourceId) : defaultNoRegUser.activityToGo;
            }
            return activityClass;
        }
    }

    @Override
    public Class<? extends Activity> nextActivity(Class<? extends Activity> previousActivity)
    {
        Timber.d("nextActivity()");
        Class<? extends Activity> acClass = acRouterMap.get(previousActivity);
        if (acClass != null) {
            return acClass;
        }
        return identityCacher.isRegisteredUser() ? defaultRegUser.activityToGo : defaultNoRegUser.activityToGo;
    }

    /* =========================  HELPERS CLASSES  =========================*/

    public enum IntrospectRouterToAc implements IntrospectRouterToAcIf {

        // General defaults.
        defaultRegUser(LoginAc.class),
        defaultNoRegUser(ComuSearchAc.class),
        // Search comunidad.
        comunidadFound_regUserComu(UserComuDataAc.class),
        comunidadFound_regUser(RegUserComuAc.class),
        comunidadFound_noRegUser(RegUserAndUserComuAc.class),
        noComunidadFound_regUser(RegComuAndUserComuAc.class),
        noComunidadFound_noRegUser(RegComuAndUserAndUserComuAc.class),
        // Usuario
        afterModifiedUserAlias(SeeUserComuByUserAc.class),
        // UsuarioComunidad.
        userComuItemSelected(UserComuDataAc.class),
        afterModifiedUserComu(SeeUserComuByUserAc.class),
        // Password.
        modifyPswd(SeeUserComuByUserAc.class),
        sendNewPswd(LoginAc.class),
        afterClickPswdSentDialog(LoginAc.class),
        // Incidencia
        writeNewComment(IncidCommentRegAc.class),
        writeNewIncidencia(IncidRegAc.class),
        selectedOpenIncid(IncidEditAc.class),
        selectedClosedIncid(IncidResolucionEditAc.class),
        erasedOpenIncid(IncidSeeByComuAc.class),
        modifiedOpenIncid(IncidSeeByComuAc.class),
        afterRegNewIncid(IncidSeeByComuAc.class),
        // Resolución.
        afterResolucionReg(IncidEditAc.class),
        regResolucion(IncidResolucionRegAc.class),
        editResolucion(IncidResolucionEditAc.class),
        modifyResolucion(IncidEditAc.class),
        modifyResolucionError(IncidSeeByComuAc.class),
        closeIncidencia(IncidSeeByComuAc.class),
        errorClosingIncid(IncidSeeByComuAc.class),;

        final Class<? extends Activity> activityToGo;

        IntrospectRouterToAc(Class<? extends Activity> activityToGo)
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