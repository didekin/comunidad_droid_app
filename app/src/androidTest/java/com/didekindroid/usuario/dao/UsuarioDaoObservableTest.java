package com.didekindroid.usuario.dao;

import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDaoTestUtil.SendPswdCallable;
import com.didekindroid.usuario.testutil.UsuarioDaoTestUtil.SendPswdCallableError;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Test;

import java.io.IOException;

import io.reactivex.functions.Predicate;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkUpdatedCacheAfterPswd;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.deleteMeSingle;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.loginPswdSendSingle;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.loginSingle;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.loginUpdateTkCache;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.userDataLoaded;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.userModifiedTkUpdated;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.userModifiedWithPswdValidation;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 12:20
 */
public class UsuarioDaoObservableTest {

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

        loginSingle(new Usuario.UsuarioBuilder().userName("user@notfound.com").password(USER_DROID.getPassword()).build())
                .test().assertError(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception
            {
                UiException uiException = (UiException) throwable;
                return uiException.getErrorBean().getMessage().equalsIgnoreCase(USER_NAME_NOT_FOUND.getHttpMessage());
            }
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
        TKhandler.updateIsRegistered(userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body());
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

    // ..................................... USER DATA ..........................................

    @Test
    public void testUserDataLoaded() throws IOException, UiException
    {

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        // Caso OK.
        userDataLoaded().test().assertResult(USER_PEPE);
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void test_UserModifiedTokenUpdated() throws Exception
    {
        Usuario oldUsuario = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        userModifiedTkUpdated(TKhandler.getTokenCache().get(), doNewUser(oldUsuario)).test().awaitDone(4, SECONDS).assertComplete();

        // El hecho de poder borrar implica que la cache se ha actualizado correctamente.
        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

    @Test
    public void test_UserModifiedWithPswdValidation() throws Exception
    {
        Usuario oldUsuario = new Usuario.UsuarioBuilder().copyUsuario(signUpAndUpdateTk(COMU_ESCORIAL_PEPE)).password(USER_PEPE.getPassword()).build();
        userModifiedWithPswdValidation(oldUsuario, doNewUser(oldUsuario)).test().assertResult(true);

        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    private void finishLoginPswdSendSingle() throws UiException
    {
        // Es necesario conseguir un nuevo token.
        TKhandler.initIdentityCache(Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword()));
        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

    public Usuario doNewUser(Usuario oldUsuario)
    {
        return new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(oldUsuario.getuId())
                .password(USER_PEPE.getPassword())
                .build();
    }
}