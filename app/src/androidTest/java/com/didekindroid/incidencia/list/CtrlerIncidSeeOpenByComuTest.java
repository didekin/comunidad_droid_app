package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.incidencia.list.CtrlerIncidSeeOpenByComu.incidImportancia;
import static com.didekindroid.incidencia.list.CtrlerIncidSeeOpenByComu.incidOpenList;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.lib_one.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 28/02/17
 * Time: 17:55
 */

public class CtrlerIncidSeeOpenByComuTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    Activity activity;
    Incidencia incidencia;
    CtrlerIncidSeeOpenByComu controller;
    IncidImportancia incidImportancia;

    @Before
    public void setUp() throws UiException, IOException
    {
        activity = activityRule.getActivity();
        controller = new CtrlerIncidSeeOpenByComu();
        incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
        incidencia = incidImportancia.getIncidencia();
    }

    @After
    public void clearUp() throws UiException
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    /* .................................... OBSERVABLES .................................*/

    @Test
    public void testIncidOpenList() throws UiException
    {
        incidOpenList(incidencia.getComunidad().getC_Id()).test()
                .assertOf(testObserver -> {
                    List<IncidenciaUser> list = testObserver.values().get(0);
                    assertThat(list.size(), is(1));
                    assertThat(list.get(0).getIncidencia().getDescripcion(), is(INCID_DEFAULT_DESC));
                    assertThat(list.get(0).getIncidencia().getImportanciaAvg(), is(3f));
                });
    }

    @Test
    public void testIncidImportancia() throws UiException
    {
        incidImportancia(incidencia).test()
                .assertOf(testObserver -> {
                    Bundle bundle = testObserver.values().get(0);
                    checkBundle(bundle);
                });
    }

    // ....................................INSTANCE METHODS ........................................

    @Test
    public void testLoadItemsByEntitiyId()
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<IncidenciaUser>>() {
                @Override
                public void onSuccess(List<IncidenciaUser> incidenciaUsers)
                {
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, incidencia.getComunidadId()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testSelectItem() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.selectItem(new DisposableSingleObserver<Bundle>() {
                @Override
                public void onSuccess(Bundle bundle)
                {
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(USER_PEPE).build()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    void checkBundle(Bundle bundle)
    {
        final IncidAndResolBundle resolBundle = (IncidAndResolBundle) bundle.getSerializable(INCID_RESOLUCION_BUNDLE.key);
        final IncidImportancia incidImportancia = resolBundle.getIncidImportancia();
        assertThat(incidImportancia.getImportancia(), is((short) 3));
        assertThat(incidImportancia.getUserComu(), is(this.incidImportancia.getUserComu()));
    }
}
