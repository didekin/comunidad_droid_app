package com.didekindroid.incidencia.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.oauth2.Rol;
import com.didekin.incidservice.domain.IncidUserComu;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.incidencia.dominio.IncidenciaDomainTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 14:53
 */
@RunWith(AndroidJUnit4.class)
public class IncidServiceTest {

    CleanUserEnum whatClean;
    UsuarioComunidad pepeUserComu;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_PEPE;
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        pepeUserComu = ServOne.seeUserComusByUser().get(0);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testGetHighestRolFunction() throws UiException
    {
        assertThat(IncidenciaServ.getHighestRolFunction(pepeUserComu.getComunidad().getC_Id()), is(Rol.PRESIDENTE.function));
    }

    @Test
    public void testIncidSeeByUser() throws UiException
    {
        IncidUserComu incidPepeUserComu1 = new IncidUserComu(doIncidencia("Incidencia One", (short) 43), pepeUserComu, (short) 3, null);
        assertThat(IncidenciaServ.regIncidenciaUserComu(incidPepeUserComu1), is(1));
        IncidUserComu incidPepeUserComu2 = new IncidUserComu(doIncidencia("Incidencia Two", (short) 11), pepeUserComu, (short) 2, null);
        assertThat(IncidenciaServ.regIncidenciaUserComu(incidPepeUserComu2), is(1));

        assertThat(IncidenciaServ.incidSeeByUser().size(),is(2));
    }

    @Test
    public void testRegIncidenciaUserComu() throws Exception
    {
        assertThat(pepeUserComu, notNullValue());
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad.UserComuBuilder(pepeUserComu.getComunidad(), null).userComuRest(pepeUserComu).build();
        IncidUserComu incidPepeUserComu = new IncidUserComu(doIncidencia("Incidencia One", (short) 43), usuarioComunidad, (short) 3, null);
        assertThat(IncidenciaServ.regIncidenciaUserComu(incidPepeUserComu), is(1));
    }
}
