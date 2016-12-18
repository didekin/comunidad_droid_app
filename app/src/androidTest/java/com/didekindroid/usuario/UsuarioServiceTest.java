package com.didekindroid.usuario;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum;
import com.didekinaar.utils.IoHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOneUser;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.usuario.UsuarioService.AarUserServ;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_DROID;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;
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
public class UsuarioServiceTest {

    Context context;
    File refreshTkFile;
    CleanUserEnum whatClean;

    @Before
    public void setUp() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
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
        boolean isDeleted = AarUserServ.deleteAccessToken(TKhandler.getAccessTokenInCache().getValue());
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
        assertThat(AarUserServ.modifyUserGcmToken("pepe_test_gcm_token"), is(1));
        assertThat(AarUserServ.getGcmToken(), is("pepe_test_gcm_token"));
    }

    @Test
    public void testGetUserData() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        Usuario usuario = AarUserServ.getUserData();
        assertThat(usuario.getUserName(), is(USER_JUAN.getUserName()));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testLoginInternal_1()
    {
        // User not in DB.
        try {
            AarUserServ.loginInternal("user@notfound.com", "password_wrong");
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

        assertThat(AarUserServ.loginInternal(USER_JUAN.getUserName(), USER_JUAN.getPassword()), is(true));
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

        int rowUpdated = AarUserServ.modifyUser(usuarioIn);
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

        int rowUpdated = AarUserServ.modifyUser(usuarioIn);
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
        assertThat(AarUserServ.modifyUserGcmToken("GCMToken12345X"), is(1));
        assertThat(AarUserServ.modifyUserGcmToken("GCMToken98765Z"), is(1));
    }

    @Test
    public void testPasswordChange() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        String passwordClear_2 = "new_juan_password";
        assertThat(AarUserServ.passwordChange(passwordClear_2), is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(USER_JUAN.getUserName())
                .password(passwordClear_2)
                .build());
    }

    @Test
    public void testPasswordSend() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        assertThat(AarUserServ.passwordSend(USER_DROID.getUserName()).execute().body(), is(true));
        // Es necesario conseguir un nuevo token. La validación del antiguo falla por el cambio de password.
        SpringOauthToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        AarUserServ.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        AarActivityTestUtils.cleanWithTkhandler();
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