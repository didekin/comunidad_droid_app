package com.didekindroid.comunidad;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.comunidad.testutil.ComuTestData;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestData;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.didekindroid.comunidad.ComunidadDao.comunidadDao;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:22
 */
@RunWith(AndroidJUnit4.class)
public class ComunidadDaoTest {

    CleanUserEnum whatClean;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_NOTHING;
    }

    @After
    public void cleaningUp() throws UiException
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testGetComuData() throws UiException, IOException
    {
        whatClean = CLEAN_PEPE;

        UserComuTestData.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDB = userComuDao.getComusByUser().get(0);
        Comunidad c1 = comunidadDao.getComuData(cDB.getC_Id());
        assertThat(c1, is(cDB));
    }

    @Test
    public void testSearchComunidades() throws Exception
    {
        UserComuTestData.signUpAndUpdateTk(COMU_REAL_JUAN);
        UserComuTestData.signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidadSearch = ComuTestData.makeComunidad("Calle", "de la Plazuela", (short) 11, "",
                new Municipio((short) 13, new Provincia((short) 3)));

        List<Comunidad> comunidades = comunidadDao.searchComunidades(comunidadSearch).execute().body();

        assertThat(comunidades.size(), is(1));

        assertThat(comunidades.get(0).getNombreComunidad(), is("Travesía de la Plazuela 11"));
        assertThat(comunidades.get(0).getNombreVia(), CoreMatchers.is("de la Plazuela"));
        assertThat(comunidades.get(0).getTipoVia(), CoreMatchers.is("Travesía"));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getProvinciaId(), is((short) 3));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getNombre(), is("Alicante/Alacant"));
        assertThat(comunidades.get(0).getMunicipio().getCodInProvincia(), is((short) 13));
        assertThat(comunidades.get(0).getMunicipio().getNombre(), is("Algueña"));

        whatClean = CLEAN_JUAN_AND_PEPE;
    }

}