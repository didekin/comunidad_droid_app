package com.didekindroid.incidencia.list.open;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.incidencia.list.open.CtrlerIncidSeeOpenByComu.incidImportancia;
import static com.didekindroid.incidencia.list.open.CtrlerIncidSeeOpenByComu.incidOpenList;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.testutil.ActivityTestUtils.doListView;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_C;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_D;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
    UsuarioComunidad pepeUserComu;
    IncidImportancia incidImportancia;

    @Before
    public void setUp() throws UiException, IOException
    {
        activity = activityRule.getActivity();
        controller = new CtrlerIncidSeeOpenByComu(new ViewerIncidSeeForTest(activity));
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
                .assertOf(new Consumer<TestObserver<List<IncidenciaUser>>>() {
                    @Override
                    public void accept(TestObserver<List<IncidenciaUser>> testObserver) throws Exception
                    {
                        List<IncidenciaUser> list = testObserver.values().get(0);
                        assertThat(list.size(), is(1));
                        assertThat(list.get(0).getIncidencia().getDescripcion(), is(INCID_DEFAULT_DESC));
                        assertThat(list.get(0).getIncidencia().getImportanciaAvg(), is(3f));
                    }
                });
    }

    @Test
    public void testIncidImportancia() throws UiException
    {
        incidImportancia(incidencia).test()
                .assertOf(new Consumer<TestObserver<Bundle>>() {
                    @Override
                    public void accept(TestObserver<Bundle> testObserver) throws Exception
                    {
                        Bundle bundle = testObserver.values().get(0);
                        checkBundle(bundle);
                    }
                });
    }

    // ....................................INSTANCE METHODS ........................................

    @Test
    public void testLoadItemsByEntitiyId()
    {
        CtrlerIncidSeeOpenByComu controllerLocal = new CtrlerIncidSeeOpenByComu(new ViewerIncidSeeForTest(activity)) {
            @Override
            public void onSuccessLoadItemsInList(List<IncidenciaUser> incidOpenList)
            {
                assertThat(incidOpenList, notNullValue());
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }
        };

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controllerLocal.loadItemsByEntitiyId(incidencia.getComunidadId()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controllerLocal.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testOnSuccessLoadItemsById() throws Exception
    {
        // Execute
        controller.onSuccessLoadItemsInList(new ArrayList<IncidenciaUser>());
        // Check
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_D));
    }

    @Test
    public void testSelectItem() throws Exception
    {
        CtrlerIncidSeeOpenByComu controllerLocal = new CtrlerIncidSeeOpenByComu(new ViewerIncidSeeForTest(activity)) {
            @Override
            public void onSuccessSelectedItem(@NonNull Bundle bundle)
            {
                checkBundle(bundle);
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
            }
        };

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controllerLocal.selectItem(new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(USER_PEPE).build()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
        assertThat(controllerLocal.getSubscriptions().size(), is(1));
    }

    @Test
    public void testOnSuccessSelectedItem() throws Exception
    {
        controller.onSuccessSelectedItem(new Bundle());
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_C));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    void checkBundle(Bundle bundle)
    {
        final IncidImportancia incidImportancia = (IncidImportancia) bundle.getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        assertThat(incidImportancia.getImportancia(), is((short) 3));
        assertThat(incidImportancia.getUserComu(), is(this.incidImportancia.getUserComu()));
        assertThat(bundle.getBoolean(INCID_RESOLUCION_FLAG.key), is(false));
    }

    class ViewerIncidSeeForTest extends ViewerIncidSeeOpen {

        ViewerIncidSeeForTest(Activity activity)
        {
            super(doListView(R.layout.mock_list_fr), activity);
        }

        @Override
        public void replaceComponent(@NonNull Bundle bundle)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_C), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void onSuccessLoadItems(List<IncidenciaUser> incidCloseList)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_D), is(BEFORE_METHOD_EXEC));
        }
    }
}
