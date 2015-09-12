package com.didekindroid.usuario.webservices;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.retrofitcl.ServiceOneException;
import com.didekin.security.OauthToken;
import com.didekin.serviceone.domain.*;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.ioutils.IoHelper;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import com.didekindroid.usuario.dominio.DomainDataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static com.didekindroid.usuario.activity.utils.CleanEnum.*;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
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
    public void testDeleteComunidad() throws Exception
    {
        // Comunidad with one user.

        assertThat(refreshTkFile.exists(), is(false));
        signUpAndUpdateTk(COMU_REAL_JUAN);

        List<UsuarioComunidad> usuarioComunidades = ServOne.getUsuariosComunidad();

        boolean isDeleted = ServOne.deleteComunidad(usuarioComunidades.get(0).getComunidad().getC_Id());
        assertThat(isDeleted, is(true));

        // User cleanup. He/she has not got any role associated, althought the access token is still valid.
        isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));

        whatClean = CLEAN_TK_HANDLER;
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
    public void testGetComunidadesByUser_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        try {
            assertThat(ServOne.getComunidadesByUser(), nullValue());
        } catch (ServiceOneException se) { // NO hay excepción; ServiceOne devuelve null.
            fail();
        }
    }

    @Test
    public void testGetComunidadesByUser_2()
    {
        regTwoUserComuSameUser(DomainDataUtils.makeListTwoUserComu());
        List<Comunidad> comunidades = ServOne.getComunidadesByUser();
        assertThat(comunidades.size(), is(2));
        assertThat(comunidades, hasItems(COMU_REAL, DomainDataUtils.COMU_LA_PLAZUELA_5));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetUsuariosComunidad_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        assertThat(ServOne.getUsuariosComunidad(), nullValue());
    }

    @Test
    public void testGetUsuariosComunidad__2() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        List<UsuarioComunidad> comunidadesUser = ServOne.getUsuariosComunidad();
        assertThat(comunidadesUser.size(), is(1));
        assertThat(comunidadesUser, hasItem(COMU_REAL_JUAN));

        ServOne.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        comunidadesUser = ServOne.getUsuariosComunidad();
        assertThat(comunidadesUser.size(), is(2));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetUserData() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        Usuario usuario = ServOne.getUserData();
        assertThat(usuario.getUserName(), is(USER_JUAN.getUserName()));
        assertThat(usuario.getPrefixTf(), is(USER_JUAN.getPrefixTf()));
        assertThat(usuario.getNumeroTf(), is(USER_JUAN.getNumeroTf()));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testRegComuAndUserComu() throws Exception
    {
        assertThat(refreshTkFile.exists(), is(false));
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        boolean isRegistered = ServOne.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        assertThat(isRegistered, is(true));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws Exception
    {
        boolean isInserted = ServOne.regComuAndUserAndUserComu(COMU_REAL_JUAN);
        assertThat(isInserted,is(true));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testRegUserComu() throws Exception
    {
        // User and comunidad are already registered.

        // First usuarioComunidad.
        Usuario usuario_1 = signUpAndUpdateTk(COMU_REAL_JUAN);
        List<Comunidad> comunidadesUserOne = ServOne.getComunidadesByUser();
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadesUserOne.get(0).getC_Id()).build();

        // Segundo usuarioComunidad, con diferente usuario y comunidad.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);

        // Añado comunidad del primer usuario al segundo.
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, null, "portal",
                "esc", "planta2", "doorJ", PROPIETARIO.function);
        int rowInserted = ServOne.regUserComu(userComu);
        assertThat(rowInserted, is(1));
        assertThat(ServOne.getComunidadesByUser().size(), is(2));

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
        Comunidad comunidad1 = ServOne.getComunidadesByUser().get(0); // User1 in session.
        assertThat(comunidad1.getNombreComunidad(), is(COMU_REAL.getNombreComunidad()));

        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE); // User2, comunidad3, userComu 3.
        ServOne.regUserComu(makeUsuarioComunidad(comunidad1, null, "portal", "esc", "plantaY", "door21",
                PROPIETARIO.function)); // User2 in session, comunidad1, userComu4.
        // Obtengo los id de las dos comunidades en DB, user2 in session.
        List<Comunidad> comunidades = ServOne.getComunidadesByUser(); // comunidades 1 y 3.
        assertThat(comunidades.size(), is(2));
        // Busco por comunidad 1, segunda por orden.
        List<UsuarioComunidad> usuarioComusDB = ServOne.seeUserComuByComu(comunidades.get(1).getC_Id());
        assertThat(usuarioComusDB.size(), is(2)); // userComu 2 y 4; users 1 y 2.
        assertThat(usuarioComusDB.get(0).getUsuario().getUserName(), is(COMU_PLAZUELA5_JUAN.getUsuario().getUserName()));
        assertThat(usuarioComusDB.get(0).getComunidad().getNombreComunidad(),
                is(COMU_REAL.getNombreComunidad()));
        assertThat(usuarioComusDB.get(1).getUsuario().getUserName(), is(COMU_REAL_PEPE.getUsuario().getUserName()));
        assertThat(usuarioComusDB.get(1).getComunidad().getNombreComunidad(),
                is(COMU_REAL.getNombreComunidad()));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

//    ====================== NON INTERFACE TESTS =========================

    @Test
    public void testSignedUp()
    {
        assertThat(refreshTkFile.exists(), is(false));

        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        assertThat(refreshTkFile.exists(), is(true));
        OauthToken.AccessToken tokenJuan = TKhandler.getAccessTokenInCache();
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