package com.didekindroid.incidencia.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.AmbitoIncidencia;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.didekin.common.exception.DidekinExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekin.common.exception.DidekinExceptionMsg.USERCOMU_WRONG_INIT;
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
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
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
    public void testCloseIncidencia_1() throws InterruptedException, UiException, IOException
    {
        // Caso NOT OK: usuario no 'adm'.

        whatClean = CLEAN_JUAN_AND_PEPE;

        Resolucion resolucion = signPepeWithResolucion();
        try {
            IncidenciaServ.closeIncidencia(resolucion);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testDeleteIncidencia_1() throws UiException, InterruptedException, IOException
    {
        // Caso 1: usuario iniciador sin autoridad adm.
        signPepeWithIncidImportancia();

        try {
            IncidenciaServ.deleteIncidencia(incidImportancia_1.getIncidencia().getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testModifyIncidImportancia_1() throws UiException, InterruptedException, IOException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;
        // Caso OK: usuario sin registro de incidImportancia en BD, intenta modificar la incidencia, sin perfil administrador.
        // Resultado: inserta nuevo registro con importancia. No altera la incidencia.

        signPepeWithIncidencia();

        // Registro userComu en misma comunidad.
        UsuarioComunidad userComuJuan = makeUserComuWithComunidadId(COMU_REAL_JUAN, pepeUserComu.getComunidad().getC_Id());
        assertThat(ServOne.regUserAndUserComu(userComuJuan).execute().body(), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        Thread.sleep(1000);
        // Return 1: only one record inserted or updated.
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
    public void testModifyIncidImportancia_2() throws UiException, InterruptedException, IOException
    {
        // Caso OK: usuario CON registro de incidImportancia en BD, intenta modificar la incidencia, sin perfil administrador, CON perfil iniciador.
        signPepeWithIncidImportancia();
        Thread.sleep(1000);
        // Returns 2 : incidencia and incidImportancia records are both updated.
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
    public void testModifyResolucion_1() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso NOT OK: añadimos un avance a una resolución que no los tenía.
        Resolucion resolucion = signPepeWithResolucion();
        try {
            IncidenciaServ.modifyResolucion(resolucion);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testRegIncidImportancia_1() throws UiException, InterruptedException, IOException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        //Caso: la incidencia ya exite en BD. El registro de la nueva incidImportancia devuelve '1', no '2'.
        signPepeWithIncidencia();
        // Registro userComu en misma comunidad y lo asocio a la incidencia.
        UsuarioComunidad userComuJuan = makeUserComuWithComunidadId(COMU_REAL_JUAN, pepeUserComu.getComunidad().getC_Id());
        assertThat(ServOne.regUserAndUserComu(userComuJuan).execute().body(), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        userComuJuan = ServOne.getUserComuByUserAndComu(pepeUserComu.getComunidad().getC_Id());
        IncidImportancia newIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(incidencia_1)
                .usuarioComunidad(userComuJuan)
                .importancia((short) 2)
                .build();
        assertThat(IncidenciaServ.regIncidImportancia(newIncidImportancia), is(1));
    }

    @Test
    public void testRegIncidImportancia_2() throws UiException, IOException
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
    public void testRegResolucion_1() throws UiException, InterruptedException, IOException
    {
        // Caso: userComu sin funciones administrador.
        signPepeWithIncidImportancia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia_1, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        try {
            IncidenciaServ.regResolucion(resolucion);
            fail();
        } catch (UiException ie) {
            assertThat(ie.getErrorBean().getMessage(), is(UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
        }
    }

    @Test
    public void testSeeResolucion_1() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        Resolucion resolucion = signPepeWithResolucion();
        assertThat(resolucion.getDescripcion(), is(RESOLUCION_DEFAULT_DESC));
        assertThat(resolucion.getCosteEstimado(), is(1122));
    }

    @Test
    public void testSeeIncidClosedByComu() throws UiException, InterruptedException, IOException
    {
        // CASO OK: consulta por usuario no 'adm'.

        whatClean = CLEAN_JUAN_AND_PEPE;
        // Este método deja a Juan de usuario.
        Resolucion resolucion = signPepeWithResolucion();

        // Pepe es adm.
        updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
        assertThat(IncidenciaServ.closeIncidencia(resolucion), is(2));

        // Cambiamos a Juan.
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        List<IncidenciaUser> incidenciaUsers = IncidenciaServ.seeIncidsClosedByComu(juanUserComu.getComunidad().getC_Id());
        assertThat(incidenciaUsers.size(), is(1));
        assertThat(incidenciaUsers.get(0).getIncidencia(), is(resolucion.getIncidencia()));
    }

    @Test
    public void testSeeUserComusImportancia_1() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        signPepeWithIncidencia(COMU_ESCORIAL_PEPE);
        // Registro userComu Juan en misma comunidad.
        juanUserComu = makeUserComuWithComunidadId(COMU_ESCORIAL_JUAN, pepeUserComu.getComunidad().getC_Id());
        assertThat(ServOne.regUserAndUserComu(juanUserComu).execute().body(), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        juanUserComu = ServOne.getUserComuByUserAndComu(juanUserComu.getComunidad().getC_Id());
        // Añado registro incidImportancia de Juan.
        insertGetIncidenciaUser(incidencia_1.getIncidenciaId(), juanUserComu, 4);
        assertThat(IncidenciaServ.seeUserComusImportancia(incidencia_1.getIncidenciaId()).size(), is(2));
    }

    @Test
    public void testSeeUserComusImportancia_2() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso: incidencia.comunidad != usuario.comunidad.
        signPepeWithIncidencia(COMU_ESCORIAL_PEPE);
        // Registramos userComu en otra comunidad.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        try {
            IncidenciaServ.seeUserComusImportancia(incidencia_1.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }


//    ============================= HELPER METHODS ===============================

    private void signPepeWithIncidImportancia() throws UiException, IOException
    {
        signPepeWithIncidencia();
        // Verificamos poderes: true; solo hay una incidenciaUser.
        incidImportancia_1 = IncidenciaServ.seeIncidImportancia(incidencia_1.getIncidenciaId()).getIncidImportancia();
        assertThat(incidImportancia_1.isIniciadorIncidencia(), is(true));
    }

    private void signPepeWithIncidencia() throws UiException, IOException
    {
        signPepeWithIncidencia(COMU_REAL_PEPE);
    }

    private void signPepeWithIncidencia(UsuarioComunidad userComu) throws UiException, IOException
    {
        pepe = signUpAndUpdateTk(userComu);
        pepeUserComu = ServOne.seeUserComusByUser().get(0);
        // Insertamos incidencia.
        incidencia_1 = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
    }

    /**
     * Utiliad para tests donde el usario no tenga poderes adm y necesitemos un usuario que sí los tenga.
     */
    private Resolucion signPepeWithResolucion() throws UiException, InterruptedException, IOException
    {
        signPepeWithIncidencia(COMU_ESCORIAL_PEPE);
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia_1, RESOLUCION_DEFAULT_DESC, 1122, new Timestamp(new Date().getTime()));
        assertThat(IncidenciaServ.regResolucion(resolucion), is(1));

        // Registro userComu en misma comunidad.
        juanUserComu = makeUserComuWithComunidadId(COMU_ESCORIAL_JUAN, pepeUserComu.getComunidad().getC_Id());
        assertThat(ServOne.regUserAndUserComu(juanUserComu).execute().body(), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        juanUserComu = ServOne.getUserComuByUserAndComu(juanUserComu.getComunidad().getC_Id());
        assertThat(juanUserComu != null && juanUserComu.hasAdministradorAuthority(), is(false));
        return IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
    }
}
