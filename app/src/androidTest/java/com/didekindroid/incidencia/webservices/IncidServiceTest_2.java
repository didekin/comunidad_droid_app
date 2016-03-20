package com.didekindroid.incidencia.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.AmbitoIncidencia;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Date;

import static com.didekin.common.exception.DidekinExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUserComuWithComunidadId;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 14:53
 */

/**
 * Tests con usario_comunidad sin autoridad 'adm'.
 */
@RunWith(AndroidJUnit4.class)
public class IncidServiceTest_2 {

    CleanUserEnum whatClean;
    Usuario pepe;
    UsuarioComunidad pepeUserComu;
    UsuarioComunidad juanUserComu;
    Incidencia incidencia_1;
    IncidImportancia incidImportancia_1;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_PEPE;
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testCloseIncidencia_1() throws InterruptedException, UiException
    {
        // Caso NOT OK: usuario no 'adm'.

        whatClean = CLEAN_JUAN_AND_PEPE;

        Resolucion resolucion = signPepeWithResolucion();
        try {
            IncidenciaServ.closeIncidencia(resolucion);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testDeleteIncidencia_1() throws UiException, InterruptedException
    {
        // Caso 1: usuario iniciador sin autoridad adm.
        signPepeWithIncidImportancia();

        try {
            IncidenciaServ.deleteIncidencia(incidImportancia_1.getIncidencia().getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testModifyIncidImportancia_1() throws UiException, InterruptedException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;
        // Caso OK: usuario sin registro de incidImportancia en BD, intenta modificar la incidencia, sin perfil administrador.
        // Resultado: inserta nuevo registro con importancia. No altera la incidencia.

        signPepeWithIncidencia();

        // Registro userComu en misma comunidad.
        UsuarioComunidad userComuJuan = makeUserComuWithComunidadId(COMU_REAL_JUAN, pepeUserComu.getComunidad().getC_Id());
        assertThat(ServOne.regUserAndUserComu(userComuJuan), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        Thread.sleep(1000);
        assertThat(IncidenciaServ.modifyIncidImportancia(new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder().copyIncidencia(incidencia_1).descripcion("modified_desc").build())
                        .usuarioComunidad(userComuJuan)
                        .importancia((short) 2)
                        .build()),
                is(1));

        IncidImportancia incidImportancia = IncidService.IncidenciaServ.seeIncidImportancia(incidencia_1.getIncidenciaId()).getIncidImportancia();
        assertThat(incidImportancia.getImportancia(), is((short) 2));
        assertThat(incidImportancia.getIncidencia().getDescripcion(), is(INCID_DEFAULT_DESC));
    }

    @Test
    public void testModifyIncidImportancia_2() throws UiException, InterruptedException
    {
        // Caso OK: usuario CON registro de incidImportancia en BD, intenta modificar la incidencia, sin perfil administrador, CON perfil iniciador.
        signPepeWithIncidImportancia();
        Thread.sleep(1000);

        assertThat(IncidenciaServ.modifyIncidImportancia(new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder().copyIncidencia(incidencia_1).descripcion("modified_desc").ambitoIncid(new AmbitoIncidencia((short) 16)).build())
                        .usuarioComunidad(pepeUserComu)
                        .importancia((short) 2)
                        .build()),
                is(2));
        IncidImportancia incidImportanciaBd = IncidenciaServ.seeIncidImportancia(incidencia_1.getIncidenciaId()).getIncidImportancia();
        assertThat(incidImportanciaBd.getImportancia(), is((short) 2));
        assertThat(incidImportanciaBd.getIncidencia().getDescripcion(), is("modified_desc"));
        assertThat(incidImportanciaBd.getIncidencia().getAmbitoIncidencia().getAmbitoId(), is((short) 16));
    }

    @Test
    public void testModifyResolucion_1() throws InterruptedException, UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso NOT OK: añadimos un avance a una resolución que no los tenía.
        Resolucion resolucion = signPepeWithResolucion();
        try {
            IncidenciaServ.modifyResolucion(resolucion);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testRegIncidImportancia_1() throws UiException, InterruptedException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        //Caso: la incidencia ya exite en BD. El registro de la nueva incidImportancia devuelve '1', no '2'.
        signPepeWithIncidencia();
        // Registro userComu en misma comunidad y lo asocio a la incidencia.
        UsuarioComunidad userComuJuan = makeUserComuWithComunidadId(COMU_REAL_JUAN, pepeUserComu.getComunidad().getC_Id());
        assertThat(ServOne.regUserAndUserComu(userComuJuan), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        userComuJuan = ServOne.getUserComuByUserAndComu(pepeUserComu.getComunidad().getC_Id());
        IncidImportancia newIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(incidencia_1)
                .usuarioComunidad(userComuJuan)
                .importancia((short) 2)
                .build();
        assertThat(IncidenciaServ.regIncidImportancia(newIncidImportancia), is(1));
    }

    @Test
    public void testRegIncidImportancia_2() throws UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso: incidencia.comunidad != usuario.comunidad.
        signPepeWithIncidencia();
        // Registramos userComu en otra comunidad.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        UsuarioComunidad juanUserComu = ServOne.seeUserComusByUser().get(0);
        try {
            new IncidImportancia.IncidImportanciaBuilder(incidencia_1)
                    .usuarioComunidad(juanUserComu)
                    .importancia((short) 2)
                    .build();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(INCID_IMPORTANCIA_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testRegResolucion_1() throws UiException, InterruptedException
    {
        // Caso: userComu sin funciones administrador.
        signPepeWithIncidImportancia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia_1, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        try {
            IncidenciaServ.regResolucion(resolucion);
            fail();
        } catch (UiException ie) {
            assertThat(ie.getInServiceException().getHttpMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testSeeResolucion_1() throws InterruptedException, UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        Resolucion resolucion = signPepeWithResolucion();
        assertThat(resolucion.getDescripcion(), is(RESOLUCION_DEFAULT_DESC));
        assertThat(resolucion.getCosteEstimado(), is(1122));
    }

//    ============================= HELPER METHODS ===============================

    private void signPepeWithIncidImportancia() throws UiException
    {
        signPepeWithIncidencia();
        // Verificamos poderes: true; solo hay una incidenciaUser.
        incidImportancia_1 = IncidenciaServ.seeIncidImportancia(incidencia_1.getIncidenciaId()).getIncidImportancia();
        assertThat(incidImportancia_1.isIniciadorIncidencia(), is(true));
    }

    private void signPepeWithIncidencia() throws UiException
    {
        signPepeWithIncidencia(COMU_REAL_PEPE);
    }

    private void signPepeWithIncidencia(UsuarioComunidad userComu) throws UiException
    {
        pepe = signUpAndUpdateTk(userComu);
        pepeUserComu = ServOne.seeUserComusByUser().get(0);
        // Insertamos incidencia.
        incidencia_1 = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
    }

    /**
     * Utiliad para tests donde el usario no tenga poderes adm y necesitemo un usuario que sí los tenga.
     */
    private Resolucion signPepeWithResolucion() throws UiException, InterruptedException
    {
        signPepeWithIncidencia(COMU_ESCORIAL_PEPE);
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia_1, RESOLUCION_DEFAULT_DESC, 1122, new Timestamp(new Date().getTime()));
        assertThat(IncidenciaServ.regResolucion(resolucion), is(1));

        // Registro userComu en misma comunidad y lo asocio a la incidencia.
        juanUserComu = makeUserComuWithComunidadId(COMU_ESCORIAL_JUAN, pepeUserComu.getComunidad().getC_Id());
        assertThat(ServOne.regUserAndUserComu(juanUserComu), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        juanUserComu = ServOne.getUserComuByUserAndComu(juanUserComu.getComunidad().getC_Id());
        assertThat(juanUserComu.hasAdministradorAuthority(), is(false));
        return IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
    }
}
