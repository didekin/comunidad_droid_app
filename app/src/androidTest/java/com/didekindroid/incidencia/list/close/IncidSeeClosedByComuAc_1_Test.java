package com.didekindroid.incidencia.list.close;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkIncidListView;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.testutil.ActivityTestUtils.getAdapterCount;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 11:35
 */

public class IncidSeeClosedByComuAc_1_Test {

    IncidImportancia incidImportancia2;
    IncidImportancia incidImportancia1;

    @Rule
    public IntentsTestRule<IncidSeeClosedByComuAc> activityRule = new IntentsTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
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
        }
    };

    int activityLayoutId = R.id.incid_see_closed_by_comu_ac;
    int fragmentLayoutId = R.id.incid_see_generic_layout;
    private IncidSeeClosedByComuAc activity;
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
    public void testOnCreate_1() throws Exception
    {
        // CASO OK: muestra las incidencias de la comunidad por defecto (Calle La Fuente).

        assertThat(activity, notNullValue());
        assertThat(fragment, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_comunidad_spinner)).check(matches(isDisplayed()));
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnStop(){
        // Preconditions.
        /*waitAtMost(3, SECONDS).until(getAdapterCount(listAdapter), is(1));

        assertThat(controllerList.getSubscriptions().size(), is(1));
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(controllerList.getSubscriptions().size(), is(0));*/
    }

    //    ===================================== HELPERS =====================================

}
