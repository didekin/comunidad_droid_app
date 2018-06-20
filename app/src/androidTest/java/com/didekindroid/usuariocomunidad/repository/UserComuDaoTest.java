package com.didekindroid.usuariocomunidad.repository;

import android.support.test.runner.AndroidJUnit4;

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
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
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
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpWithTkGetComu;
import static com.didekinlib.http.usuario.UsuarioServConstant.IS_USER_DELETED;
import static com.didekinlib.model.usuariocomunidad.Rol.INQUILINO;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
    public void testDeleteUserComu()
    {
        assertThat(userComuDao
                .deleteUserComu(signUpWithTkGetComu(COMU_PLAZUELA5_JUAN).getC_Id()).blockingGet(), is(IS_USER_DELETED));
        assertThat(userComuDao.getTkCacher().isRegisteredCache(), is(false));
        cleanWithTkhandler();
    }

    @Test
    public void test_getComusByUser()
    {
        whatClean = CLEAN_JUAN;
        regTwoUserComuSameUser(makeListTwoUserComu());
        assertThat(userComuDao.getComusByUser().blockingGet(), hasItems(comu_real, COMU_LA_PLAZUELA_5));
    }

    @Test
    public void test_getUserComuByUserAndComu_1()
    {
        whatClean = CLEAN_JUAN;
        assertThat(userComuDao.
                getUserComuByUserAndComu(signUpWithTkGetComu(COMU_REAL_JUAN).getC_Id()).blockingGet(), is(COMU_REAL_JUAN));
    }

    @Test
    public void test_getUserComuByUserAndComu_2()
    {
        whatClean = CLEAN_JUAN;
        regUserComuWithTkCache(COMU_REAL_JUAN);
        // La comunidad no existe en BD.
        assertThat(userComuDao.getUserComuByUserAndComu(999L).blockingGet(), nullValue());
    }

    @Test
    public void test_isOldestOrAdmonUserComu()
    {
        whatClean = CLEAN_PEPE;
        assertThat(userComuDao
                .isOldestOrAdmonUserComu(signUpWithTkGetComu(COMU_TRAV_PLAZUELA_PEPE).getC_Id()).blockingGet(), is(true));
    }

    @Test
    public void test_modifyComuData()
    {
        whatClean = CLEAN_PEPE;
        Comunidad cNew = new Comunidad.ComunidadBuilder()
                .copyComunidadNonNullValues(signUpWithTkGetComu(COMU_TRAV_PLAZUELA_PEPE))
                .nombreVia("new_nombreVia")
                .build();
        assertThat(userComuDao.modifyComuData(cNew).blockingGet(), is(1));
    }

    @Test
    public void test_modifyUserComu()
    {
        whatClean = CLEAN_PEPE;

        regUserComuWithTkCache(COMU_REAL_PEPE);
        UsuarioComunidad userComuOld = userComuDao.seeUserComusByUser().blockingGet().get(0);
        UsuarioComunidad userComuNew = new UsuarioComunidad.UserComuBuilder(userComuOld.getComunidad(), userComuOld.getUsuario())
                .userComuRest(COMU_REAL_PEPE)
                .escalera("new_escaler")
                .build();
        assertThat(userComuDao.modifyUserComu(userComuNew).blockingGet(), is(1));
    }

    @Test
    public void test_regComuAndUserAndUserComu()
    {
        whatClean = CLEAN_JUAN;
        userComuDao.regComuAndUserAndUserComu(COMU_REAL_JUAN).test().assertComplete();
    }

    @Test
    public void test_regComuAndUserComu() throws Exception
    {
        whatClean = CLEAN_JUAN;
        userComuDao.regComuAndUserComu(
                new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, regGetUserComu(COMU_REAL_JUAN))
                        .planta("uno")
                        .roles(INQUILINO.function)
                        .build())
                .test().assertComplete();
    }

    @Test
    public void test_regUserAndUserComu()
    {
        whatClean = CLEAN_JUAN2_AND_PEPE;

        // Comunidad is associated to other user.
        Comunidad comunidad = signUpWithTkGetComu(COMU_TRAV_PLAZUELA_PEPE);
        cleanWithTkhandler();

        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));
        userComuDao.regUserAndUserComu(userComu).test().assertComplete();
    }

    @Test
    public void testRegUserComu()
    {
        whatClean = CLEAN_JUAN_AND_PEPE;
        // Comunidad1 and user1 in DB.
        Comunidad comunidad1 = signUpWithTkGetComu(COMU_REAL_JUAN);
        // Comunidad2 and user2 in DB.
        regUserComuWithTkCache(COMU_TRAV_PLAZUELA_PEPE);
        // Add comunidad1 and user2 (her data are in cache now and they can be null).
        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad1, null, "portal",
                "esc", "planta2", "doorJ", PRO.function);
        userComuDao.regUserComu(userComu).test().assertComplete();
    }

    @Test
    public void test_seeUserComuByComu()
    {
        whatClean = CLEAN_JUAN;
        Comunidad comunidad = signUpWithTkGetComu(COMU_ESCORIAL_JUAN);
        List<UsuarioComunidad> userComus = userComuDao.seeUserComusByComu(comunidad.getC_Id()).blockingGet();
        assertThat(userComus.get(0).getComunidad(), is(comunidad));
    }

    @Test
    public void testSeeUserComusByUser_4()
    {
        whatClean = CLEAN_JUAN;
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        regUserComuWithTkCache(COMU_REAL_JUAN);
        assertThat(userComuDao.seeUserComusByUser().blockingGet(), hasItem(COMU_REAL_JUAN));
    }
}