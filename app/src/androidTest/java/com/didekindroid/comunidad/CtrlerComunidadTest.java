package com.didekindroid.comunidad;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.MaybeObserverMock;
import com.didekindroid.lib_one.api.SingleObserverMock;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 17:11
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComunidadTest {

    private CtrlerComunidad controller;
    Comunidad comunidad;

    @Before
    public void setUp()
    {
        initSec_Http(getTargetContext());
        comunidad = signUpGetComu(COMU_ESCORIAL_PEPE);
        controller = new CtrlerComunidad();
    }

    @After
    public void clearUp()
    {
        resetAllSchedulers();
        assertThat(controller.clearSubscriptions(), is(0));
        cleanOptions(CLEAN_PEPE);
    }

    //    =================================== TESTS ===================================

    @Test
    public void test_getComunidadData() throws Exception
    {
        execCheckSchedulersTest(ctrler -> ctrler.getComunidadData(new SingleObserverMock<Comunidad>(), comunidad.getC_Id()), controller);
    }

    @Test
    public void test_searchInComunidades_1() throws Exception
    {
        execCheckSchedulersTest(ctrler -> ctrler.searchInComunidades(new SingleObserverMock<List<Comunidad>>(), comunidad), controller);
    }

    @Test
    public void test_ModifyComunidadData() throws Exception
    {
        Comunidad newComunidad = new Comunidad.ComunidadBuilder()
                .copyComunidadNonNullValues(comunidad).nombreVia("nuevo_nombre_via").build();
        execCheckSchedulersTest(ctrler -> ctrler.modifyComunidadData(new SingleObserverMock<Integer>(), newComunidad), controller);
    }

    @Test
    public void test_GetUserComu() throws Exception
    {
        execCheckSchedulersTest(ctrler -> ctrler.getUserComu(new MaybeObserverMock<>(), comunidad), controller);
    }
}