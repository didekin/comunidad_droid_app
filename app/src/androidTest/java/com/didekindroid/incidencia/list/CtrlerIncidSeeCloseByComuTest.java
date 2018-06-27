package com.didekindroid.incidencia.list;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.SingleObserverMock;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.incidencia.testutils.IncidTestData.doSimpleIncidenciaUser;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetDefaultResolucion;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetUserComu;

/**
 * User: pedro@didekin
 * Date: 14/02/17
 * Time: 13:25
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidSeeCloseByComuTest {

    private CtrlerIncidSeeCloseByComu controller;

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
        controller = new CtrlerIncidSeeCloseByComu();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    /* ............................ INSTANCE METHODS ...............................*/

    @Test
    public void testLoadItemsByEntitiyId() throws Exception
    {
        // No hay incidencias.
        execCheckSchedulersTest(
                ctrler -> ctrler.loadItemsByEntitiyId(new SingleObserverMock<>(), signUpGetComu(COMU_ESCORIAL_PEPE).getC_Id()),
                controller
        );
    }

    @Test
    public void testSelectItem() throws Exception
    {
        UsuarioComunidad userComu = signUpGetUserComu(COMU_ESCORIAL_PEPE);
        Resolucion resolucion = insertGetDefaultResolucion(userComu);
        execCheckSchedulersTest(
                ctrler -> ctrler.selectItem(
                        new SingleObserverMock<>(),
                        doSimpleIncidenciaUser(
                                resolucion.getIncidencia().getIncidenciaId(),
                                resolucion.getIncidencia().getFechaAlta(),
                                userComu.getUsuario().getuId(),
                                resolucion.getFechaPrev())),
                controller
        );
    }
}