package com.didekindroid.incidencia.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.oauth2.Rol;
import com.didekin.incidservice.domain.IncidenciaUser;
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
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_EL_ESCORIAL;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

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
    public void testIncidSeeByComu() throws UiException
    {
        IncidenciaUser incidPepeUserComu1 = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                .usuario(pepeUserComu.getUsuario())
                .importancia((short) 3)
                .build();
        assertThat(IncidenciaServ.regIncidenciaUser(incidPepeUserComu1), is(1));
        IncidenciaUser incidPepeUserComu2 = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia Two", pepeUserComu.getComunidad().getC_Id(), (short) 11))
                .usuario(USER_PEPE)
                .importancia((short) 2)
                .build();
        assertThat(IncidenciaServ.regIncidenciaUser(incidPepeUserComu2), is(1));

        assertThat(IncidenciaServ.incidSeeByComu(pepeUserComu.getComunidad().getC_Id()).size(),is(2));
    }

    @Test
    public void testRegIncidenciaUserComu() throws Exception
    {
        assertThat(pepeUserComu, notNullValue());
        IncidenciaUser incidPepeUserComu = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                .usuario(pepeUserComu.getUsuario())
                .importancia((short) 3)
                .build();
        assertThat(IncidenciaServ.regIncidenciaUser(incidPepeUserComu), is(1));
    }
}
