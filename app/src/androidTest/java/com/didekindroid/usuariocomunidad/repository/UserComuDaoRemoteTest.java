package com.didekindroid.usuariocomunidad.repository;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekinlib.http.HttpHandler;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Response;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_REAL;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMockDaoRemote.userComuMockDao;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.TOKEN_NULL;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekinlib.http.usuario.UsuarioServConstant.IS_USER_DELETED;
import static com.didekinlib.model.usuariocomunidad.Rol.INQUILINO;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:26
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDaoRemoteTest {

    CleanUserEnum whatClean;
    File refreshTkFile;
    HttpHandler httpHandler;

    @Before
    public void setUp() throws Exception
    {
        refreshTkFile = TKhandler.getRefreshTokenFile();
        whatClean = CLEAN_NOTHING;
        httpHandler = httpInitializer.get().getHttpHandler();
    }

    @After
    public void cleaningUp() throws UiException
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testDeleteUserComu() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        List<UsuarioComunidad> userComus = userComuDaoRemote.seeUserComusByUser();
        UsuarioComunidad uc_1 = userComus.get(0);

        assertThat(userComuDaoRemote.deleteUserComu(uc_1.getComunidad().getC_Id()), is(IS_USER_DELETED));
        cleanWithTkhandler();
    }

    @Test
    public void testGetComunidadesByUser_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        try {
            assertThat(userComuDaoRemote.getComusByUser(), nullValue());
            fail();
        } catch (UiException se) {
            assertThat(se.getErrorBean().getMessage(), is(TOKEN_NULL.getHttpMessage()));
        }
    }

    @Test
    public void testGetComunidadesByUser_2() throws UiException, IOException
    {
        regTwoUserComuSameUser(makeListTwoUserComu());
        List<Comunidad> comunidades = userComuDaoRemote.getComusByUser();
        assertThat(comunidades.size(), is(2));
        assertThat(comunidades, hasItems(COMU_REAL, COMU_LA_PLAZUELA_5));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetUserComuByUserAndComu_1() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        signUpAndUpdateTk(COMU_REAL_JUAN);
        Comunidad comunidad = userComuDaoRemote.getComusByUser().get(0);
        assertThat(userComuDaoRemote.getUserComuByUserAndComu(comunidad.getC_Id()), is(COMU_REAL_JUAN));
    }

    @Test
    public void testGetUserComuByUserAndComu_2() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        signUpAndUpdateTk(COMU_REAL_JUAN);
        // La comunidad no existe en BD.
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(999L).build();
        assertThat(userComuDaoRemote.getUserComuByUserAndComu(comunidad.getC_Id()), nullValue());
    }

    @Test
    public void testIsOldestUser() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = userComuDaoRemote.getComusByUser().get(0);
        assertThat(userComuDaoRemote.isOldestOrAdmonUserComu(cDb.getC_Id()), is(true));

        cleanWithTkhandler();
        UsuarioComunidad userComu = makeUsuarioComunidad(cDb, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function);
        userComuMockDao.regUserAndUserComu(userComu).execute().body();
        updateSecurityData(USER_JUAN2.getUserName(), USER_JUAN2.getPassword());

        assertThat(userComuDaoRemote.isOldestOrAdmonUserComu(cDb.getC_Id()), is(false));
    }

    @Test
    public void testModifyComuData() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = userComuDaoRemote.getComusByUser().get(0);
        Comunidad cNew = new Comunidad.ComunidadBuilder()
                .c_id(cDb.getC_Id())
                .nombreVia("new_nombreVia")
                .tipoVia("new_tipoVia")
                .numero((short) 23)
                .sufijoNumero("sufi")
                .municipio(cDb.getMunicipio())
                .build();
        assertThat(userComuDaoRemote.modifyComuData(cNew), is(1));
        Comunidad cNewDb = userComuDaoRemote.getComusByUser().get(0);
        assertThat(cNewDb.getNombreVia(), is(cNew.getNombreVia()));
        assertThat(cNewDb.getTipoVia(), is(cNew.getTipoVia()));
        assertThat(cNewDb.getNumero(), is(cNew.getNumero()));
        assertThat(cNewDb.getSufijoNumero(), is(cNew.getSufijoNumero()));
        assertThat(cNewDb.getMunicipio(), is(cNew.getMunicipio()));
    }

    @Test
    public void testModifyUserComu() throws UiException, IOException
    {

        whatClean = CLEAN_PEPE;

        Usuario usuario_1 = signUpAndUpdateTk(COMU_REAL_PEPE);
        List<UsuarioComunidad> userComus = userComuDaoRemote.seeUserComusByUser();
        UsuarioComunidad uc_1 = makeUsuarioComunidad(userComus.get(0).getComunidad(), usuario_1,
                "portal3", "esc_2", "planta-ñ", "puerta2", "pre");
        assertThat(userComuDaoRemote.modifyUserComu(uc_1), is(1));

        List<UsuarioComunidad> userComus_2 = userComuDaoRemote.seeUserComusByUser();
        UsuarioComunidad uc_2 = userComus_2.get(0);
        assertThat(uc_2.getPortal(), is(uc_1.getPortal()));
        assertThat(uc_2.getEscalera(), is(uc_1.getEscalera()));
        assertThat(uc_2.getPlanta(), is(uc_1.getPlanta()));
        assertThat(uc_2.getPuerta(), is(uc_1.getPuerta()));
        assertThat(uc_2.getRoles(), is(uc_1.getRoles()));
    }

    @Test
    public void testRegComuAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;

        Usuario juan = signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(userComuDaoRemote.regComuAndUserComu(
                new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, juan).planta("uno").roles(INQUILINO.function).build()), is(true));
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;

        boolean isInserted = userComuMockDao.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_1() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        // Comunidad is associated to other user.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = userComuDaoRemote.getComusByUser().get(0);
        cleanWithTkhandler();

        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));
        boolean isInserted = userComuMockDao.regUserAndUserComu(userComu).execute().body();
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_2() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        // Duplicated usuarioComunidad.
        Usuario pepe = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = userComuDaoRemote.getComusByUser().get(0);
        cleanWithTkhandler();
        Usuario pepeAgain = new Usuario.UsuarioBuilder().copyUsuario(pepe).password(USER_PEPE.getPassword()).build();
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, pepeAgain,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));

        Response<Boolean> response = userComuMockDao.regUserAndUserComu(userComu).execute();
        assertThat(response.isSuccessful(), is(false));
        ErrorBean errorBean = httpHandler.getErrorBean(response);
        assertThat(errorBean, notNullValue());
        assertThat(errorBean.getMessage(), is(USER_NAME_DUPLICATE.getHttpMessage()));
        assertThat(errorBean.getHttpStatus(), is(USER_NAME_DUPLICATE.getHttpStatus()));
    }

    @Test
    public void testRegUserComu() throws Exception
    {
        // User and comunidad are already registered.

        // First usuarioComunidad.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        List<Comunidad> comunidadesUserOne = userComuDaoRemote.getComusByUser();
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadesUserOne.get(0).getC_Id()).build();

        // Segundo usuarioComunidad, con diferente userComu y comunidad.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);

        // Añado comunidad del primer userComu al segundo.
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, null, "portal",
                "esc", "planta2", "doorJ", PRO.function);
        int rowInserted = userComuDaoRemote.regUserComu(userComu);
        assertThat(rowInserted, is(1));
        assertThat(userComuDaoRemote.getComusByUser().size(), is(2));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

    @Test
    public void testSeeUserComuByComu() throws Exception
    {
        regTwoUserComuSameUser(makeListTwoUserComu()); // User1, comunidades 1 y 2, userComu 1 y 2.
        Comunidad comunidad1 = userComuDaoRemote.getComusByUser().get(0); // User1 in session.
        assertThat(comunidad1.getNombreComunidad(), is(COMU_REAL.getNombreComunidad()));

        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE); // User2, comunidad3, userComu 3.
        userComuDaoRemote.regUserComu(makeUsuarioComunidad(comunidad1, null, "portal", "esc", "plantaY", "door21",
                PRO.function)); // User2 in session, comunidad1, userComu4.
        // Obtengo los id de las dos comunidades en DB, user2 in session.
        List<Comunidad> comunidades = userComuDaoRemote.getComusByUser(); // comunidades 1 y 3.
        assertThat(comunidades.size(), is(2));
        // Busco por comunidad 1, segunda por orden.
        List<UsuarioComunidad> usuarioComusDB = userComuDaoRemote.seeUserComusByComu(comunidades.get(1).getC_Id());
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
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        userComuDaoRemote.seeUserComusByUser();
    }

    @Test(expected = UiException.class)
    public void testSeeUserComusByUser_2() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(usuarioDaoRemote.deleteUser(), is(true)); // We do not update security data.
        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        userComuDaoRemote.seeUserComusByUser();
    }

    @Test(expected = UiException.class)
    public void testSeeUserComusByUser_3() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(usuarioDaoRemote.deleteUser(), is(true));
        TKhandler.updateIsRegistered(false); // New variation: partially update of security data.
        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        userComuDaoRemote.seeUserComusByUser();
    }

    @Test
    public void testSeeUserComusByUser_4() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        List<UsuarioComunidad> comunidadesUser = userComuDaoRemote.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(1));
        assertThat(comunidadesUser, hasItem(COMU_REAL_JUAN));

        userComuDaoRemote.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        comunidadesUser = userComuDaoRemote.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(2));

        whatClean = CLEAN_JUAN;
    }
}