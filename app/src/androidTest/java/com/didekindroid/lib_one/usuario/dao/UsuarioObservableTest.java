package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.TokenIdentityCacher;
import com.didekindroid.lib_one.usuario.dao.UsuarioDaoTestUtil.SendPswdCallable;
import com.didekindroid.lib_one.usuario.dao.UsuarioDaoTestUtil.SendPswdCallableError;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.didekindroid.lib_one.security.AuthDao.authDao;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.security.SecurityTestUtils.checkInitTokenCache;
import static com.didekindroid.lib_one.security.SecurityTestUtils.checkNoInitCache;
import static com.didekindroid.lib_one.security.SecurityTestUtils.checkUpdatedCacheAfterPswd;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.lib_one.usuario.dao.UsuarioObservable.deleteMeSingle;
import static com.didekindroid.lib_one.usuario.dao.UsuarioObservable.loginPswdSendSingle;
import static com.didekindroid.lib_one.usuario.dao.UsuarioObservable.loginUpdateTkCache;
import static com.didekindroid.lib_one.usuario.dao.UsuarioObservable.passwordChangeWithPswdValidation;
import static com.didekindroid.lib_one.usuario.dao.UsuarioObservable.userAliasModified;
import static com.didekindroid.lib_one.usuario.dao.UsuarioObservable.userDataSingle;
import static com.didekindroid.lib_one.usuario.dao.UsuarioObservable.userNameModified;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 12:20
 */
public class UsuarioObservableTest {

    TokenIdentityCacher tkCacher = (TokenIdentityCacher) secInitializer.get().getTkCacher();

    @Before
    public void setUp()
    {
        cleanWithTkhandler();
    }

    // ................................. DELETE USER ...............................

    @Test
    public void testGetDeleteMeSingle() throws Exception
    {
        regUserComuWithTkCache(comu_real_rodrigo);

        deleteMeSingle.test().assertResult(true);
        assertThat(tkCacher.getTokenCache().get(), nullValue());
        assertThat(tkCacher.isRegisteredUser(), is(false));
    }

    //    .................................... LOGIN .................................

    @Test
    public void test_LoginUpdateTkCache_1() throws UiException, IOException
    {
        tkCacher.updateIsRegistered(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body());
        checkNoInitCache(tkCacher); // Precondition.
        loginUpdateTkCache(user_crodrigo).test().assertResult(true);
        checkInitTokenCache(tkCacher);
        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_LoginUpdateTkCache_2() throws UiException, IOException
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        checkInitTokenCache(tkCacher); // Precondition.
        loginUpdateTkCache(new Usuario.UsuarioBuilder().userName(user_crodrigo.getUserName()).password("password_wrong").build())
                .test().assertResult(false);
        checkUpdatedCacheAfterPswd(false, tkCacher.getTokenCache().get(), tkCacher);
        cleanOptions(CLEAN_RODRIGO);
    }

    /**
     * We use a mock callable to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void test_LoginPswdSendSingle_1() throws UiException, IOException, InterruptedException
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        checkInitTokenCache(tkCacher); // Precondition.
        loginPswdSendSingle(new SendPswdCallable()).test().assertResult(true);
        // Check cache cleaning.
        checkNoInitCache(tkCacher);
        finishLoginPswdSendSingle();
    }

    /**
     * We use a mock callable to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void test_LoginPswdSendSingle_2() throws UiException, IOException, InterruptedException
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        checkInitTokenCache(tkCacher); // Precondition.
        loginPswdSendSingle(new SendPswdCallableError()).test().assertFailure(UiException.class);
        // Check cache hasn't changed.
        checkInitTokenCache(tkCacher);
        finishLoginPswdSendSingle();
    }

    // ..................................... PASSWORD ..........................................

    @Test
    public void test_PasswordChangeWithPswdValidation() throws Exception
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        final SpringOauthToken oldToken = tkCacher.getTokenCache().get();

        Usuario newUser = new Usuario.UsuarioBuilder().userName(user_crodrigo.getUserName()).password("new_password").build();
        passwordChangeWithPswdValidation(user_crodrigo, newUser).test().assertComplete();
        checkUpdatedCacheAfterPswd(true, oldToken, tkCacher);
        usuarioDaoRemote.deleteUser();
    }

    // ..................................... USER DATA ..........................................

    @Test
    public void testUserDataLoaded() throws IOException, UiException
    {

        regUserComuWithTkCache(comu_real_rodrigo);
        // Caso OK.
        userDataSingle.test().assertResult(user_crodrigo);
        cleanOneUser(user_crodrigo);
    }

    @Test
    public void test_UserAliasModified() throws Exception
    {
        // Preconditions.
        Usuario oldUsuario = new Usuario.UsuarioBuilder().copyUsuario(regGetUserComu(comu_real_rodrigo))
                .password(user_crodrigo.getPassword()).build();
        // Exec: we change alias.
        userAliasModified(oldUsuario,
                new Usuario.UsuarioBuilder()
                        .copyUsuario(oldUsuario)
                        .alias("new_alias")
                        .build())
                .test().assertResult(true);
        // Check side effects.
        assertThat(tkCacher.getTokenCache().get(), notNullValue());
        // Delete.
        cleanOneUser(oldUsuario);
    }

    @Test
    public void test_UserNameModified() throws Exception
    {
        // Preconditions.
        Usuario oldUsuario = new Usuario.UsuarioBuilder().copyUsuario(regGetUserComu(comu_real_rodrigo))
                .password(user_crodrigo.getPassword()).build();
        assertThat(tkCacher.getTokenCache().get(), notNullValue());
        // Exec: we change userName.
        userNameModified(oldUsuario,
                new Usuario.UsuarioBuilder()
                        .copyUsuario(oldUsuario)
                        .userName(USER_DROID.getUserName())
                        .build())
                .test().assertResult(true);
        // Check side effects.
        assertThat(tkCacher.getTokenCache().get(), nullValue());
        // Delete.
        assertThat(usuarioMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    private void finishLoginPswdSendSingle() throws UiException
    {
        // Es necesario conseguir un nuevo token.
        tkCacher.initIdentityCache(authDao.getPasswordUserToken(user_crodrigo.getUserName(), user_crodrigo.getPassword()));
        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

}