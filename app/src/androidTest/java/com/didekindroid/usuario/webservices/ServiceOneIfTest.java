package com.didekindroid.usuario.webservices;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.retrofitcl.OauthToken.AccessToken;
import com.didekin.retrofitcl.ServiceOneException;
import com.didekin.serviceone.domain.*;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.R;
import com.didekindroid.utils.IoHelper;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import com.didekindroid.usuario.dominio.DomainDataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static com.didekin.retrofitcl.OauthTokenHelper.HELPER;
import static com.didekin.serviceone.controllers.ControllerConstant.IS_USER_DELETED;
import static com.didekin.serviceone.exception.ExceptionMessage.USER_DUPLICATE;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.exception.UiException.UiAction.SEARCH_COMU;
import static com.didekindroid.utils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.CleanEnum.*;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRESIDENTE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:08
 */
@RunWith(AndroidJUnit4.class)
public class ServiceOneIfTest {

    Context context;
    File refreshTkFile;
    CleanEnum whatClean;

    @Before
    public void setUp() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        whatClean = CLEAN_NOTHING;
    }

    @After
    public void cleaningUp()
    {
        cleanOptions(whatClean);
    }

//    ========================= INTERFACE TESTS =======================

    @Test
    public void testDeleteAccessToken()
    {
        whatClean = CLEAN_PEPE;

        signUpAndUpdateTk(COMU_REAL_PEPE);
        boolean isDeleted = ServOne.deleteAccessToken(TKhandler.getAccessTokenInCache().getValue());
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testDeleteUser() throws Exception
    {
        // No file with refreshToken.
        assertThat(refreshTkFile.exists(), is(false));
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testDeleteUserComu() throws UiException
    {
        Usuario usuario_1 = signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        List<UsuarioComunidad> userComus = ServOne.seeUserComusByUser();
        UsuarioComunidad uc_1 = userComus.get(0);

        assertThat(ServOne.deleteUserComu(uc_1.getComunidad().getC_Id()), is(IS_USER_DELETED));
        cleanWithTkhandler();
    }

    @Test
    public void testGetComuData()
    {
        whatClean = CLEAN_PEPE;

        Usuario u1 = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDB = ServOne.getComusByUser().get(0);
        Comunidad c1 = ServOne.getComuData(cDB.getC_Id());
        assertThat(c1, is(cDB));
    }

    @Test
    public void testGetComunidadesByUser_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        try {
            assertThat(ServOne.getComusByUser(), nullValue());
        } catch (ServiceOneException se) { // NO hay excepción; ServiceOne devuelve null.
            fail();
        }
    }

    @Test
    public void testGetComunidadesByUser_2()
    {
        regTwoUserComuSameUser(DomainDataUtils.makeListTwoUserComu());
        List<Comunidad> comunidades = ServOne.getComusByUser();
        assertThat(comunidades.size(), is(2));
        assertThat(comunidades, hasItems(COMU_REAL, DomainDataUtils.COMU_LA_PLAZUELA_5));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetUserData() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        Usuario usuario = ServOne.getUserData();
        assertThat(usuario.getUserName(), is(USER_JUAN.getUserName()));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testIsOldestUser()
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        Usuario u1 = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = ServOne.getComusByUser().get(0);
        assertThat(ServOne.isOldestUserComu(cDb.getC_Id()), is(true));

        cleanWithTkhandler();
        UsuarioComunidad userComu = makeUsuarioComunidad(cDb, USER_JUAN2,
                "portalB", null, "planta1", null, PROPIETARIO.function.concat(",").concat(PRESIDENTE.function));
        ServOne.regUserAndUserComu(userComu);
        updateSecurityData(USER_JUAN2.getUserName(), USER_JUAN2.getPassword());

        assertThat(ServOne.isOldestUserComu(cDb.getC_Id()), is(false));
    }

    @Test
    public void testLoginInternal_1()
    {
        // User not in DB.
        try {
            ServOne.loginInternal("user@notfound.com", "password_wrong");
        } catch (UiException ue) {
            assertThat(ue.getAction(), is(SEARCH_COMU));
            assertThat(ue.getResourceId(), is(R.string.user_without_signedUp));
        }
    }

    @Test
    public void testLoginInternal_2() throws UiException
    {
        whatClean = CLEAN_JUAN;
        signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(ServOne.loginInternal(USER_JUAN.getUserName(), USER_JUAN.getPassword()), is(true));
    }

    @Test
    public void testModifyComuData()
    {
        whatClean = CLEAN_PEPE;

        Usuario u1 = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = ServOne.getComusByUser().get(0);
        Comunidad cNew = new Comunidad.ComunidadBuilder()
                .c_id(cDb.getC_Id())
                .nombreVia("new_nombreVia")
                .tipoVia("new_tipoVia")
                .numero((short) 23)
                .sufijoNumero("sufi")
                .municipio(cDb.getMunicipio())
                .build();
        assertThat(ServOne.modifyComuData(cNew), is(1));
        assertThat(ServOne.getComusByUser().get(0), allOf(
                hasProperty("tipoVia", equalTo(cNew.getTipoVia())),
                hasProperty("nombreVia", equalTo(cNew.getNombreVia())),
                hasProperty("numero", equalTo((short) 23)),
                hasProperty("sufijoNumero", equalTo(cNew.getSufijoNumero())),
                hasProperty("municipio", equalTo(cNew.getMunicipio()))
        ));
    }

    @Test
    public void testModifyUser_1()
    {
        whatClean = CLEAN_JUAN;

        // Changed alias; not user.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .alias("new_alias_juan")
                .uId(usuario_1.getuId())
                .build();

        int rowUpdated = ServOne.modifyUser(usuarioIn);
        assertThat(rowUpdated, is(1));
    }

    @Test
    public void testModifyUser_2()
    {
        whatClean = CLEAN_NOTHING;

        // Changed user.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_REAL_PEPE);
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .userName("new_pepe@pepe.com")
                .alias("new_alias_pepe")
                .uId(usuario_1.getuId())
                .build();

        int rowUpdated = ServOne.modifyUser(usuarioIn);
        assertThat(rowUpdated, is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                        .copyUsuario(usuarioIn)
                        .password(USER_PEPE.getPassword())
                        .build()
        );
    }

    @Test
    public void testModifyUserComu() throws UiException
    {

        whatClean = CLEAN_PEPE;

        Usuario usuario_1 = signUpAndUpdateTk(COMU_REAL_PEPE);
        List<UsuarioComunidad> userComus = ServOne.seeUserComusByUser();
        UsuarioComunidad uc_1 = makeUsuarioComunidad(userComus.get(0).getComunidad(), usuario_1,
                "portal3", "esc_2", "planta-ñ", "puerta2", "pre");
        assertThat(ServOne.modifyUserComu(uc_1), is(1));

        List<UsuarioComunidad> userComus_2 = ServOne.seeUserComusByUser();
        UsuarioComunidad uc_2 = userComus_2.get(0);
        assertThat(uc_2.getPortal(), is(uc_1.getPortal()));
        assertThat(uc_2.getEscalera(), is(uc_1.getEscalera()));
        assertThat(uc_2.getPlanta(), is(uc_1.getPlanta()));
        assertThat(uc_2.getPuerta(), is(uc_1.getPuerta()));
        assertThat(uc_2.getRoles(), is(uc_1.getRoles()));
    }

    @Test
    public void testPasswordChange()
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        String passwordClear_2 = "new_juan_password";
        assertThat(ServOne.passwordChange(passwordClear_2), is(1));

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(USER_JUAN.getUserName())
                .password(passwordClear_2)
                .build());
    }

    @Test
    public void testPasswordSend()
    {
        signUpAndUpdateTk(COMU_REAL_PEPE);
        assertThat(ServOne.passwordSend(USER_PEPE.getUserName()),is(true));
        // Es necesario conseguir un nuevo token. La validación del antiguo falla por el cambio de password.
        AccessToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenKey());
        ServOne.deleteUser(HELPER.doBearerAccessTkHeader(token));
        cleanWithTkhandler();
    }

    @Test
    public void testRegComuAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;

        assertThat(refreshTkFile.exists(), is(false));
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        boolean isRegistered = ServOne.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        assertThat(isRegistered, is(true));
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;

        boolean isInserted = ServOne.regComuAndUserAndUserComu(COMU_REAL_JUAN);
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_1()
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        // Comunidad is associated to other user.
        Usuario pepe = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = ServOne.getComusByUser().get(0);
        cleanWithTkhandler();

        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, USER_JUAN2,
                "portalB", null, "planta1", null, PROPIETARIO.function.concat(",").concat(PRESIDENTE.function));
        boolean isInserted = ServOne.regUserAndUserComu(userComu);
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_2()
    {
        whatClean = CLEAN_PEPE;

        // Duplicated usuarioComunidad.
        Usuario pepe = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = ServOne.getComusByUser().get(0);
        cleanWithTkhandler();

        try {
            UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, pepe,
                    "portalB", null, "planta1", null, PROPIETARIO.function.concat(",").concat(PRESIDENTE.function));
            ServOne.regUserAndUserComu(userComu);
            fail();
        } catch (ServiceOneException e) {
            assertThat(e.getServiceMessage(), is(USER_DUPLICATE.getMessage()));
            assertThat(e.getHttpStatus(), is(USER_DUPLICATE.getHttpStatus()));
        }
    }

    @Test
    public void testRegUserComu() throws Exception
    {
        // User and comunidad are already registered.

        // First usuarioComunidad.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_REAL_JUAN);
        List<Comunidad> comunidadesUserOne = ServOne.getComusByUser();
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadesUserOne.get(0).getC_Id()).build();

        // Segundo usuarioComunidad, con diferente usuario y comunidad.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);

        // Añado comunidad del primer usuario al segundo.
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, null, "portal",
                "esc", "planta2", "doorJ", PROPIETARIO.function);
        int rowInserted = ServOne.regUserComu(userComu);
        assertThat(rowInserted, is(1));
        assertThat(ServOne.getComusByUser().size(), is(2));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

    @Test
    public void testSearchComunidades() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidadSearch = DomainDataUtils.makeComunidad("Calle", "de la Plazuela", (short) 11, "",
                new Municipio((short) 13, new Provincia((short) 3)));

        List<Comunidad> comunidades = ServOne.searchComunidades(comunidadSearch);

        cleanTwoUsers(USER_JUAN, USER_PEPE);  // anticipated cleaning.

        assertThat(comunidades.size(), is(1));

        assertThat(comunidades.get(0).getNombreComunidad(), is("Travesía de la Plazuela 11"));
        assertThat(comunidades.get(0).getNombreVia(), CoreMatchers.is("de la Plazuela"));
        assertThat(comunidades.get(0).getTipoVia(), CoreMatchers.is("Travesía"));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getProvinciaId(), is((short) 3));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getNombre(), is("Alicante/Alacant"));
        assertThat(comunidades.get(0).getMunicipio().getCodInProvincia(), is((short) 13));
        assertThat(comunidades.get(0).getMunicipio().getNombre(), is("Algueña"));
    }

    @Test
    public void testSeeUserComuByComu() throws Exception
    {
        regTwoUserComuSameUser(makeListTwoUserComu()); // User1, comunidades 1 y 2, userComu 1 y 2.
        Comunidad comunidad1 = ServOne.getComusByUser().get(0); // User1 in session.
        assertThat(comunidad1.getNombreComunidad(), is(COMU_REAL.getNombreComunidad()));

        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE); // User2, comunidad3, userComu 3.
        ServOne.regUserComu(makeUsuarioComunidad(comunidad1, null, "portal", "esc", "plantaY", "door21",
                PROPIETARIO.function)); // User2 in session, comunidad1, userComu4.
        // Obtengo los id de las dos comunidades en DB, user2 in session.
        List<Comunidad> comunidades = ServOne.getComusByUser(); // comunidades 1 y 3.
        assertThat(comunidades.size(), is(2));
        // Busco por comunidad 1, segunda por orden.
        List<UsuarioComunidad> usuarioComusDB = ServOne.seeUserComusByComu(comunidades.get(1).getC_Id());
        assertThat(usuarioComusDB.size(), is(2)); // userComu 2 y 4; users 1 y 2.
        assertThat(usuarioComusDB.get(0).getUsuario(), is(COMU_PLAZUELA5_JUAN.getUsuario()));
        assertThat(usuarioComusDB.get(0).getComunidad().getC_Id(), is(comunidades.get(1).getC_Id()));
        assertThat(usuarioComusDB.get(1).getUsuario(), is(COMU_REAL_PEPE.getUsuario()));
        assertThat(usuarioComusDB.get(1).getComunidad().getC_Id(), is(comunidades.get(1).getC_Id()));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

    @Test(expected = UiException.class)
    public void testSeeUserComusByUser_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        ServOne.seeUserComusByUser();
    }

    @Test(expected = UiException.class)
    public void testSeeUserComusByUser_2() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(ServOne.deleteUser(), is(true)); // We do not update security data.
        assertThat(TKhandler.doBearerAccessTkHeader(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        ServOne.seeUserComusByUser();
    }

    @Test(expected = UiException.class)
    public void testSeeUserComusByUser_3() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(ServOne.deleteUser(), is(true));
        updateIsRegistered(false, context); // New variation: partially update of security data.
        assertThat(TKhandler.doBearerAccessTkHeader(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        ServOne.seeUserComusByUser();
    }

    @Test
    public void testSeeUserComusByUser_4() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        List<UsuarioComunidad> comunidadesUser = ServOne.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(1));
        assertThat(comunidadesUser, hasItem(COMU_REAL_JUAN));

        ServOne.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        comunidadesUser = ServOne.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(2));

        whatClean = CLEAN_JUAN;
    }

//    ====================== NON INTERFACE TESTS =========================

    @Test
    public void testSignedUp()
    {
        assertThat(refreshTkFile.exists(), is(false));

        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(refreshTkFile.exists(), is(true));
        AccessToken tokenJuan = TKhandler.getAccessTokenInCache();
        assertThat(tokenJuan, notNullValue());
        assertThat(tokenJuan.getValue(), not(isEmptyOrNullString()));
        assertThat(IoHelper.readStringFromFile(refreshTkFile), is(tokenJuan.getRefreshToken().getValue()));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testBaseURL() throws Exception
    {
        String hostAndPort = DidekindroidApp.getBaseURL();
        assertThat(hostAndPort, equalTo("http://10.0.3.2:9000"));
    }
}