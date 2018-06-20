package com.didekindroid.router.testutil;

import com.didekindroid.R;

import static com.didekindroid.lib_one.usuario.router.UserContextAction.login_from_default;
import static com.didekindroid.lib_one.usuario.router.UserContextAction.showPswdSentMessage;
import static com.didekindroid.lib_one.usuario.router.UserContextName.default_reg_user;
import static com.didekindroid.lib_one.usuario.router.UserContextName.new_comu_user_usercomu_just_registered;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.confidencialidad_mn;
import static com.didekindroid.lib_one.usuario.router.UserMnAction.navigateUp;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.show_login_noUser;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.show_login_no_authHeader;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.show_userData_wrongMail;
import static com.didekindroid.router.DidekinContextAction.didekinContextAcMap;
import static com.didekindroid.router.DidekinMnAction.didekinMnItemMap;
import static com.didekindroid.router.DidekinUiExceptionAction.didekinExcpMsgMap;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/04/2018
 * Time: 17:17
 */
public final class UserRouterMapUtil {

    private UserRouterMapUtil()
    {
    }

    public static void checkUserExcepMsgMap()
    {
        assertThat(didekinExcpMsgMap.get(BAD_REQUEST.getHttpMessage()), is(show_login_noUser));
        assertThat(didekinExcpMsgMap.get(UNAUTHORIZED.getHttpMessage()), is(show_login_no_authHeader));
        assertThat(didekinExcpMsgMap.get(PASSWORD_NOT_SENT.getHttpMessage()), is(show_userData_wrongMail));
    }

    public static void checkUserContextActionMap()
    {
        assertThat(didekinContextAcMap.get(new_comu_user_usercomu_just_registered), is(showPswdSentMessage));
        assertThat(didekinContextAcMap.get(default_reg_user), is(login_from_default));
    }

    public static void checkUserMnActionMap()
    {
        assertThat(didekinMnItemMap.get(android.R.id.home), is(navigateUp));
        assertThat(didekinMnItemMap.get(R.id.confidenc_item_mn), is(confidencialidad_mn));
    }
}
