package com.didekinaar.usuario;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.ErrorBean;
import com.didekin.comunidad.Comunidad;
import com.didekin.comunidad.Municipio;
import com.didekin.comunidad.Provincia;
import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UsuarioTestUtils;
import com.didekinaar.utils.IoHelper;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Response;

import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.TOKEN_NULL;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekin.usuario.UsuarioEndPoints.IS_USER_DELETED;
import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.comunidad.AarComunidadService.AarComuServ;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOneUser;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanTwoUsers;
import static com.didekinaar.testutil.AarActivityTestUtils.updateSecurityData;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_LA_PLAZUELA_5;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_REAL;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_REAL_DROID;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.USER_DROID;
import static com.didekinaar.testutil.UsuarioTestUtils.USER_JUAN;
import static com.didekinaar.testutil.UsuarioTestUtils.USER_JUAN2;
import static com.didekinaar.testutil.UsuarioTestUtils.USER_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekinaar.usuario.AarUsuarioService.AarUserServ;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.usuariocomunidad.RolUi.PRE;
import static com.didekinaar.usuariocomunidad.RolUi.PRO;
import static com.didekinaar.utils.UIutils.updateIsRegistered;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
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
public class AarUsuarioServiceTest {

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
    public void cleaningUp() throws UiAarException
    {
        cleanOptions(whatClean);
    }

//    ========================= INTERFACE TESTS =======================

    @Test
    public void testDeleteAccessToken() throws UiAarException, IOException
    {
        whatClean = CLEAN_PEPE;

        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_PEPE);
        boolean isDeleted = AarUserServ.deleteAccessToken(TKhandler.getAccessTokenInCache().getValue());
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testDeleteUser() throws Exception
    {
        // No file with refreshToken.
        assertThat(refreshTkFile.exists(), is(false));
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testDeleteUserComu() throws UiAarException, IOException
    {
        AarActivityTestUtils.signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        List<UsuarioComunidad> userComus = AarUserComuServ.seeUserComusByUser();
        UsuarioComunidad uc_1 = userComus.get(0);

        assertThat(AarUserComuServ.deleteUserComu(uc_1.getComunidad().getC_Id()), is(IS_USER_DELETED));
        AarActivityTestUtils.cleanWithTkhandler();
    }

    @Test
    public void testGetComuData() throws UiAarException, IOException
    {
        whatClean = CLEAN_PEPE;

        AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDB = AarUserComuServ.getComusByUser().get(0);
        Comunidad c1 = AarComuServ.getComuData(cDB.getC_Id());
        assertThat(c1, is(cDB));
    }

    @Test
    public void testGetComunidadesByUser_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        try {
            assertThat(AarUserComuServ.getComusByUser(), nullValue());
            fail();
        } catch (UiAarException se) {
            assertThat(se.getErrorBean().getMessage(), is(TOKEN_NULL.getHttpMessage()));
        }
    }

    @Test
    public void testGetComunidadesByUser_2() throws UiAarException, IOException
    {
        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        List<Comunidad> comunidades = AarUserComuServ.getComusByUser();
        assertThat(comunidades.size(), is(2));
        assertThat(comunidades, hasItems(COMU_REAL, COMU_LA_PLAZUELA_5));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetGcmToken() throws UiAarException, IOException
    {
        whatClean = CLEAN_PEPE;

        AarActivityTestUtils.signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        assertThat(AarUserServ.modifyUserGcmToken("pepe_test_gcm_token"), is(1));
        assertThat(AarUserServ.getGcmToken(), is("pepe_test_gcm_token"));
    }

    @Test
    public void testGetUserComuByUserAndComu_1() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);
        Comunidad comunidad = AarUserComuServ.getComusByUser().get(0);
        assertThat(AarUserComuServ.getUserComuByUserAndComu(comunidad.getC_Id()), is(COMU_REAL_JUAN));
    }

    @Test
    public void testGetUserComuByUserAndComu_2() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);
        // La comunidad no existe en BD.
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(999L).build();
        try {
            AarUserComuServ.getUserComuByUserAndComu(comunidad.getC_Id());
            fail();
        } catch (UiAarException e) {
            assertThat(e.getErrorBean().getMessage(), is(COMUNIDAD_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testGetUserData() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);

        Usuario usuario = AarUserServ.getUserData();
        assertThat(usuario.getUserName(), is(USER_JUAN.getUserName()));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testIsOldestUser() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = AarUserComuServ.getComusByUser().get(0);
        assertThat(AarUserComuServ.isOldestOrAdmonUserComu(cDb.getC_Id()), is(true));

        AarActivityTestUtils.cleanWithTkhandler();
        UsuarioComunidad userComu = makeUsuarioComunidad(cDb, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function);
        AarUserComuServ.regUserAndUserComu(userComu).execute().body();
        updateSecurityData(USER_JUAN2.getUserName(), USER_JUAN2.getPassword());

        assertThat(AarUserComuServ.isOldestOrAdmonUserComu(cDb.getC_Id()), is(false));
    }

    @Test
    public void testLoginInternal_1()
    {
        // User not in DB.
        try {
            AarUserServ.loginInternal("user@notfound.com", "password_wrong");
            fail();
        } catch (UiAarException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USER_NAME_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testLoginInternal_2() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(AarUserServ.loginInternal(USER_JUAN.getUserName(), USER_JUAN.getPassword()), is(true));
    }

    @Test
    public void testModifyComuData() throws UiAarException, IOException
    {
        whatClean = CLEAN_PEPE;

        AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = AarUserComuServ.getComusByUser().get(0);
        Comunidad cNew = new Comunidad.ComunidadBuilder()
                .c_id(cDb.getC_Id())
                .nombreVia("new_nombreVia")
                .tipoVia("new_tipoVia")
                .numero((short) 23)
                .sufijoNumero("sufi")
                .municipio(cDb.getMunicipio())
                .build();
        assertThat(AarUserComuServ.modifyComuData(cNew), is(1));
        Comunidad cNewDb = AarUserComuServ.getComusByUser().get(0);
        assertThat(cNewDb.getNombreVia(), is(cNew.getNombreVia()));
        assertThat(cNewDb.getTipoVia(), is(cNew.getTipoVia()));
        assertThat(cNewDb.getNumero(), is(cNew.getNumero()));
        assertThat(cNewDb.getSufijoNumero(), is(cNew.getSufijoNumero()));
        assertThat(cNewDb.getMunicipio(), is(cNew.getMunicipio()));
    }

    @Test
    public void testModifyUser_1() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        // Changed alias; not user.
        Usuario usuario_1 = AarActivityTestUtils.signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .alias("new_alias_juan")
                .uId(usuario_1.getuId())
                .build();

        int rowUpdated = AarUserServ.modifyUser(usuarioIn);
        assertThat(rowUpdated, is(1));
    }

    @Test
    public void testModifyUser_2() throws UiAarException, IOException
    {
        whatClean = CLEAN_NOTHING;

        // Changed user.
        Usuario usuario_1 = AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_PEPE);
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
    public void testModifyUserComu() throws UiAarException, IOException
    {

        whatClean = CLEAN_PEPE;

        Usuario usuario_1 = AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_PEPE);
        List<UsuarioComunidad> userComus = AarUserComuServ.seeUserComusByUser();
        UsuarioComunidad uc_1 = makeUsuarioComunidad(userComus.get(0).getComunidad(), usuario_1,
                "portal3", "esc_2", "planta-ñ", "puerta2", "pre");
        assertThat(AarUserComuServ.modifyUserComu(uc_1), is(1));

        List<UsuarioComunidad> userComus_2 = AarUserComuServ.seeUserComusByUser();
        UsuarioComunidad uc_2 = userComus_2.get(0);
        assertThat(uc_2.getPortal(), is(uc_1.getPortal()));
        assertThat(uc_2.getEscalera(), is(uc_1.getEscalera()));
        assertThat(uc_2.getPlanta(), is(uc_1.getPlanta()));
        assertThat(uc_2.getPuerta(), is(uc_1.getPuerta()));
        assertThat(uc_2.getRoles(), is(uc_1.getRoles()));
    }

    @Test
    public void testmodifyUserGcmToken() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;
        AarActivityTestUtils.signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        assertThat(AarUserServ.modifyUserGcmToken("GCMToken12345X"), is(1));
        assertThat(AarUserServ.modifyUserGcmToken("GCMToken98765Z"), is(1));
    }

    @Test
    public void testPasswordChange() throws UiAarException, IOException
    {
        AarActivityTestUtils.signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        String passwordClear_2 = "new_juan_password";
        assertThat(AarUserServ.passwordChange(passwordClear_2), is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(USER_JUAN.getUserName())
                .password(passwordClear_2)
                .build());
    }

    @Test
    public void testPasswordSend() throws UiAarException, IOException
    {
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_DROID);
        assertThat(AarUserServ.passwordSend(USER_DROID.getUserName()).execute().body(), is(true));
        // Es necesario conseguir un nuevo token. La validación del antiguo falla por el cambio de password.
        SpringOauthToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        AarUserServ.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        AarActivityTestUtils.cleanWithTkhandler();
    }

    @Test
    public void testRegComuAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;

        assertThat(refreshTkFile.exists(), is(false));
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);

        boolean isRegistered = AarUserComuServ.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        assertThat(isRegistered, is(true));
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;

        boolean isInserted = AarUserComuServ.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_1() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        // Comunidad is associated to other user.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = AarUserComuServ.getComusByUser().get(0);
        AarActivityTestUtils.cleanWithTkhandler();

        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));
        boolean isInserted = AarUserComuServ.regUserAndUserComu(userComu).execute().body();
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_2() throws UiAarException, IOException
    {
        whatClean = CLEAN_PEPE;

        // Duplicated usuarioComunidad.
        Usuario pepe = AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = AarUserComuServ.getComusByUser().get(0);
        AarActivityTestUtils.cleanWithTkhandler();
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, pepe,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));

        Response<Boolean> response = AarUserComuServ.regUserAndUserComu(userComu).execute();
        assertThat(response.isSuccessful(), is(false));
        ErrorBean errorBean = creator.get().getRetrofitHandler().getErrorBean(response);
        assertThat(errorBean, notNullValue());
        assertThat(errorBean.getMessage(), is(USER_NAME_DUPLICATE.getHttpMessage()));
        assertThat(errorBean.getHttpStatus(), is(USER_NAME_DUPLICATE.getHttpStatus()));
    }

    @Test
    public void testRegUserComu() throws Exception
    {
        // User and comunidad are already registered.

        // First usuarioComunidad.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);
        List<Comunidad> comunidadesUserOne = AarUserComuServ.getComusByUser();
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadesUserOne.get(0).getC_Id()).build();

        // Segundo usuarioComunidad, con diferente userComu y comunidad.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);

        // Añado comunidad del primer userComu al segundo.
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, null, "portal",
                "esc", "planta2", "doorJ", PRO.function);
        int rowInserted = AarUserComuServ.regUserComu(userComu);
        assertThat(rowInserted, is(1));
        assertThat(AarUserComuServ.getComusByUser().size(), is(2));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

    @Test
    public void testSearchComunidades() throws Exception
    {
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);
        AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidadSearch = UsuarioTestUtils.makeComunidad("Calle", "de la Plazuela", (short) 11, "",
                new Municipio((short) 13, new Provincia((short) 3)));

        List<Comunidad> comunidades = AarComuServ.searchComunidades(comunidadSearch).execute().body();

        assertThat(comunidades.size(), is(1));

        assertThat(comunidades.get(0).getNombreComunidad(), is("Travesía de la Plazuela 11"));
        assertThat(comunidades.get(0).getNombreVia(), CoreMatchers.is("de la Plazuela"));
        assertThat(comunidades.get(0).getTipoVia(), CoreMatchers.is("Travesía"));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getProvinciaId(), is((short) 3));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getNombre(), is("Alicante/Alacant"));
        assertThat(comunidades.get(0).getMunicipio().getCodInProvincia(), is((short) 13));
        assertThat(comunidades.get(0).getMunicipio().getNombre(), is("Algueña"));

        cleanTwoUsers(USER_JUAN, USER_PEPE);
    }

    @Test
    public void testSeeUserComuByComu() throws Exception
    {
        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu()); // User1, comunidades 1 y 2, userComu 1 y 2.
        Comunidad comunidad1 = AarUserComuServ.getComusByUser().get(0); // User1 in session.
        assertThat(comunidad1.getNombreComunidad(), is(COMU_REAL.getNombreComunidad()));

        AarActivityTestUtils.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE); // User2, comunidad3, userComu 3.
        AarUserComuServ.regUserComu(makeUsuarioComunidad(comunidad1, null, "portal", "esc", "plantaY", "door21",
                PRO.function)); // User2 in session, comunidad1, userComu4.
        // Obtengo los id de las dos comunidades en DB, user2 in session.
        List<Comunidad> comunidades = AarUserComuServ.getComusByUser(); // comunidades 1 y 3.
        assertThat(comunidades.size(), is(2));
        // Busco por comunidad 1, segunda por orden.
        List<UsuarioComunidad> usuarioComusDB = AarUserComuServ.seeUserComusByComu(comunidades.get(1).getC_Id());
        assertThat(usuarioComusDB.size(), is(2)); // userComu 2 y 4; users 1 y 2.
        assertThat(usuarioComusDB.get(0).getUsuario(), is(COMU_PLAZUELA5_JUAN.getUsuario()));
        assertThat(usuarioComusDB.get(0).getComunidad().getC_Id(), is(comunidades.get(1).getC_Id()));
        assertThat(usuarioComusDB.get(1).getUsuario(), is(COMU_REAL_PEPE.getUsuario()));
        assertThat(usuarioComusDB.get(1).getComunidad().getC_Id(), is(comunidades.get(1).getC_Id()));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

    @Test(expected = UiAarException.class)
    public void testSeeUserComusByUser_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        AarUserComuServ.seeUserComusByUser();
    }

    @Test(expected = UiAarException.class)
    public void testSeeUserComusByUser_2() throws Exception
    {
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(AarUserServ.deleteUser(), is(true)); // We do not update security data.
        assertThat(TKhandler.doBearerAccessTkHeader(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        AarUserComuServ.seeUserComusByUser();
    }

    @Test(expected = UiAarException.class)
    public void testSeeUserComusByUser_3() throws Exception
    {
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(AarUserServ.deleteUser(), is(true));
        updateIsRegistered(false, context); // New variation: partially update of security data.
        assertThat(TKhandler.doBearerAccessTkHeader(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        AarUserComuServ.seeUserComusByUser();
    }

    @Test
    public void testSeeUserComusByUser_4() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);

        List<UsuarioComunidad> comunidadesUser = AarUserComuServ.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(1));
        assertThat(comunidadesUser, hasItem(COMU_REAL_JUAN));

        AarUserComuServ.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        comunidadesUser = AarUserComuServ.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(2));

        whatClean = CLEAN_JUAN;
    }

//    ====================== NON INTERFACE TESTS =========================

    @Test
    public void testSignedUp() throws UiAarException, IOException
    {
        assertThat(refreshTkFile.exists(), is(false));

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        AarActivityTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(refreshTkFile.exists(), is(true));
        SpringOauthToken tokenJuan = TKhandler.getAccessTokenInCache();
        assertThat(tokenJuan, notNullValue());
        assertThat(tokenJuan.getValue(), not(isEmptyOrNullString()));
        assertThat(IoHelper.readStringFromFile(refreshTkFile), is(tokenJuan.getRefreshToken().getValue()));

        whatClean = CLEAN_JUAN;
    }

    /*@Test
    public void testBaseURL() throws Exception
    {
        String hostAndPort = DidekinApp.getBaseURL();
        assertThat(hostAndPort, equalTo("http://192.168.57.1:8080"));
    }*/
}