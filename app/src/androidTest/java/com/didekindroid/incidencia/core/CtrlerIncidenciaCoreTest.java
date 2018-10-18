package com.didekindroid.incidencia.core;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.DidekinApp;
import com.didekindroid.lib_one.api.MaybeObserverMock;
import com.didekindroid.lib_one.api.SingleObserverMock;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Instrumentation.newApplication;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.incidencia.testutils.IncidTestData.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidTestData.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidenciaUser;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.getMilliSecondsFromCalendarAdd;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetUserComu;
import static java.util.Calendar.SECOND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/04/17
 * Time: 12:14
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidenciaCoreTest {

    private CtrlerIncidenciaCore controller;
    private AtomicBoolean toClean = new AtomicBoolean(false);

    @Before
    public void setUp() throws Exception
    {
        getInstrumentation().callApplicationOnCreate(newApplication(DidekinApp.class, getTargetContext()));
        controller = new CtrlerIncidenciaCore();
        toClean.set(true);
        waitAtMost(2, SECONDS).until(() -> toClean.get());
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        if (toClean.get()) {
            cleanOptions(CLEAN_PEPE);
        }
    }

    //    ============================= TESTS ===============================

    @Test
    public void test_CloseIncidencia() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.closeIncidencia(new SingleObserverMock<>(), insertGetDefaultResolucion(signUpGetUserComu(COMU_ESCORIAL_PEPE))),
                controller
        );
    }

    @Test
    public void test_EraseIncidencia() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.eraseIncidencia(new SingleObserverMock<>(), insertGetIncidImportancia(COMU_REAL_PEPE).getIncidencia()),
                controller
        );
    }

    @Test
    public void testGetAmbitoIncidDesc()
    {
        assertThat(controller.getAmbitoIncidDesc((short) 9), is("Buzones"));
        toClean.set(false);
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        // No hay registros de incidImportancia; lista vacía.
        execCheckSchedulersTest(
                ctrler -> ctrler.loadItemsByEntitiyId(new SingleObserverMock<>(), signUpGetComu(COMU_ESCORIAL_PEPE).getC_Id()),
                controller
        );
    }

    @Test
    public void testModifyIncidImportancia() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.modifyIncidImportancia(new SingleObserverMock<>(), insertGetIncidImportancia(COMU_ESCORIAL_PEPE)),
                controller
        );
    }

    @Test
    public void test_ModifyResolucion() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.modifyResolucion(new SingleObserverMock<>(), insertGetDefaultResolucion(signUpGetUserComu(COMU_ESCORIAL_PEPE))),
                controller
        );
    }

    @Test
    public void test_regIncidImportancia() throws Exception
    {
        UsuarioComunidad userComu = signUpGetUserComu(COMU_REAL_PEPE);
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComu.getUsuario().getUserName(), INCID_DEFAULT_DESC, userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia((short) 1)
                .build();

        execCheckSchedulersTest(
                ctrler -> ctrler.regIncidImportancia(new SingleObserverMock<>(), incidImportancia),
                controller
        );
    }

    @Test
    public void test_SeeResolucion() throws Exception
    {
        // Incidencia
        execCheckSchedulersTest(
                ctrler -> ctrler.seeResolucion(
                        new MaybeObserverMock<>(),
                        insertGetDefaultResolucion(signUpGetUserComu(COMU_ESCORIAL_PEPE)).getIncidencia().getIncidenciaId()),
                controller
        );
    }

    @Test
    public void test_RegResolucion() throws Exception
    {
        Resolucion resolucion = doResolucion(
                insertGetIncidenciaUser(signUpGetUserComu(COMU_ESCORIAL_PEPE), 2).getIncidencia(),
                "resol_desc", 1000,
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 30))
        );
        execCheckSchedulersTest(ctrler -> ctrler.regResolucion(new SingleObserverMock<>(), resolucion), controller);
    }
}