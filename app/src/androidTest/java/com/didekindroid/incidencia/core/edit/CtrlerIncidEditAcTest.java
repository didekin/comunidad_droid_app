package com.didekindroid.incidencia.core.edit;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.MaybeObserverMock;
import com.didekindroid.api.SingleObserverMock;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.didekindroid.incidencia.IncidObservable.incidImportanciaByUsers;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/04/17
 * Time: 12:14
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidEditAcTest {

    CtrlerIncidEditAc controller;
    long incidenciaId;

    @Before
    public void setUp() throws Exception
    {
        IncidAndResolBundle resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_ESCORIAL_PEPE), false);
        incidenciaId = resolBundle.getIncidImportancia().getIncidencia().getIncidenciaId();
        controller = new CtrlerIncidEditAc();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================= TESTS ===============================

    @Test
    public void testSeeResolucion() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.seeResolucion(new MaybeObserverMock<Resolucion>(), incidenciaId), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadItemsByEntitiyId(
                    incidImportanciaByUsers(incidenciaId),
                    new SingleObserverMock<List<ImportanciaUser>>(),
                    incidenciaId),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }
}