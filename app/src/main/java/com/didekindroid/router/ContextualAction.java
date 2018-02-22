package com.didekindroid.router;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.lib_one.util.MuteActivity;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.incidencia.comment.IncidCommentRegAc;
import com.didekindroid.incidencia.comment.IncidCommentSeeAc;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditAc;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.router.ContextualNameIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.usuario.LoginAc;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.PasswordSentDialog;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserComuAc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuContextualName.comu_data_just_modified;
import static com.didekindroid.comunidad.util.ComuContextualName.found_comu_plural;
import static com.didekindroid.comunidad.util.ComuContextualName.found_comu_single_for_current_neighbour;
import static com.didekindroid.comunidad.util.ComuContextualName.found_comu_single_for_current_user;
import static com.didekindroid.comunidad.util.ComuContextualName.found_comu_single_for_no_reg_user;
import static com.didekindroid.comunidad.util.ComuContextualName.new_comu_usercomu_just_registered;
import static com.didekindroid.comunidad.util.ComuContextualName.new_usercomu_just_registered;
import static com.didekindroid.comunidad.util.ComuContextualName.no_found_comu_for_current_user;
import static com.didekindroid.comunidad.util.ComuContextualName.no_found_comu_for_no_reg_user;
import static com.didekindroid.comunidad.util.ComuContextualName.to_reg_new_comu_usercomu;
import static com.didekindroid.comunidad.util.ComuContextualName.usercomu_just_deleted;
import static com.didekindroid.comunidad.util.ComuContextualName.usercomu_just_modified;
import static com.didekindroid.comunidad.util.ComuContextualName.usercomu_just_selected;
import static com.didekindroid.incidencia.IncidContextualName.after_incid_resolucion_modif_error;
import static com.didekindroid.incidencia.IncidContextualName.incid_closed_just_selected;
import static com.didekindroid.incidencia.IncidContextualName.incid_open_just_closed;
import static com.didekindroid.incidencia.IncidContextualName.incid_open_just_modified;
import static com.didekindroid.incidencia.IncidContextualName.incid_open_just_selected;
import static com.didekindroid.incidencia.IncidContextualName.incid_resolucion_just_modified;
import static com.didekindroid.incidencia.IncidContextualName.incidencia_just_erased;
import static com.didekindroid.incidencia.IncidContextualName.new_incid_comment_just_registered;
import static com.didekindroid.incidencia.IncidContextualName.new_incid_resolucion_just_registered;
import static com.didekindroid.incidencia.IncidContextualName.new_incidencia_just_registered;
import static com.didekindroid.incidencia.IncidContextualName.to_edit_incid_resolucion;
import static com.didekindroid.incidencia.IncidContextualName.to_register_new_incid_comment;
import static com.didekindroid.incidencia.IncidContextualName.to_register_new_incid_resolucion;
import static com.didekindroid.incidencia.IncidContextualName.to_register_new_incidencia;
import static com.didekindroid.lib_one.usuario.UserContextualName.default_no_reg_user;
import static com.didekindroid.lib_one.usuario.UserContextualName.default_reg_user;
import static com.didekindroid.lib_one.usuario.UserContextualName.login_just_done;
import static com.didekindroid.lib_one.usuario.UserContextualName.new_comu_user_usercomu_just_registered;
import static com.didekindroid.lib_one.usuario.UserContextualName.new_user_usercomu_just_registered;
import static com.didekindroid.lib_one.usuario.UserContextualName.pswd_just_modified;
import static com.didekindroid.lib_one.usuario.UserContextualName.pswd_just_sent_to_user;
import static com.didekindroid.lib_one.usuario.UserContextualName.user_alias_just_modified;
import static com.didekindroid.lib_one.usuario.UserContextualName.user_just_deleted;
import static com.didekindroid.lib_one.usuario.UserContextualName.user_name_just_modified;
import static com.didekindroid.lib_one.usuario.UsuarioAssertionMsg.user_name_should_be_initialized;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static java.util.EnumSet.of;

/**
 * User: pedro@didekin
 * Date: 05/02/2018
 * Time: 16:37
 */
public enum ContextualAction implements RouterActionIf {

    // Comunidad.
    showComuFound(of(found_comu_plural), ComuSearchResultsAc.class),
    searchForComu(of(
            default_no_reg_user,
            user_just_deleted), ComuSearchAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle, int flags)
        {
            super.initActivity(activity, bundle, flags);
            activity.finish();
        }
    },
    // UsuarioComunidad.
    editCurrentUserComu(of(
            found_comu_single_for_current_neighbour,
            usercomu_just_selected), UserComuDataAc.class),
    regNewUserComu(of(found_comu_single_for_current_user), RegUserComuAc.class),
    regNewUser(of(found_comu_single_for_no_reg_user), RegUserAndUserComuAc.class),
    regNewComuAndUserComu(of(
            no_found_comu_for_current_user,
            to_reg_new_comu_usercomu), RegComuAndUserComuAc.class),
    regNewComuAndNewUser(of(no_found_comu_for_no_reg_user), RegComuAndUserAndUserComuAc.class),
    seeUserComuByUser_fromComu(of(
            comu_data_just_modified,
            new_comu_usercomu_just_registered,
            new_usercomu_just_registered,
            usercomu_just_modified,
            usercomu_just_deleted), SeeUserComuByUserAc.class),
    seeUserComuByUser_fromUser(of(
            login_just_done,
            user_alias_just_modified,
            pswd_just_modified), SeeUserComuByUserAc.class),
    // Usuario
    login_from_default(of(default_reg_user), LoginAc.class),
    login_from_user(of(pswd_just_sent_to_user), LoginAc.class),
    showPswdSentMessage(of(
            new_comu_user_usercomu_just_registered,
            new_user_usercomu_just_registered,
            user_name_just_modified), MuteActivity.class) {
        @Override
        public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle, int flags)
        {
            Timber.d("initActivity(), three params.");
            assertTrue(bundle != null
                    && bundle.getSerializable(usuario_object.key) != null, user_name_should_be_initialized);
            DialogFragment newFragment = PasswordSentDialog.newInstance(bundle);
            newFragment.show(activity.getFragmentManager(), "passwordMailDialog");
        }
    },
    // Incidencia.comment
    regNewComment(of(to_register_new_incid_comment), IncidCommentRegAc.class),
    seeIncidComment(of(new_incid_comment_just_registered), IncidCommentSeeAc.class),
    // Incidencia.
    seeIncidByComu(of(
            new_incidencia_just_registered,
            incid_open_just_modified,
            incidencia_just_erased,
            incid_open_just_closed,
            after_incid_resolucion_modif_error), IncidSeeByComuAc.class),
    regNewIncidencia(of(to_register_new_incidencia), IncidRegAc.class),
    editIncidenciaOpen(of(
            incid_open_just_selected,
            new_incid_resolucion_just_registered,
            incid_resolucion_just_modified), IncidEditAc.class),
    regNewIncidResolucion(of(to_register_new_incid_resolucion), IncidResolucionRegAc.class),
    seeIncidResolucion(of(
            incid_closed_just_selected,
            to_edit_incid_resolucion), IncidResolucionEditAc.class),;

    // ==========================  Static members ============================

    static final Map<ContextualNameIf, ContextualAction> contextualAcMap = new HashMap<>(values().length * 3);

    static {
        for (ContextualAction action : values()) {
            for (ContextualNameIf contextualName : action.contextualNmSet) {
                contextualAcMap.put(contextualName, action);
            }
        }
    }


    /* ==========================  Instance members ============================*/

    private final Set<? extends ContextualNameIf> contextualNmSet;
    private final Class<? extends Activity> activityToGo;

    ContextualAction(Set<? extends ContextualNameIf> contextualNmSet, Class<? extends Activity> activityToGo)
    {
        this.contextualNmSet = contextualNmSet;
        this.activityToGo = activityToGo;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return activityToGo;
    }
}