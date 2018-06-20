package com.didekindroid.incidencia.list;

import com.didekindroid.lib_one.api.SingleObserverMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidenciaUser;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpWithTkGetUserComu;

/**
 * User: pedro@didekin
 * Date: 28/02/17
 * Time: 17:55
 */

public class CtrlerIncidSeeOpenByComuTest {

    private CtrlerIncidSeeOpenByComu controller;

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
        controller = new CtrlerIncidSeeOpenByComu();
    }

    @After
    public void clearUp()
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    // ....................................INSTANCE METHODS ........................................

    @Test
    public void testLoadItemsByEntitiyId() throws Exception
    {
        // No hay incidencias.
        execCheckSchedulersTest(
                ctrler -> ctrler.loadItemsByEntitiyId(new SingleObserverMock<>(), signUpWithTkGetComu(COMU_ESCORIAL_PEPE).getC_Id()),
                controller
        );
    }

    @Test
    public void testSelectItem() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.selectItem(
                        new SingleObserverMock<>(),
                        insertGetIncidenciaUser(signUpWithTkGetUserComu(COMU_ESCORIAL_PEPE), (short) 3)),
                controller
        );
    }
}
