package com.didekindroid.lib_one.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.TokenIdentityCacher;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekindroid.lib_one.util.IoHelper;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
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
    TokenIdentityCacher tkCacher;

    @Before
    public void setUp() throws Exception
    {
        tkCacher = (TokenIdentityCacher) secInitializer.get().getTkCacher();
        refreshTkFile = tkCacher.getRefreshTokenFile();
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
        whatClean = CLEAN_RODRIGO;

        regUserComuWithTkCache(comu_real_rodrigo);
        boolean isDeleted = usuarioDaoRemote.deleteAccessToken(tkCacher.getTokenCache().get().getValue());
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testDeleteUser() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        regUserComuWithTkCache(comu_real_rodrigo);
        // Borramos.
        assertThat(usuarioDaoRemote.deleteUser(), is(true));
    }

    @Test
    public void testGetGcmToken() throws UiException, IOException
    {
        whatClean = CLEAN_RODRIGO;

        regUserComuWithTkCache(comu_real_rodrigo);
        assertThat(usuarioDaoRemote.modifyUserGcmToken("test_gcm_token"), is(1));
        assertThat(usuarioDaoRemote.getGcmToken(), is("test_gcm_token"));
    }

    @Test
    public void testGetUserData() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        regUserComuWithTkCache(comu_real_rodrigo);

        Usuario usuario = usuarioDaoRemote.getUserData();
        assertThat(usuario.getUserName(), is(user_crodrigo.getUserName()));

        whatClean = CLEAN_RODRIGO;
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
        whatClean = CLEAN_RODRIGO;
        regUserComuWithTkCache(comu_real_rodrigo);

        assertThat(usuarioDaoRemote.loginInternal(user_crodrigo.getUserName(), user_crodrigo.getPassword()), is(true));
    }

    @Test
    public void testModifyUserWithToken_1() throws UiException, IOException
    {
        whatClean = CLEAN_RODRIGO;

        // Changed alias; not userName.
        Usuario usuario_1 = regGetUserComu(comu_real_rodrigo);
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .copyUsuario(usuario_1)
                .password(user_crodrigo.getPassword())
                .alias("new_alias")
                .build();

        int rowUpdated = usuarioDaoRemote.modifyUserWithToken(tkCacher.getTokenCache().get(), usuarioIn);
        assertThat(rowUpdated, is(1));
        // Login data has not changed.
        assertThat(usuarioDaoRemote.loginInternal(usuarioIn.getUserName(), usuarioIn.getPassword()), is(true));
    }

    @Test
    public void testModifyUserWithToken_2() throws UiException, IOException
    {
        whatClean = CLEAN_NOTHING;

        // Preconditions.
        Usuario usuario_1 = regGetUserComu(comu_real_rodrigo);
        assertThat(usuarioDaoRemote.loginInternal(usuario_1.getUserName(), user_crodrigo.getPassword()), is(true));

        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .userName(USER_DROID.getUserName())
                .alias("new_alias")
                .uId(usuario_1.getuId())
                .build();

        int rowUpdated = usuarioDaoRemote.modifyUserWithToken(tkCacher.getTokenCache().get(), usuarioIn);
        assertThat(rowUpdated, is(1));
        // Login data has changed: not only userName, but password.
        assertThat(usuarioDaoRemote.loginInternal(USER_DROID.getUserName(), user_crodrigo.getPassword()), is(false));
        // Clean.
        assertThat(usuarioMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
    }

    @Test
    public void testmodifyUserGcmToken() throws UiException, IOException
    {
        whatClean = CLEAN_RODRIGO;
        regUserComuWithTkCache(comu_real_rodrigo);
        assertThat(usuarioDaoRemote.modifyUserGcmToken("GCMToken12345X"), is(1));
        assertThat(usuarioDaoRemote.modifyUserGcmToken("GCMToken98765Z"), is(1));
    }

    @Test
    public void testPasswordChange() throws UiException, IOException
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        String newPassword = "new_password";
        assertThat(usuarioDaoRemote.passwordChange(tkCacher.getTokenCache().get(), newPassword), is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(user_crodrigo.getUserName())
                .password(newPassword)
                .build());
    }

    @Test
    public void testPasswordSend_1() throws UiException, IOException
    {
        // If exception, login data are not changed.
        regUserComuWithTkCache(comu_real_rodrigo);
        try {
            usuarioDaoRemote.sendPassword("wrong_userName");
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USER_NAME_NOT_FOUND.getHttpMessage()));
        }
        assertThat(usuarioDaoRemote.loginInternal(user_crodrigo.getUserName(), user_crodrigo.getPassword()), is(true));
        whatClean = CLEAN_RODRIGO;
    }

    @Test
    public void testPasswordSend_2() throws UiException, IOException
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        // Exec and check.
        assertThat(usuarioDaoRemote.sendPassword(user_crodrigo.getUserName()), is(true));
        // Login data has changed.
        assertThat(usuarioDaoRemote.loginInternal(user_crodrigo.getUserName(), user_crodrigo.getPassword()), is(false));
        // Clean.
        assertThat(usuarioMockDao.deleteUser(user_crodrigo.getUserName()).execute().body(), is(true));

    }

//    ====================== NON INTERFACE TESTS =========================

    @Test
    public void testSignedUp() throws UiException, IOException
    {
        assertThat(refreshTkFile.exists(), is(false));

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        regUserComuWithTkCache(comu_real_rodrigo);

        assertThat(refreshTkFile.exists(), is(true));
        SpringOauthToken tokenJuan = tkCacher.getTokenCache().get();
        assertThat(tokenJuan, notNullValue());
        assertThat(tokenJuan.getValue(), not(isEmptyOrNullString()));
        assertThat(IoHelper.readStringFromFile(refreshTkFile), is(tokenJuan.getRefreshToken().getValue()));

        whatClean = CLEAN_RODRIGO;
    }
}