package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.core.edit.ViewerIncidEditMaxFr.newViewerIncidEditMaxFr;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkScreenEditMaxPowerFr;
import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 10:08
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidEditMaxFrTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    IncidImportancia incidImportancia;
    boolean flagResolucion;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Perfil pro, inicidador de la incidencia.
                incidImportancia = insertGetIncidImportancia(COMU_REAL_JUAN);
                flagResolucion = false;
            } catch (IOException | UiException e) {
                fail();
            }

            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
            intent.putExtra(INCID_RESOLUCION_FLAG.key, flagResolucion);
            return intent;
        }
    };

    IncidEditAc activity;
    IncidEditMaxFr fragment;
    View frView;
    ViewerIncidEditMaxFr viewer;
    IncidenciaDataDbHelper dbHelper;
    int nextActivityId = R.id.incid_see_open_by_comu_ac;
    int onClickLinkImportanciaId = R.id.incid_see_usercomu_importancia_ac;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        fragment = (IncidEditMaxFr) activity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
        frView = fragment.getView();
        IncidImportancia incidImportanciaArg = (IncidImportancia) fragment.getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        boolean flagResolucionArg = fragment.getArguments().getBoolean(INCID_RESOLUCION_FLAG.key);
        assertThat(incidImportanciaArg, is(incidImportancia));
        assertThat(incidImportanciaArg.getUserComu(), notNullValue());
        assertThat(flagResolucionArg, is(flagResolucion));

        viewer = newViewerIncidEditMaxFr(frView, activity.viewer, flagResolucionArg);
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_JUAN);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testNewViewerIncidEditMaxFr() throws Exception
    {
        assertThat(viewer.hasResolucion, is(false));
        assertThat(viewer.getViewInViewer(), is(frView));
        assertThat(viewer.getController(), instanceOf(CtrlerIncidRegEditFr.class));
        assertThat(viewer.getParentViewer(), is(activity.getViewerAsParent()));
        assertThat(viewer.viewerAmbitoIncidSpinner, notNullValue());
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        execDoInViewer();

        assertThat(viewer.incidImportancia, is(incidImportancia));
        assertThat(viewer.incidenciaBean.getCodAmbitoIncid(), is(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(viewer.incidImportanciaBean.getImportancia(), is(incidImportancia.getImportancia()));

        checkScreenEditMaxPowerFr(incidImportancia, flagResolucion);
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);
    }

    @Test
    public void testOnClickLinkImportanciaUsers() throws Exception
    {
        // Preconditions:
        execDoInViewer();
        viewer.onClickLinkToImportanciaUsers(new LinkToImportanciaUsersListener(viewer));
        onView(withId(onClickLinkImportanciaId)).check(matches(isDisplayed()));
        intended(hasExtra(INCIDENCIA_OBJECT.key, incidImportancia.getIncidencia()));
    }

    @Test
    public void testOnClickButtonModify_1() throws Exception
    {
        // Preconditions:
        execDoInViewer();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.incidImportanciaBean.setImportancia((short) 11);
                viewer.onClickButtonModify();
            }
        });
        waitAtMost(2, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_importancia));
    }

    @Test
    public void testOnClickButtonModify_2() throws Exception
    {
        // Preconditions:
        execDoInViewer();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.incidImportanciaBean.setImportancia((short) 1);
                viewer.onClickButtonModify();
            }
        });
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(nextActivityId));
    }

    @Test
    public void testOnClickButtonErase_1() throws Exception
    {
        // Usuario iniciador de incidencia.
        assertThat(incidImportancia.getIncidencia().getUserName(), is(incidImportancia.getUserComu().getUsuario().getUserName()));
        // No usuario adm.
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(false));

        execDoInViewer();
        onView(withId(R.id.incid_edit_fr_borrar_txt)).check(matches(not(isDisplayed())));
        onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testOnClickButtonErase_2() throws Exception
    {
        // Preconditions.
        cleanOptions(CLEAN_JUAN);
        incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
        // Usuario adm.
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));

        execDoInViewer();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onClickButtonErase();
            }
        });
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(nextActivityId));
    }

    @Test
    public void testOnSuccessModifyIncidImportancia() throws Exception
    {
        viewer.onSuccessModifyIncidImportancia(1);
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(nextActivityId));
    }

    @Test
    public void testOnSuccessEraseIncidencia() throws Exception
    {
        viewer.onSuccessEraseIncidencia(1);
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(nextActivityId));
    }

    @Test
    public void testOnSuccessRegisterIncidencia()
    {
        try {
            viewer.onSuccessRegisterIncidImportancia(2);
            fail();
        } catch (Exception ue) {
            assertThat(ue, instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        addSubscription(viewer.viewerAmbitoIncidSpinner.getController());
        addSubscription(viewer.viewerImportanciaSpinner.getController());
        addSubscription(viewer.getController());

        assertThat(viewer.clearSubscriptions(), is(0));

        assertThat(viewer.viewerAmbitoIncidSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.viewerImportanciaSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void testSaveState() throws Exception
    {
        Bundle bundleTest = new Bundle();
        viewer.viewerAmbitoIncidSpinner.setItemSelectedId(11);
        viewer.viewerImportanciaSpinner.setItemSelectedId((short) 31);
        viewer.saveState(bundleTest);

        assertThat(bundleTest.getLong(AMBITO_INCIDENCIA_POSITION.key), is(11L));
        assertThat(bundleTest.getLong(INCID_IMPORTANCIA_NUMBER.key), is(31L));
    }

    @Test
    public void testOnStop()
    {
        AtomicInteger atomicInteger = new AtomicInteger(addSubscription(fragment.viewer.getController()).size());
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        atomicInteger.compareAndSet(1, fragment.viewer.getController().getSubscriptions().size());
        waitAtMost(2, SECONDS).untilAtomic(atomicInteger, is(0));
    }

    //    ............................... HELPERS .................................

    private void execDoInViewer()
    {
        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.doViewInViewer(null, incidImportancia);
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isRun);
    }
}