package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMinFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
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
 * <p>
 * This test used a only one user in DB: pepe.
 * User Juan is used only to launch the behavior imposed by the internal structure of the IncidenciaResolBundle
 * received in the activity.
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidEditMinFrTest {

    IncidAndResolBundle resolBundle;
    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpWithTkGetComu(COMU_REAL_PEPE);
                IncidImportancia incidImportancia = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0), (short) 2);
                // Premisa: usuario no iniciador y usuario no ADM.
                assertThat(COMU_REAL_JUAN.hasAdministradorAuthority(), is(false));
                incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder()
                                .copyIncidencia(incidImportancia.getIncidencia())
                                .userName(USER_JUAN.getUserName())  // This change converts Pepe in a non-adm user, without initiator status.
                                .build())
                        .copyIncidImportancia(incidImportancia)
                        .build();
                assertThat(incidImportancia.isIniciadorIncidencia(), is(false));
                resolBundle = new IncidAndResolBundle(incidImportancia, false);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
            return intent;
        }
    };
    IncidEditAc activity;
    IncidenciaDataDbHelper dbHelper;
    ViewerIncidEditMinFr viewer;


    //    ============================  TESTS  ===================================

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        IncidEditMinFr fragment = (IncidEditMinFr) activity.getSupportFragmentManager().findFragmentByTag(IncidEditMinFr.class.getName());

        AtomicReference<ViewerIncidEditMinFr> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, fragment.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
        viewer = viewerAtomic.get();
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testNewViewerIncidEditMinFr() throws Exception
    {
        assertThat(viewer.getController(), instanceOf(CtrlerIncidenciaCore.class));
        assertThat(viewer.getParentViewer(), allOf(
                is(activity.getParentViewer()),
                notNullValue()
        ));
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        assertThat(viewer.hasResolucion.get(), is(false));

        IncidImportancia incidImportancia = viewer.resolBundle.getIncidImportancia();
        assertThat(incidImportancia, is(resolBundle.getIncidImportancia()));
        assertThat(viewer.incidenciaBean.getCodAmbitoIncid(), is(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(viewer.incidImportanciaBean.getImportancia(), is(incidImportancia.getImportancia()));

        checkDataEditMinFr(dbHelper, activity, incidImportancia);
    }

    /*
    *  Case: importancia == 0. Importancia is modified to 1.
    */
    @Test
    public void testOnClickButtonModify_1() throws Exception
    {
        activity.runOnUiThread(() -> {
            viewer.incidImportanciaBean.setImportancia((short) 1);
            viewer.onClickButtonModify();
        });
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(incidSeeByComuAcLayout));
    }

    @Test
    public void testOnSuccessModifyIncidImportancia() throws Exception
    {
        viewer.onSuccessModifyIncidImportancia(1);
        waitAtMost(3, SECONDS).until(isViewDisplayedAndPerform(withId(incidSeeByComuAcLayout)));
    }

    @Test
    public void test_saveState() throws Exception
    {
        Bundle bundleTest = new Bundle();
        viewer.viewerImportanciaSpinner.setSelectedItemId((short) 31);

        viewer.saveState(bundleTest);
        assertThat(bundleTest.getLong(INCID_IMPORTANCIA_NUMBER.key), is(31L));
    }

    //    ============================  LIFE CYCLE TESTS  ===================================

    /* We check that all the viewers' controllers are invoked, as the result of invoking the method viewer.clearSubscriptions.
     * It serves also as a test on the activity's onStop() method. */
    @Test
    public void testClearSubscriptions() throws Exception
    {
        checkSubscriptionsOnStop(activity, viewer.viewerImportanciaSpinner.getController(), viewer.getController());
    }
}