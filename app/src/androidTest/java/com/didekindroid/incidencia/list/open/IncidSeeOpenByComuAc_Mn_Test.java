package com.didekindroid.incidencia.list.open;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
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
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_CLOSED_BY_COMU_AC;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isViewWithTextOk;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_COMU_AC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 18:45
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenByComuAc_Mn_Test {

    int activityLayoutId = R.id.incid_see_open_by_comu_ac;
    int secondLayoutId = R.id.incid_see_generic_layout;
    Comunidad comunidadInIntent;
    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {

        /**
         * Preconditions:
         * 1. A comunidadId is passed as an intent extra.
         * 2. The user is registered.
         */
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
    private IncidSeeOpenByComuAc activity;

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

    @Test
    public void testOnCreate_1() throws Exception
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(secondLayoutId)).check(matches(isDisplayed()));
        // No hay incidencias registradas.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

        waitAtMost(3, SECONDS).until(
                isViewWithTextOk(comunidadInIntent.getNombreComunidad())
        );

        clickNavigateUp();
    }

//  ================================ MENU ====================================

    @Test
    public void testIncidSeeClosedByComuMn() throws InterruptedException
    {
        INCID_SEE_CLOSED_BY_COMU_AC.checkMenuItem_WTk(activity);
        checkUp(activityLayoutId, secondLayoutId);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(activity);
        checkUp(activityLayoutId, secondLayoutId);
    }

    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id()));
        checkUp(activityLayoutId, secondLayoutId);
    }
}