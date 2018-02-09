package com.didekindroid.router;

import android.app.Activity;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.incidencia.comment.IncidCommentRegAc;
import com.didekindroid.incidencia.comment.IncidCommentSeeAc;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditAc;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.router.RouterTo;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegUserComuAc;

/**
 * User: pedro@didekin
 * Date: 05/02/2018
 * Time: 16:37
 */
public enum LeadRouter implements RouterTo {

    // General defaults.
    defaultAcForRegUser(LoginAc.class),
    defaultAcForNoRegUser(ComuSearchAc.class),
    // Search comunidad.
    comunidadFound_several(ComuSearchResultsAc.class),
    comunidadFound_editUserComu(UserComuDataAc.class),
    comunidadFound_regUserComu(RegUserComuAc.class),
    comunidadFound_noRegUser(RegUserAndUserComuAc.class),
    noComunidadFound_regComuUserComu(RegComuAndUserComuAc.class),
    noComunidadFound_noRegUser(RegComuAndUserAndUserComuAc.class),
    // Comunidad
    afterMofiedComunidad(SeeUserComuByUserAc.class),
    // Usuario
    afterLogin(SeeUserComuByUserAc.class),
    afterModifiedUser(SeeUserComuByUserAc.class),
    // UsuarioComunidad.
    afterRegComuAndUserComu(SeeUserComuByUserAc.class),
    afterRegUserComu(afterRegComuAndUserComu.activityToGo),
    userComuItemSelected(UserComuDataAc.class),
    afterModifiedUserComu(SeeUserComuByUserAc.class),
    newComunAndUserComu(noComunidadFound_regComuUserComu.activityToGo),
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
    // Incidencia comment.
    afterRegComment(IncidCommentSeeAc.class),
    // Resolución.
    afterResolucionReg(IncidEditAc.class),
    regResolucion(IncidResolucionRegAc.class),
    editResolucion(IncidResolucionEditAc.class),
    modifyResolucion(IncidEditAc.class),
    modifyResolucionError(IncidSeeByComuAc.class),
    closeIncidencia(IncidSeeByComuAc.class),;

    final Class<? extends Activity> activityToGo;

    LeadRouter(Class<? extends Activity> activityToGo)
    {
        this.activityToGo = activityToGo;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return activityToGo;
    }
}
