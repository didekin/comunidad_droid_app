package com.didekindroid.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.http.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekindroid.util.IoHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static com.didekin.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
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
public class UsuarioDaoRemoteTest {

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
        boolean isDeleted = usuarioDao.deleteAccessToken(TKhandler.getAccessTokenInCache().getValue());
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testDeleteUser() throws Exception
    {
        // No file with refreshToken.
        assertThat(refreshTkFile.exists(), is(false));
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetGcmToken() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        assertThat(usuarioDao.modifyUserGcmToken("pepe_test_gcm_token"), is(1));
        assertThat(usuarioDao.getGcmToken(), is("pepe_test_gcm_token"));
    }

    @Test
    public void testGetUserData() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        Usuario usuario = usuarioDao.getUserData();
        assertThat(usuario.getUserName(), is(USER_JUAN.getUserName()));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testLoginInternal_1()
    {
        // User not in DB.
        try {
            usuarioDao.loginInternal("user@notfound.com", "password_wrong");
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

        assertThat(usuarioDao.loginInternal(USER_JUAN.getUserName(), USER_JUAN.getPassword()), is(true));
    }

    @Test
    public void testModifyUser_1() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        // Changed alias; not user.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .alias("new_alias_juan")
                .uId(usuario_1.getuId())
                .build();

        int rowUpdated = usuarioDao.modifyUser(usuarioIn);
        assertThat(rowUpdated, is(1));
    }

    @Test
    public void testModifyUser_2() throws UiException, IOException
    {
        whatClean = CLEAN_NOTHING;

        // Changed user.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_REAL_PEPE);
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .userName("new_pepe@pepe.com")
                .alias("new_alias_pepe")
                .uId(usuario_1.getuId())
                .build();

        int rowUpdated = usuarioDao.modifyUser(usuarioIn);
        assertThat(rowUpdated, is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                .copyUsuario(usuarioIn)
                .password(USER_PEPE.getPassword())
                .build()
        );
    }

    @Test
    public void testmodifyUserGcmToken() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        assertThat(usuarioDao.modifyUserGcmToken("GCMToken12345X"), is(1));
        assertThat(usuarioDao.modifyUserGcmToken("GCMToken98765Z"), is(1));
    }

    @Test
    public void testPasswordChange() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        String passwordClear_2 = "new_juan_password";
        assertThat(usuarioDao.passwordChange(passwordClear_2), is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(USER_JUAN.getUserName())
                .password(passwordClear_2)
                .build());
    }

    @Test
    public void testPasswordSend() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        assertThat(usuarioDao.sendPassword(USER_DROID.getUserName()), is(true));
        // Es necesario conseguir un nuevo token. La validación del antiguo falla por el cambio de password.
        SpringOauthToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        usuarioDao.deleteUser();
        UsuarioDataTestUtils.cleanWithTkhandler();
    }

//    ====================== NON INTERFACE TESTS =========================

    @Test
    public void testSignedUp() throws UiException, IOException
    {
        assertThat(refreshTkFile.exists(), is(false));

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(refreshTkFile.exists(), is(true));
        SpringOauthToken tokenJuan = TKhandler.getAccessTokenInCache();
        assertThat(tokenJuan, notNullValue());
        assertThat(tokenJuan.getValue(), not(isEmptyOrNullString()));
        assertThat(IoHelper.readStringFromFile(refreshTkFile), is(tokenJuan.getRefreshToken().getValue()));

        whatClean = CLEAN_JUAN;
    }
}