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
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeCloseAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 18:00
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeClosedByComuAc_Mn_Test {

    Comunidad comunidadInIntent;

    @Rule
    public ActivityTestRule<IncidSeeClosedByComuAc> activityRule = new ActivityTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class, true, true) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidadInIntent = signUpWithTkGetComu(COMU_REAL_DROID);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id());
            return intent;
        }
    };

    private IncidSeeClosedByComuAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_DROID);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Test
    public void testOnCreateEmptyList() throws Exception
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_comunidad_spinner)).check(matches(isDisplayed()));

        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(android.R.id.empty)));
        // No hay incidencias cerradas.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testIncidSeeOpenByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(activity);
        checkUp(incidSeeCloseAcLayout, incidSeeGenericFrLayout);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(activity);
        checkUp(incidSeeCloseAcLayout, incidSeeGenericFrLayout);
    }
}