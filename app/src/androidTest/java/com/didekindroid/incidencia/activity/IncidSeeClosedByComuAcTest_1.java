package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekindroid.R;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.regSeveralUserComuSameUser;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 18:00
 */

/**
 * Tests elementales sobre vista y menús. Lista vacía. Dos comunidades en el spinner.
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeClosedByComuAcTest_1 {

    private IncidSeeClosedByComuAc mActivity;
    private AarActivityTestUtils.CleanUserEnum whatToClean;
    Comunidad mComuPlazuelas5;
    Comunidad mComuReal;
    Comunidad comunidadInIntent;
    IncidSeeByComuListFr mFragment;

    int activityLayoutId = R.id.incid_see_closed_by_comu_ac;
    int fragmentLayoutId = R.id.incid_see_generic_layout;

    @Rule
    public IntentsTestRule<IncidSeeClosedByComuAc> activityRule = new IntentsTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_REAL_PEPE);
                mComuPlazuelas5 = AppUserComuServ.getComusByUser().get(0);
                mComuReal = AppUserComuServ.getComusByUser().get(1);
                comunidadInIntent = mComuReal;
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, comunidadInIntent.getC_Id());
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        whatToClean = CLEAN_PEPE;
        mActivity = activityRule.getActivity();
        mFragment = (IncidSeeByComuListFr) mActivity.getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        // CASO OK: muestra estado inicial actividad.

        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity, notNullValue());
        assertThat(mFragment, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_comunidad_spinner)).check(matches(isDisplayed()));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        // No hay incidencias cerradas.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));

        clickNavigateUp();
    }

    @Test
    public void testOnDataSpinner() throws InterruptedException
    {
        // Caso OK: muestra datos de la comunidad en el intent (2ª comunidad en el spinner)

        Thread.sleep(2000);
        assertThat(mActivity.mComunidadSelected, is(comunidadInIntent));
        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_comunidad_spinner))
        )).check(matches(withText(is(mComuReal.getNombreComunidad()))
        )).check(matches(withText(is(comunidadInIntent.getNombreComunidad()))
        )).check(matches(isDisplayed()));
        // Verificamos el índice de comunidad en el fragmento.
        assertThat(mFragment.mComunidadSelectedIndex, is(1));

        // Caso OK: seleccionamos 1ª comunidad en spinner.

        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onData(allOf(
                is(instanceOf(Comunidad.class)),
                is(mComuPlazuelas5))
        ).perform(click()).check(matches(isDisplayed()));

        // Verificamos que la actividad recibe la comunidad seleccionada.
        assertThat(mActivity.mComunidadSelected, is(mComuPlazuelas5));
        // Se actualiza el índice de comunidad en el fragmento.
        assertThat(mFragment.mComunidadSelectedIndex, is(0));
    }

    @Test
    public void testIncidSeeOpenByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId, fragmentLayoutId);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId, fragmentLayoutId);
    }
}