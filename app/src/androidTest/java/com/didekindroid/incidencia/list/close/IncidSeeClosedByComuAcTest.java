package com.didekindroid.incidencia.list.close;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeCloseAcResourceId;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrResourceId;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.nextResolucionFrResourceId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkIncidClosedListView;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.doComunidadSpinner;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptions;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.isComuSpinnerWithText;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 11:35
 */

public class IncidSeeClosedByComuAcTest {

    IncidImportancia incidImportancia2;
    IncidImportancia incidImportancia1;

    @Rule
    public IntentsTestRule<IncidSeeClosedByComuAc> activityRule = new IntentsTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
                incidImportancia1 = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0), (short) 1);
                incidImportancia2 = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(1), (short) 4);

                // Cierre incidencias..
                incidenciaDao.closeIncidencia(insertGetResolucionNoAdvances(incidImportancia1));
                incidenciaDao.closeIncidencia(insertGetResolucionNoAdvances(incidImportancia2));

                // Incidencias con fecha de cierre.
                incidImportancia1 = new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder()
                                .copyIncidencia(incidImportancia1.getIncidencia())
                                .fechaCierre(
                                        incidenciaDao.seeIncidsClosedByComu(incidImportancia1.getIncidencia().getComunidadId()).get(0).getIncidencia().getFechaCierre()
                                )
                                .build())
                        .copyIncidImportancia(incidImportancia1)
                        .build();

                incidImportancia2 = new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder()
                                .copyIncidencia(incidImportancia2.getIncidencia())
                                .fechaCierre(
                                        incidenciaDao.seeIncidsClosedByComu(incidImportancia2.getIncidencia().getComunidadId()).get(0).getIncidencia().getFechaCierre()
                                )
                                .build())
                        .copyIncidImportancia(incidImportancia2)
                        .build();

            } catch (UiException | IOException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, incidImportancia1.getIncidencia().getComunidadId());
            return intent;
        }
    };

    IncidSeeClosedByComuAc activity;
    private IncidSeeCloseByComuFr fragment;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeCloseByComuFr) activity.getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }


    //  ==================================== INTEGRATION TESTS  ====================================

    @Test
    public void testOnCreate() throws Exception
    {
        /* CASO OK: muestra las incidencias de la comunidad por defecto (Calle La Fuente).*/
        assertThat(activity, notNullValue());
        assertThat(fragment, notNullValue());
        // Inicializa correctamente fragment.
        assertThat(fragment.getArguments().getLong(COMUNIDAD_ID.key), is(incidImportancia1.getIncidencia().getComunidadId()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(incidSeeCloseAcResourceId)).check(matches(isDisplayed()));
        onView(withId(incidSeeGenericFrResourceId)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_comunidad_spinner)).check(matches(isDisplayed()));

        // Data
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        waitAtMost(2, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));

        // Cambiamos la comunidad en el spinner y revisamos los datos.
        doComunidadSpinner(incidImportancia2.getIncidencia().getComunidad());
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia2, activity)));
    }

    @Test
    public void testOnSelectedWithUp() throws UiException
    {
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        // Seleccionamos incidencia.
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(nextResolucionFrResourceId)));
        // Up and check.
        checkUp();
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
    }

    @Test
    public void testOnSelectedWithBack() throws UiException
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        // Seleccionamos incidencia.
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(nextResolucionFrResourceId)));
        // Back and check.
        checkBack(onView(withId(nextResolucionFrResourceId)));
        waitAtMost(4, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnStop()
    {
        checkSubscriptions(fragment.viewer.getController(), activity);
    }

    //    ===================================== HELPERS =====================================

}
