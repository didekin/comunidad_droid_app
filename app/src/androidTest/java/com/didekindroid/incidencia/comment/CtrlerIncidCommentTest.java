package com.didekindroid.incidencia.comment;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.SingleObserverMock;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.incidencia.testutils.IncidTestData.doComment;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.lib_one.testutil.InitializerTestUtil.initSec_Http;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CtrlerIncidCommentTest {

    private CtrlerIncidComment controller;
    private Incidencia incidencia;

    @Before
    public void setUp() throws Exception
    {
        initSec_Http(getTargetContext());
        controller = new CtrlerIncidComment();
        incidencia = insertGetIncidImportancia(comu_real_rodrigo).getIncidencia();
    }

    @After
    public void clearUp()
    {
        resetAllSchedulers();
        assertThat(controller.clearSubscriptions(), is(0));
        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        execCheckSchedulersTest(ctrler -> ctrler.loadItemsByEntitiyId(new SingleObserverMock<>(), incidencia.getIncidenciaId()),
                controller);
    }

    @Test
    public void test_RegIncidComment() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.regIncidComment(new SingleObserverMock<>(), doComment("Comment_DESC", incidencia)),
                controller);
    }
}