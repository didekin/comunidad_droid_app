package com.didekindroid.usuariocomunidad;

import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Test;

import retrofit2.Response;

import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN2;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuGetAuthTk;
import static com.didekindroid.usuariocomunidad.UserComuMockDao.userComuMockDao;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeUsuarioComunidad;
import static com.didekinlib.http.usuario.TkValidaPatterns.tkEncrypted_direct_symmetricKey_REGEX;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 13:10
 */
public class UserComuMockDaoTest {

    @Test
    public void testRegUserAndUserComu_1()
    {
        // Comunidad is associated to other user.
        regUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidad = userComuDao.getComusByUser().blockingGet().get(0);
        cleanWithTkhandler();

        UsuarioComunidad userComu = makeUsuarioComunidad(
                comunidad, USER_JUAN2,
                "portalB", null, "planta1", null,
                PROPIETARIO.function.concat(",").concat(PRESIDENTE.function));
        assertThat(tkEncrypted_direct_symmetricKey_REGEX.isPatternOk(userComuMockDao.regUserAndUserComu(userComu)
                        .map(Response::body)
                        .blockingGet()),
                is(true));

        cleanOptions(CLEAN_JUAN2_AND_PEPE);
    }

}