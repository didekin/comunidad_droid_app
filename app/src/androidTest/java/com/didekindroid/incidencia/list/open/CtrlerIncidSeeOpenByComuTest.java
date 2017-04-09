package com.didekindroid.incidencia.list.open;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.ViewerIncidListByComu;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.CoreMatchers;
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
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_C;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static io.reactivex.plugins.RxJavaPlugins.reset;
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
        controller = new CtrlerIncidSeeOpenByComu(new ViewerIncidSeeForTest(new ListView(activity), null, activity, null));
        incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
        incidencia = incidImportancia.getIncidencia();
    }

    @After
    public void clearUp() throws UiException
    {
        controller.clearSubscriptions();
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
        CtrlerIncidSeeOpenByComu controllerLocal = new CtrlerIncidSeeOpenByComu(new ViewerIncidSeeForTest(null, null, activity, null)){
            @Override
            public void onSuccessLoadItemsById(List<IncidenciaUser> incidOpenList)
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
            reset();
        }
        assertThat(controllerLocal.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testOnSuccessLoadItemsById() throws Exception
    {
        IncidenciaUser incidenciaUser_1 = new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(USER_PEPE).build();
        IncidenciaUser incidenciaUser_2 = new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(USER_JUAN).build();
        List<IncidenciaUser> incidOpenList = new ArrayList<>(2);
        incidOpenList.add(incidenciaUser_1);
        incidOpenList.add(incidenciaUser_2);

        // Preconditions
        assertThat(controller.adapter.getCount(), is(0));
        // Execute
        controller.onSuccessLoadItemsById(incidOpenList);
        // Check
        assertThat(controller.adapter.getCount(), is(2));
        assertThat(controller.getViewer().getViewInViewer().getAdapter(), CoreMatchers.<ListAdapter>is(controller.adapter));
    }

    @Test
    public void testSelectItem() throws Exception
    {
        CtrlerIncidSeeOpenByComu controllerLocal = new CtrlerIncidSeeOpenByComu(new ViewerIncidSeeForTest(null, null, activity, null)){
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
            reset();
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

    class ViewerIncidSeeForTest extends ViewerIncidListByComu {

        public ViewerIncidSeeForTest(ListView view, View emptyListView, Activity activity, ViewerIf parentViewer)
        {
            super(view, emptyListView, activity, parentViewer);
        }

        @Override
        public void replaceRootView(@NonNull Bundle bundle)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_C), is(BEFORE_METHOD_EXEC));
        }
    }

    void checkBundle(Bundle bundle)
    {
        short importancia = ((IncidImportancia) bundle.getSerializable(INCID_IMPORTANCIA_OBJECT.key)).getImportancia();
        assertThat(importancia, is((short) 3));
        UsuarioComunidad usuarioComunidad = ((IncidImportancia) bundle.getSerializable(INCID_IMPORTANCIA_OBJECT.key)).getUserComu();
        assertThat(usuarioComunidad, is(incidImportancia.getUserComu()));
        assertThat(bundle.getBoolean(INCID_RESOLUCION_FLAG.key), is(false));
    }
}
