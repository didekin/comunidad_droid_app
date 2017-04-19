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
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

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
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkDataEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkScreenEditMinFr;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 09/04/17
 * Time: 14:42
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidEditMinFrTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    IncidImportancia newIncidImportancia;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Perfil pro, iniciador incidencia.
                IncidImportancia oldIncidImportancia = insertGetIncidImportancia(COMU_REAL_PEPE);
                // Perfil pro, no iniciador.
                UsuarioComunidad userComuJuan = makeUsuarioComunidad(oldIncidImportancia.getIncidencia().getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                userComuDaoRemote.regUserAndUserComu(userComuJuan).execute();
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                // Nueva incidImportancia.
                newIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(oldIncidImportancia.getIncidencia())
                        .usuarioComunidad(userComuDaoRemote.seeUserComusByUser().get(0))
                        .importancia((short) 0)
                        .build();
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, newIncidImportancia);
            return intent;
        }
    };

    IncidEditAc activity;
    IncidEditMinFr fragment;
    View frView;
    ViewerIncidEditMinFr viewer;
    IncidenciaDataDbHelper dbHelper;
    int nextActivityId = R.id.incid_see_open_by_comu_ac;
    int onClickLinkImportanciaId = R.id.incid_see_usercomu_importancia_ac;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        fragment = (IncidEditMinFr) activity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
        frView = fragment.getView();

        viewer = ViewerIncidEditMinFr.newViewerIncidEditMinFr(frView, activity.viewer);
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testNewViewerIncidEditMinFr() throws Exception
    {
        assertThat(viewer.getViewInViewer(), is(frView));
        assertThat(viewer.getController(), instanceOf(CtrlerIncidRegEditFr.class));
        assertThat(viewer.getParentViewer(), is(activity.getViewerAsParent()));
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        execDoInViewer();

        assertThat(viewer.incidImportancia, is(newIncidImportancia));
        assertThat(viewer.incidImportanciaBean.getImportancia(), is(newIncidImportancia.getImportancia()));
        checkScreenEditMinFr();
        checkDataEditMinFr(dbHelper, activity, newIncidImportancia);
    }

    @Test
    public void testOnClickLinkImportanciaUsers() throws Exception
    {
        execDoInViewer();
        viewer.onClickLinkToImportanciaUsers(new LinkToImportanciaUsersListener(viewer));
        onView(withId(onClickLinkImportanciaId)).check(matches(isDisplayed()));
        intended(hasExtra(INCIDENCIA_OBJECT.key, newIncidImportancia.getIncidencia()));
    }

    /*
    *  Case: importancia == 0. Importancia is modified to 1.
    */
    @Test
    public void testOnClickButtonModify_1() throws Exception
    {
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

    /*
    *  Case: importancia > 0. Also, the incidImportancia instance has not been saved in BD previously.
    *  Importancia is modified to 3.
    */
    @Test
    public void testOnClickButtonModify_2() throws Exception
    {
        newIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(newIncidImportancia.getIncidencia())
                .copyIncidImportancia(newIncidImportancia)
                .importancia((short) 2)
                .build();
        execDoInViewer();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.incidImportanciaBean.setImportancia((short) 3);
                viewer.onClickButtonModify();
            }
        });
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(nextActivityId));
    }

    @Test
    public void testOnSuccessRegisterIncidImportancia() throws Exception
    {
        viewer.onSuccessRegisterIncidImportancia(2);
        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(nextActivityId)));
    }

    @Test
    public void testOnSuccessModifyIncidImportancia() throws Exception
    {
        viewer.onSuccessModifyIncidImportancia(1);
        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(nextActivityId)));
    }

    @Test
    public void testOnSuccessEraseIncidencia() throws Exception
    {
        try {
            viewer.onSuccessEraseIncidencia(1);
            fail();
        } catch (Exception ue) {
            assertThat(ue, instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        addSubscription(viewer.viewerImportanciaSpinner.getController());
        addSubscription(viewer.getController());

        assertThat(viewer.clearSubscriptions(), is(0));

        assertThat(viewer.viewerImportanciaSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void testSaveState() throws Exception
    {
        Bundle bundleTest = new Bundle();
        viewer.viewerImportanciaSpinner.setItemSelectedId((short) 31);

        viewer.saveState(bundleTest);

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
                viewer.doViewInViewer(null, newIncidImportancia);
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isRun);
    }
}