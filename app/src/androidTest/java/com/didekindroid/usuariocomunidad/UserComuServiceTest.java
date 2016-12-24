package com.didekindroid.usuariocomunidad;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.didekin.common.exception.ErrorBean;
import com.didekin.comunidad.Comunidad;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Response;

import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.TOKEN_NULL;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekin.usuario.UsuarioEndPoints.IS_USER_DELETED;
import static com.didekinaar.AppInitializer.creator;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.testutil.AarTestUtil.updateSecurityData;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.USER_JUAN2;
import static com.didekinaar.security.TokenIdentityCacher.updateIsRegistered;
import static com.didekindroid.comunidad.testutil.ComuTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuTestUtil.COMU_REAL;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.makeUsuarioComunidad;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:26
 */
public class UserComuServiceTest {

    CleanUserEnum whatClean;
    File refreshTkFile;
    Context context;

    @Before
    public void setUp() throws Exception{
        refreshTkFile = TKhandler.getRefreshTokenFile();
        whatClean = CLEAN_NOTHING;
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testDeleteUserComu() throws UiException, IOException
    {
        UserComuTestUtil.signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        List<UsuarioComunidad> userComus = AppUserComuServ.seeUserComusByUser();
        UsuarioComunidad uc_1 = userComus.get(0);

        assertThat(AppUserComuServ.deleteUserComu(uc_1.getComunidad().getC_Id()), is(IS_USER_DELETED));
        UsuarioDataTestUtils.cleanWithTkhandler();
    }

    @Test
    public void testGetComunidadesByUser_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        try {
            assertThat(AppUserComuServ.getComusByUser(), nullValue());
            fail();
        } catch (UiException se) {
            assertThat(se.getErrorBean().getMessage(), is(TOKEN_NULL.getHttpMessage()));
        }
    }

    @Test
    public void testGetComunidadesByUser_2() throws UiException, IOException
    {
        UserComuTestUtil.regTwoUserComuSameUser(UserComuTestUtil.makeListTwoUserComu());
        List<Comunidad> comunidades = AppUserComuServ.getComusByUser();
        assertThat(comunidades.size(), is(2));
        assertThat(comunidades, hasItems(COMU_REAL, COMU_LA_PLAZUELA_5));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetUserComuByUserAndComu_1() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);
        Comunidad comunidad = AppUserComuServ.getComusByUser().get(0);
        assertThat(AppUserComuServ.getUserComuByUserAndComu(comunidad.getC_Id()), is(COMU_REAL_JUAN));
    }

    @Test
    public void testGetUserComuByUserAndComu_2() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);
        // La comunidad no existe en BD.
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(999L).build();
        try {
            AppUserComuServ.getUserComuByUserAndComu(comunidad.getC_Id());
            fail();
        } catch (UiException e) {
            assertThat(e.getErrorBean().getMessage(), is(COMUNIDAD_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testIsOldestUser() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        UserComuTestUtil.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = AppUserComuServ.getComusByUser().get(0);
        assertThat(AppUserComuServ.isOldestOrAdmonUserComu(cDb.getC_Id()), is(true));

        UsuarioDataTestUtils.cleanWithTkhandler();
        UsuarioComunidad userComu = makeUsuarioComunidad(cDb, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function);
        AppUserComuServ.regUserAndUserComu(userComu).execute().body();
        updateSecurityData(USER_JUAN2.getUserName(), USER_JUAN2.getPassword());

        assertThat(AppUserComuServ.isOldestOrAdmonUserComu(cDb.getC_Id()), is(false));
    }

    @Test
    public void testModifyComuData() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        UserComuTestUtil.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDb = AppUserComuServ.getComusByUser().get(0);
        Comunidad cNew = new Comunidad.ComunidadBuilder()
                .c_id(cDb.getC_Id())
                .nombreVia("new_nombreVia")
                .tipoVia("new_tipoVia")
                .numero((short) 23)
                .sufijoNumero("sufi")
                .municipio(cDb.getMunicipio())
                .build();
        assertThat(AppUserComuServ.modifyComuData(cNew), is(1));
        Comunidad cNewDb = AppUserComuServ.getComusByUser().get(0);
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

        Usuario usuario_1 = UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_PEPE);
        List<UsuarioComunidad> userComus = AppUserComuServ.seeUserComusByUser();
        UsuarioComunidad uc_1 = makeUsuarioComunidad(userComus.get(0).getComunidad(), usuario_1,
                "portal3", "esc_2", "planta-ñ", "puerta2", "pre");
        assertThat(AppUserComuServ.modifyUserComu(uc_1), is(1));

        List<UsuarioComunidad> userComus_2 = AppUserComuServ.seeUserComusByUser();
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

        assertThat(refreshTkFile.exists(), is(false));
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);

        boolean isRegistered = AppUserComuServ.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        assertThat(isRegistered, is(true));
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;

        boolean isInserted = AppUserComuServ.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_1() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        // Comunidad is associated to other user.
        UserComuTestUtil.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = AppUserComuServ.getComusByUser().get(0);
        UsuarioDataTestUtils.cleanWithTkhandler();

        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));
        boolean isInserted = AppUserComuServ.regUserAndUserComu(userComu).execute().body();
        assertThat(isInserted, is(true));
    }

    @Test
    public void testRegUserAndUserComu_2() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        // Duplicated usuarioComunidad.
        Usuario pepe = UserComuTestUtil.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = AppUserComuServ.getComusByUser().get(0);
        UsuarioDataTestUtils.cleanWithTkhandler();
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, pepe,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));

        Response<Boolean> response = AppUserComuServ.regUserAndUserComu(userComu).execute();
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
        UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);
        List<Comunidad> comunidadesUserOne = AppUserComuServ.getComusByUser();
        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadesUserOne.get(0).getC_Id()).build();

        // Segundo usuarioComunidad, con diferente userComu y comunidad.
        UserComuTestUtil.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);

        // Añado comunidad del primer userComu al segundo.
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, null, "portal",
                "esc", "planta2", "doorJ", PRO.function);
        int rowInserted = AppUserComuServ.regUserComu(userComu);
        assertThat(rowInserted, is(1));
        assertThat(AppUserComuServ.getComusByUser().size(), is(2));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

    @Test
    public void testSeeUserComuByComu() throws Exception
    {
        UserComuTestUtil.regTwoUserComuSameUser(UserComuTestUtil.makeListTwoUserComu()); // User1, comunidades 1 y 2, userComu 1 y 2.
        Comunidad comunidad1 = AppUserComuServ.getComusByUser().get(0); // User1 in session.
        assertThat(comunidad1.getNombreComunidad(), is(COMU_REAL.getNombreComunidad()));

        UserComuTestUtil.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE); // User2, comunidad3, userComu 3.
        AppUserComuServ.regUserComu(makeUsuarioComunidad(comunidad1, null, "portal", "esc", "plantaY", "door21",
                PRO.function)); // User2 in session, comunidad1, userComu4.
        // Obtengo los id de las dos comunidades en DB, user2 in session.
        List<Comunidad> comunidades = AppUserComuServ.getComusByUser(); // comunidades 1 y 3.
        assertThat(comunidades.size(), is(2));
        // Busco por comunidad 1, segunda por orden.
        List<UsuarioComunidad> usuarioComusDB = AppUserComuServ.seeUserComusByComu(comunidades.get(1).getC_Id());
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
        AppUserComuServ.seeUserComusByUser();
    }

    @Test(expected = UiException.class)
    public void testSeeUserComusByUser_2() throws Exception
    {
        UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(usuarioDaoRemote.deleteUser(), is(true)); // We do not update security data.
        assertThat(TKhandler.doBearerAccessTkHeader(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        AppUserComuServ.seeUserComusByUser();
    }

    @Test(expected = UiException.class)
    public void testSeeUserComusByUser_3() throws Exception
    {
        UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(usuarioDaoRemote.deleteUser(), is(true));
        updateIsRegistered(false, context); // New variation: partially update of security data.
        assertThat(TKhandler.doBearerAccessTkHeader(), notNullValue());
        // Wrong credentials: the user doesn't exist.
        AppUserComuServ.seeUserComusByUser();
    }

    @Test
    public void testSeeUserComusByUser_4() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        UserComuTestUtil.signUpAndUpdateTk(COMU_REAL_JUAN);

        List<UsuarioComunidad> comunidadesUser = AppUserComuServ.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(1));
        assertThat(comunidadesUser, hasItem(COMU_REAL_JUAN));

        AppUserComuServ.regComuAndUserComu(COMU_PLAZUELA5_JUAN);
        comunidadesUser = AppUserComuServ.seeUserComusByUser();
        assertThat(comunidadesUser.size(), is(2));

        whatClean = CLEAN_JUAN;
    }


}