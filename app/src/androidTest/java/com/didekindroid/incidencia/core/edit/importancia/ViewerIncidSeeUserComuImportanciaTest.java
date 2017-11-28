package com.didekindroid.incidencia.core.edit.importancia;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.didekindroid.incidencia.core.edit.CtrlerIncidEditAc;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkImportanciaUser;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 23/10/2017
 * Time: 17:34
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidSeeUserComuImportanciaTest {

    IncidEditAc activity;
    IncidImportancia incidImportancia;
    ViewerIncidSeeUserComuImportancia viewer;

    @Before
    public void setUp() throws Exception
    {
        // Perfil adm, inicidador de la incidencia.  NO resolución.
        IncidAndResolBundle resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_PLAZUELA5_JUAN), false);
        incidImportancia = resolBundle.getIncidImportancia();

        Intent intent = new Intent(getTargetContext(), IncidEditAc.class);
        intent.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);

        TKhandler.updateIsRegistered(true);
        activity = (IncidEditAc) getInstrumentation().startActivitySync(intent);
        viewer = (ViewerIncidSeeUserComuImportancia) activity.getParentViewer().getChildViewer(ViewerIncidSeeUserComuImportancia.class);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        // Verificamos layout.
        ImportanciaUser importanciaUser = new ImportanciaUser(incidImportancia.getUserComu().getUsuario().getAlias(), incidImportancia.getImportancia());
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));

        waitAtMost(4, SECONDS).until((Callable<Adapter>) ((AdapterView<? extends Adapter>) viewer.getViewInViewer())::getAdapter, notNullValue());
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(1));
        checkImportanciaUser(importanciaUser, activity);
    }

    @Test
    public void test_NewViewerIncidSeeUserComuImportancia() throws Exception
    {
        assertThat(viewer.getController(), isA(CtrlerIncidEditAc.class));
    }

    @Test
    public void test_OnSuccessLoadItems() throws Exception
    {

        // Preconditions.
        waitAtMost(4, SECONDS).until((Callable<Adapter>) ((AdapterView<? extends Adapter>) viewer.getViewInViewer())::getAdapter, notNullValue());
        activity.runOnUiThread(() -> viewer.getViewInViewer().setAdapter(null));

        final List<ImportanciaUser> listUsers = Arrays.asList(new ImportanciaUser(USER_PEPE.getAlias(), (short) 3),
                new ImportanciaUser(USER_JUAN.getAlias(), (short) 1));

        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(listUsers);
            isRun.compareAndSet(false, true);
        });
        waitAtMost(4, SECONDS).untilTrue(isRun);
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(2));
        checkImportanciaUser(listUsers.get(0), activity);
        checkImportanciaUser(listUsers.get(1), activity);
    }

    //    ============================  LIFE CYCLE TESTS  ===================================

    @Test
    public void testClearSubscriptions() throws Exception
    {
        checkSubscriptionsOnStop(activity, viewer.getController());
    }
}