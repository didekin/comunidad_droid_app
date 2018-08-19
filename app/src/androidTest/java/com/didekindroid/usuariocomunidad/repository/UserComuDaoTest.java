package com.didekindroid.usuariocomunidad.repository;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_EL_ESCORIAL;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_PLAZUELA_5;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN2;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpMockGcmGetComu;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.http.usuario.UsuarioServConstant.IS_USER_DELETED;
import static com.didekinlib.model.usuariocomunidad.Rol.INQUILINO;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:26
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDaoTest {

    private CleanUserEnum whatClean = CLEAN_NOTHING;

    @After
    public void cleaningUp()
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testDeleteUserComu() throws Exception
    {
        assertThat(userComuDao
                .deleteUserComu(signUpGetComu(COMU_PLAZUELA5_JUAN).getC_Id()).blockingGet(), is(IS_USER_DELETED));
        assertThat(userComuDao.getTkCacher().isRegisteredCache(), is(false));
        cleanWithTkhandler();
    }

    @Test
    public void test_getComusByUser() throws Exception
    {
        whatClean = CLEAN_JUAN;
        regTwoUserComuSameUser(makeListTwoUserComu());
        assertThat(userComuDao.getComusByUser().blockingGet(), hasItems(comu_real, COMU_LA_PLAZUELA_5));
    }

    @Test
    public void test_getUserComuByUserAndComu_1() throws Exception
    {
        whatClean = CLEAN_JUAN;
        assertThat(userComuDao.
                getUserComuByUserAndComu(signUpGetComu(COMU_REAL_JUAN).getC_Id()).blockingGet(), is(COMU_REAL_JUAN));
    }

    @Test
    public void test_getUserComuByUserAndComu_2() throws Exception
    {
        whatClean = CLEAN_JUAN;
        regComuUserUserComuGetAuthTk(COMU_REAL_JUAN);
        // La comunidad no existe en BD.
        userComuDao.getUserComuByUserAndComu(999L).test().assertError(
                exception -> UiException.class.cast(exception).getErrorHtppMsg().equals(COMUNIDAD_NOT_FOUND.getHttpMessage())
        );
    }

    @Test
    public void test_isOldestOrAdmonUserComu() throws Exception
    {
        whatClean = CLEAN_PEPE;
        assertThat(userComuDao
                .isOldestOrAdmonUserComu(signUpGetComu(COMU_TRAV_PLAZUELA_PEPE).getC_Id()).blockingGet(), is(true));
    }

    @Test
    public void test_modifyComuData() throws Exception
    {
        whatClean = CLEAN_PEPE;
        Comunidad cNew = new Comunidad.ComunidadBuilder()
                .copyComunidadNonNullValues(signUpGetComu(COMU_TRAV_PLAZUELA_PEPE))
                .nombreVia("new_nombreVia")
                .build();
        assertThat(userComuDao.modifyComuData(cNew).blockingGet(), is(1));
    }

    @Test
    public void test_modifyUserComu() throws Exception
    {
        whatClean = CLEAN_PEPE;

        regComuUserUserComuGetAuthTk(COMU_REAL_PEPE);
        UsuarioComunidad userComuOld = userComuDao.seeUserComusByUser().blockingGet().get(0);
        UsuarioComunidad userComuNew = new UsuarioComunidad.UserComuBuilder(userComuOld.getComunidad(), userComuOld.getUsuario())
                .userComuRest(COMU_REAL_PEPE)
                .escalera("new_esc")
                .build();
        assertThat(userComuDao.modifyUserComu(userComuNew).blockingGet(), is(1));
    }

    @Test
    public void test_regComuAndUserAndUserComu()
    {
        whatClean = CLEAN_JUAN;
        userComuDao.regComuAndUserAndUserComu(COMU_REAL_JUAN).test().assertComplete();
        waitAtMost(6, SECONDS).until(() -> usuarioDaoRemote.getGcmToken() != null);
    }

    @Test
    public void test_regComuAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;
        userComuDao.regComuAndUserComu(
                new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, regComuUserUserComuGetUser(COMU_REAL_JUAN))
                        .planta("uno")
                        .roles(INQUILINO.function)
                        .build())
                .test().assertComplete();
    }

    @Test
    public void test_regUserAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        // Comunidad is associated to other user.
        Comunidad comunidad = signUpGetComu(COMU_TRAV_PLAZUELA_PEPE);
        cleanWithTkhandler();
        getInstance().deleteInstanceId();

        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));
        userComuDao.regUserAndUserComu(userComu).test().assertComplete();
        waitAtMost(6, SECONDS).until(() -> usuarioDaoRemote.getGcmToken() != null);
    }

    @Test
    public void testRegUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN_AND_PEPE;
        // Comunidad1 and user1 in DB.
        Comunidad comunidad1 = signUpMockGcmGetComu(COMU_REAL_JUAN, "juan_mock_gcmTk");
        userComuDao.getTkCacher().updateIsRegistered(false);
        /* Comunidad2 and user2 in DB.*/
        regComuUserUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
        // Add comunidad1 and user2 (her data are in cache now and they can be null).
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad1, null, "portal",
                "esc", "planta2", "doorJ", PRO.function);
        userComuDao.regUserComu(userComu).test().assertComplete();
    }

    @Test
    public void test_seeUserComuByComu() throws Exception
    {
        whatClean = CLEAN_JUAN;
        Comunidad comunidad = signUpGetComu(COMU_ESCORIAL_JUAN);
        List<UsuarioComunidad> userComus = userComuDao.seeUserComusByComu(comunidad.getC_Id()).blockingGet();
        assertThat(userComus.get(0).getComunidad(), is(comunidad));
    }

    @Test
    public void testSeeUserComusByUser_4() throws Exception
    {
        whatClean = CLEAN_JUAN;
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        regComuUserUserComuGetAuthTk(COMU_REAL_JUAN);
        assertThat(userComuDao.seeUserComusByUser().blockingGet(), hasItem(COMU_REAL_JUAN));
    }

    @Test
    public void test_GetUserWithAppTk()
    {
        // Precondition
        assertThat(COMU_PLAZUELA5_JUAN.getUsuario().getGcmToken(), nullValue());
        // Check.
        UsuarioComunidad userComuAfter = userComuDao.getUserWithAppTk(COMU_PLAZUELA5_JUAN);
        assertThat(userComuAfter.getUsuario().getGcmToken().length() > 2, is(true));
        assertThat(COMU_PLAZUELA5_JUAN.getUsuario(), is(userComuAfter.getUsuario()));
        assertThat(COMU_PLAZUELA5_JUAN.getComunidad(), is(userComuAfter.getComunidad()));
        assertThat(COMU_PLAZUELA5_JUAN.getRoles(), is(userComuAfter.getRoles()));
    }
}