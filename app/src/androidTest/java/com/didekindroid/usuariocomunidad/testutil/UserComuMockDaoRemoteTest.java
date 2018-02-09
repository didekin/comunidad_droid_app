package com.didekindroid.usuariocomunidad.testutil;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Test;

import java.io.IOException;

import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMockDaoRemote.userComuMockDao;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 17:34
 */
public class UserComuMockDaoRemoteTest {

    @Test
    public void test_DeleteUser() throws Exception
    {
        assertThat(userComuMockDao.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body(), is(true));
        assertThat(userComuMockDao.deleteUser(USER_JUAN.getUserName()).execute().body(), is(true));
    }

    @Test
    public void testRegComuAndUserAndUserComu() throws Exception
    {
        assertThat(userComuMockDao.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body(), is(true));
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testRegUserAndUserComu_1() throws UiException, IOException
    {
        // Comunidad is associated to other user.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = userComuDaoRemote.getComusByUser().get(0);
        cleanWithTkhandler();

        UsuarioComunidad userComu = makeUsuarioComunidad(comunidad, USER_JUAN2,
                "portalB", null, "planta1", null, PRO.function.concat(",").concat(PRE.function));
        assertThat(userComuMockDao.regUserAndUserComu(userComu).execute().body(), is(true));

        cleanOptions(CLEAN_JUAN2_AND_PEPE);
    }
}