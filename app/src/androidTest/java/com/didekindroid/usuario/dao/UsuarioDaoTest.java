package com.didekindroid.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.util.IoHelper;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMockDaoRemote.userComuMockDao;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:08
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class UsuarioDaoTest {

    File refreshTkFile;
    CleanUserEnum whatClean;

    @Before
    public void setUp() throws Exception
    {
        refreshTkFile = TKhandler.getRefreshTokenFile();
        whatClean = CLEAN_NOTHING;
    }

    @After
    public void cleaningUp() throws UiException
    {
        cleanOptions(whatClean);
    }

//    ========================= INTERFACE TESTS =======================

    @Test
    public void testDeleteAccessToken() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        signUpAndUpdateTk(COMU_REAL_PEPE);
        boolean isDeleted = usuarioDaoRemote.deleteAccessToken(TKhandler.getTokenCache().get().getValue());
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testDeleteUser() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        // Borramos.
        assertThat(usuarioDaoRemote.deleteUser(), is(true));
    }

    @Test
    public void testGetGcmToken() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        assertThat(usuarioDaoRemote.modifyUserGcmToken("pepe_test_gcm_token"), is(1));
        assertThat(usuarioDaoRemote.getGcmToken(), is("pepe_test_gcm_token"));
    }

    @Test
    public void testGetUserData() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        Usuario usuario = usuarioDaoRemote.getUserData();
        assertThat(usuario.getUserName(), is(USER_JUAN.getUserName()));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testLoginInternal_1()
    {
        // User not in DB.
        try {
            usuarioDaoRemote.loginInternal("user@notfound.com", "password_wrong");
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USER_NAME_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testLoginInternal_2() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;
        signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(usuarioDaoRemote.loginInternal(USER_JUAN.getUserName(), USER_JUAN.getPassword()), is(true));
    }

    @Test
    public void testModifyUserWithToken_1() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        // Changed alias; not userName.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .copyUsuario(usuario_1)
                .password(USER_JUAN.getPassword())
                .alias("new_alias_juan")
                .build();

        int rowUpdated = usuarioDaoRemote.modifyUserWithToken(TKhandler.getTokenCache().get(), usuarioIn);
        assertThat(rowUpdated, is(1));
        // Login data has not changed.
        assertThat(usuarioDaoRemote.loginInternal(usuarioIn.getUserName(), usuarioIn.getPassword()), is(true));
    }

    @Test
    public void testModifyUserWithToken_2() throws UiException, IOException
    {
        whatClean = CLEAN_NOTHING;

        // Preconditions.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_REAL_PEPE);
        assertThat(usuarioDaoRemote.loginInternal(USER_PEPE.getUserName(), USER_PEPE.getPassword()), is(true));

        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .userName(USER_DROID.getUserName())
                .alias("new_alias_pepe")
                .uId(usuario_1.getuId())
                .build();

        int rowUpdated = usuarioDaoRemote.modifyUserWithToken(TKhandler.getTokenCache().get(), usuarioIn);
        assertThat(rowUpdated, is(1));
        // Login data has changed: not only userName, but password.
        assertThat(usuarioDaoRemote.loginInternal(USER_DROID.getUserName(), USER_PEPE.getPassword()), is(false));
        // Clean.
        assertThat(userComuMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
    }

    @Test
    public void testmodifyUserGcmToken() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        assertThat(usuarioDaoRemote.modifyUserGcmToken("GCMToken12345X"), is(1));
        assertThat(usuarioDaoRemote.modifyUserGcmToken("GCMToken98765Z"), is(1));
    }

    @Test
    public void testPasswordChange() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        String newPassword = "new_juan_password";
        assertThat(usuarioDaoRemote.passwordChange(TKhandler.getTokenCache().get(), newPassword), is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(USER_JUAN.getUserName())
                .password(newPassword)
                .build());
    }

    @Test
    public void testPasswordSend_1() throws UiException, IOException
    {
        // If exception, login data are not changed.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            usuarioDaoRemote.sendPassword("wrong_userName");
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USER_NAME_NOT_FOUND.getHttpMessage()));
        }
        assertThat(usuarioDaoRemote.loginInternal(USER_JUAN.getUserName(), USER_JUAN.getPassword()), is(true));
    }

    @Test
    public void testPasswordSend_2() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        // Exec and check.
        assertThat(usuarioDaoRemote.sendPassword(USER_DROID.getUserName()), is(true));
        // Login data has changed.
        assertThat(usuarioDaoRemote.loginInternal(USER_DROID.getUserName(), USER_DROID.getPassword()), is(false));
        // Clean.
        assertThat(userComuMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));

    }

//    ====================== NON INTERFACE TESTS =========================

    @Test
    public void testSignedUp() throws UiException, IOException
    {
        assertThat(refreshTkFile.exists(), is(false));

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(refreshTkFile.exists(), is(true));
        SpringOauthToken tokenJuan = TKhandler.getTokenCache().get();
        assertThat(tokenJuan, notNullValue());
        assertThat(tokenJuan.getValue(), not(isEmptyOrNullString()));
        assertThat(IoHelper.readStringFromFile(refreshTkFile), is(tokenJuan.getRefreshToken().getValue()));

        whatClean = CLEAN_JUAN;
    }
}