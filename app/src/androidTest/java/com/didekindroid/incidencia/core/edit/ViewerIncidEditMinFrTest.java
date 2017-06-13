package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeOpenAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeUserComuImportanciaAcLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
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
                signUpWithTkGetComu(COMU_REAL_PEPE);
                newIncidImportancia = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0), (short) 2);
                // Trucamos los datos: juan es usuario iniciador de la incidencia.
                newIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder().copyIncidencia(newIncidImportancia.getIncidencia()).userName(USER_JUAN.getUserName()).build())
                        .copyIncidImportancia(newIncidImportancia)
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
    IncidenciaDataDbHelper dbHelper;
    ViewerIncidEditMinFr viewer;

    public static Callable<Boolean> isDataFrDisplayed(final IncidenciaDataDbHelper dbHelper, final IncidEditAc activity, final IncidImportancia incidImportancia)
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                return checkDataEditMinFr(dbHelper, activity, incidImportancia);
            }
        };
    }

    public static Callable<Boolean> isFrDisplayed()
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                return checkScreenEditMinFr();
            }
        };
    }

    //    ============================  TESTS  ===================================

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        IncidEditMinFr fragment = (IncidEditMinFr) activity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);

        waitAtMost(4, SECONDS).until(isFrDisplayed());
        waitAtMost(4, SECONDS).until(isDataFrDisplayed(dbHelper, activity, newIncidImportancia));
        viewer = fragment.viewer;
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testNewViewerIncidEditMinFr() throws Exception
    {
        assertThat(viewer.getController(), instanceOf(CtrlerIncidRegEditFr.class));
        assertThat(viewer.getParentViewer(), allOf(
                CoreMatchers.<ViewerIf>is(activity.getViewerAsParent()),
                notNullValue()
        ));
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        assertThat(viewer.incidImportancia, is(newIncidImportancia));
        assertThat(viewer.incidImportanciaBean.getImportancia(), is(newIncidImportancia.getImportancia()));
    }

    @Test
    public void testOnClickLinkImportanciaUsers() throws Exception
    {
        viewer.onClickLinkToImportanciaUsers(new LinkToImportanciaUsersListener(viewer));
        onView(withId(incidSeeUserComuImportanciaAcLayout)).check(matches(isDisplayed()));
        intended(hasExtra(INCIDENCIA_OBJECT.key, newIncidImportancia.getIncidencia()));
    }

    /*
    *  Case: importancia == 0. Importancia is modified to 1.
    */
    @Test
    public void testOnClickButtonModify_1() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.incidImportanciaBean.setImportancia((short) 1);
                viewer.onClickButtonModify();
            }
        });
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(incidSeeOpenAcLayout));
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.incidImportanciaBean.setImportancia((short) 3);
                viewer.onClickButtonModify();
            }
        });
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(incidSeeOpenAcLayout));
    }

    @Test
    public void testOnSuccessRegisterIncidImportancia() throws Exception
    {
        viewer.onSuccessRegisterIncidImportancia(2);
        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(incidSeeOpenAcLayout)));
    }

    @Test
    public void testOnSuccessModifyIncidImportancia() throws Exception
    {
        viewer.onSuccessModifyIncidImportancia(1);
        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(incidSeeOpenAcLayout)));
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        checkSubscriptionsOnStop(activity, viewer.viewerImportanciaSpinner.getController(),
                viewer.getController());
    }

    //    ============================  HELPERS  ===================================

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
        checkSubscriptionsOnStop(activity, viewer.getController());
    }
}