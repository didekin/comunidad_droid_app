package com.didekindroid.comunidad;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.comunidad.testutil.ComuTestData;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.didekindroid.comunidad.ComunidadDao.comunidadDao;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekinlib.model.usuario.http.UsuarioExceptionMsg.USERCOMU_WRONG_INIT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:22
 */
@RunWith(AndroidJUnit4.class)
public class ComunidadDaoTest {

    private CleanUserEnum whatClean = CLEAN_PEPE;

    @After
    public void cleaningUp()
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testGetComuData() throws Exception
    {
        regComuUserUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad cDB = userComuDao.getComusByUser().blockingGet().get(0);
        Comunidad c1 = comunidadDao.getComuData(cDB.getC_Id()).blockingGet();
        assertThat(c1, is(cDB));
    }

    @Test
    public void testGetComuData_wrong() throws Exception
    {
        regComuUserUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
        comunidadDao.getComuData(999L).test()
                .assertError(exception -> UiException.class.cast(exception).getErrorHtppMsg().equals(USERCOMU_WRONG_INIT.getHttpMessage()));
    }

    @Test
    public void testSearchComunidades() throws Exception
    {
        regComuUserUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
        Comunidad comunidadSearch = ComuTestData.makeComunidad("Calle", "de la Plazuela", (short) 11, "",
                new Municipio((short) 13, new Provincia((short) 3)));

        List<Comunidad> comunidades = comunidadDao.searchInComunidades(comunidadSearch).blockingGet();

        assertThat(comunidades.size(), is(1));

        assertThat(comunidades.get(0).getNombreComunidad(), is("Travesía de la Plazuela 11"));
        assertThat(comunidades.get(0).getNombreVia(), CoreMatchers.is("de la Plazuela"));
        assertThat(comunidades.get(0).getTipoVia(), CoreMatchers.is("Travesía"));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getProvinciaId(), is((short) 3));
        assertThat(comunidades.get(0).getMunicipio().getProvincia().getNombre(), is("Alicante/Alacant"));
        assertThat(comunidades.get(0).getMunicipio().getCodInProvincia(), is((short) 13));
        assertThat(comunidades.get(0).getMunicipio().getNombre(), is("Algueña"));
    }
}