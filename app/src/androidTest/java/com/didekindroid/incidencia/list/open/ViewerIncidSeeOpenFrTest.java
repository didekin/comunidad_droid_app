package com.didekindroid.incidencia.list.open;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.list.close.ViewerIncidSeeCloseFrTest.checkOnSuccessLoadItems;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidOpenListView;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceComponent;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/03/17
 * Time: 18:10
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidSeeOpenFrTest {

    Resolucion resolucion;
    IncidenciaUser incidenciaUser;
    IncidImportancia incidImportancia;

    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                incidImportancia = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0),
                        (short) 2);
                // Cierre incidencias..
                resolucion = insertGetResolucionNoAdvances(incidImportancia);
                incidenciaUser = incidenciaDao
                        .seeIncidsOpenByComu(incidImportancia.getIncidencia().getComunidadId()).get(0);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    IncidSeeOpenByComuAc activity;
    IncidSeeOpenByComuFr fragment;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeOpenByComuFr) activity.getSupportFragmentManager()
                .findFragmentByTag(IncidSeeOpenByComuFr.class.getName());
        // Wait until everything is ready.
        waitAtMost(3, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia, activity, incidenciaUser.getFechaAltaResolucion())));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================  TESTS INTEGRATION  ===================================

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        // GcmToken.
        waitAtMost(3, SECONDS).until(
                ((CtrlerFirebaseTokenIf) fragment.viewer.viewerFirebaseToken.getController())::isGcmTokenSentServer);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testNewViewerIncidSeeOpen() throws Exception
    {
        assertThat(fragment.viewer.getController(), allOf(
                notNullValue(), instanceOf(CtrlerIncidSeeOpenByComu.class)
        ));
        assertThat(fragment.viewer.getComuSpinner(), notNullValue());
        assertThat(fragment.viewer.viewerFirebaseToken, notNullValue());
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        checkSubscriptionsOnStop(activity, fragment.viewer.viewerFirebaseToken.getController(),
                fragment.viewer.getController());
    }

    @Test
    public void test_OnSuccessLoadSelectedItem() throws Exception
    {
        // Preconditions.
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, resolucion != null));
        // Exec and check.
        checkViewerReplaceComponent(fragment.viewer, incidEditAcLayout, bundle);
    }

    @Test
    public void test_OnSuccessLoadItems() throws Exception
    {
        checkOnSuccessLoadItems(incidImportancia, activity, fragment.viewer);
    }
}