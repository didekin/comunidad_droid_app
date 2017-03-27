package com.didekindroid.incidencia.list.close;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 18:00
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeClosedByComuAc_Mn_Test {

    Comunidad comunidadInIntent;
    CtrlerIncidSeeCloseByComu controllerList;

    @Rule
    public ActivityTestRule<IncidSeeClosedByComuAc> activityRule = new ActivityTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class,true, true) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidadInIntent = signUpWithTkGetComu(COMU_REAL_DROID);
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id());
            return intent;
        }
    };

    IncidSeeCloseByComuFr fragment;
    int activityLayoutId = R.id.incid_see_closed_by_comu_ac;
    int fragmentLayoutId = R.id.incid_see_generic_layout;
    private IncidSeeClosedByComuAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeCloseByComuFr) activity.getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
//        controllerList = (CtrlerIncidSeeCloseByComu) fragment.controllerSeeIncids;  TODO: no necesario el controller.
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        // CASO OK: muestra estado inicial actividad.

        assertThat(controllerList.isRegisteredUser(), is(true));
        assertThat(activity, notNullValue());
        assertThat(fragment, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_comunidad_spinner)).check(matches(isDisplayed()));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        // No hay incidencias cerradas.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));

        clickNavigateUp();
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Test
    public void testIncidSeeOpenByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(activity);
        checkUp(activityLayoutId, fragmentLayoutId);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(activity);
        checkUp(activityLayoutId, fragmentLayoutId);
    }
}