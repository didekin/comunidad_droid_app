package com.didekindroid.usuario.dao;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuario.dao.UsuarioDaoTestUtil.SendPswdCallable;
import com.didekindroid.usuario.dao.UsuarioDaoTestUtil.SendPswdCallableError;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Test;

import java.io.IOException;

import static com.didekindroid.lib_one.security.AuthDao.authDao;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkUpdatedCacheAfterPswd;
import static com.didekindroid.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.usuario.dao.UsuarioObservable.deleteMeSingle;
import static com.didekindroid.usuario.dao.UsuarioObservable.loginPswdSendSingle;
import static com.didekindroid.usuario.dao.UsuarioObservable.loginSingle;
import static com.didekindroid.usuario.dao.UsuarioObservable.loginUpdateTkCache;
import static com.didekindroid.usuario.dao.UsuarioObservable.passwordChangeWithPswdValidation;
import static com.didekindroid.usuario.dao.UsuarioObservable.userAliasModified;
import static com.didekindroid.usuario.dao.UsuarioObservable.userData;
import static com.didekindroid.usuario.dao.UsuarioObservable.userNameModified;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMockDaoRemote.userComuMockDao;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
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

    // ................................. DELETE USER ...............................

    @Test
    public void testGetDeleteMeSingle() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        deleteMeSingle().test().assertResult(true);
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    //    .................................... LOGIN .................................

    @Test
    public void testLoginSingle_1() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        loginSingle(USER_DROID).test().assertResult(true);
        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testLoginSingle_2() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginSingle(new Usuario.UsuarioBuilder()
                .userName("user@notfound.com")
                .password(USER_DROID.getPassword())
                .build())
                .test()
                .assertError(throwable -> {
                    UiException uiException = (UiException) throwable;
                    return uiException.getErrorBean().getMessage().equalsIgnoreCase(USER_NAME_NOT_FOUND.getHttpMessage());
                });

        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testLoginSingle_3() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginSingle(new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build())
                .test().assertResult(false);

        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void test_LoginUpdateTkCache_1() throws UiException, IOException
    {
        TKhandler.updateIsRegistered(userComuMockDao.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body());
        checkNoInitCache(); // Precondition.
        loginUpdateTkCache(USER_DROID).test().assertResult(true);
        checkInitTokenCache();
        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void test_LoginUpdateTkCache_2() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        checkInitTokenCache(); // Precondition.
        loginUpdateTkCache(new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build())
                .test().assertResult(false);
        checkUpdatedCacheAfterPswd(false, TKhandler.getTokenCache().get());
        cleanOptions(CLEAN_DROID);
    }

    /**
     * We use a mock callable to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void test_LoginPswdSendSingle_1() throws UiException, IOException, InterruptedException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        checkInitTokenCache(); // Precondition.
        loginPswdSendSingle(new SendPswdCallable()).test().assertResult(true);
        // Check cache cleaning.
        checkNoInitCache();
        finishLoginPswdSendSingle();
    }

    /**
     * We use a mock callable to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void test_LoginPswdSendSingle_2() throws UiException, IOException, InterruptedException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        checkInitTokenCache(); // Precondition.
        loginPswdSendSingle(new SendPswdCallableError()).test().assertFailure(UiException.class);
        // Check cache hasn't changed.
        checkInitTokenCache();
        finishLoginPswdSendSingle();
    }

    // ..................................... PASSWORD ..........................................

    @Test
    public void test_PasswordChangeWithPswdValidation() throws Exception
    {
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        final SpringOauthToken oldToken = TKhandler.getTokenCache().get();

        Usuario newUser = new Usuario.UsuarioBuilder().userName(USER_PEPE.getUserName()).password("new_password").build();
        passwordChangeWithPswdValidation(USER_PEPE, newUser).test().assertComplete();
        checkUpdatedCacheAfterPswd(true, oldToken);
        usuarioDaoRemote.deleteUser();
    }

    // ..................................... USER DATA ..........................................

    @Test
    public void testUserDataLoaded() throws IOException, UiException
    {

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        // Caso OK.
        userData().test().assertResult(USER_PEPE);
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void test_UserAliasModified() throws Exception
    {
        // Preconditions.
        Usuario oldUsuario = new Usuario.UsuarioBuilder().copyUsuario(signUpAndUpdateTk(COMU_ESCORIAL_PEPE))
                .password(USER_PEPE.getPassword()).build();
        // Exec: we change alias.
        userAliasModified(oldUsuario,
                new Usuario.UsuarioBuilder()
                        .copyUsuario(oldUsuario)
                        .alias("new_pepe_alias")
                        .build())
                .test().assertResult(true);
        // Check side effects.
        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        // Delete.
        cleanOneUser(oldUsuario);
    }

    @Test
    public void test_UserNameModified() throws Exception
    {
        // Preconditions.
        Usuario oldUsuario = new Usuario.UsuarioBuilder().copyUsuario(signUpAndUpdateTk(COMU_ESCORIAL_PEPE))
                .password(USER_PEPE.getPassword()).build();
        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        // Exec: we change userName.
        userNameModified(oldUsuario,
                new Usuario.UsuarioBuilder()
                        .copyUsuario(oldUsuario)
                        .userName(USER_DROID.getUserName())
                        .build())
                .test().assertResult(true);
        // Check side effects.
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        // Delete.
        assertThat(userComuMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    private void finishLoginPswdSendSingle() throws UiException
    {
        // Es necesario conseguir un nuevo token.
        TKhandler.initIdentityCache(authDao.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword()));
        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

}