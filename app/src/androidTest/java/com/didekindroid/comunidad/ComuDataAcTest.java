package com.didekindroid.comunidad;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.util.ComuBundleKey;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.testutil.SavedStateWrapper;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAc;
import com.didekinlib.model.comunidad.Comunidad;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComuCalleNumero;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuDataAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.regComuFrLayout;
import static com.didekindroid.lib_one.testutil.SavedStateWrapper.AFTER_SaveState;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkIsRegistered;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpWithTkGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 01/10/15
 * Time: 09:41
 */
@RunWith(AndroidJUnit4.class)
public class ComuDataAcTest {

    Comunidad comunidad;
    @Rule
    public IntentsTestRule<ComuDataAc> intentRule = new IntentsTestRule<ComuDataAc>(ComuDataAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(SeeUserComuByComuAc.class).startActivities();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidad = signUpWithTkGetComu(COMU_PLAZUELA5_JUAN);
            } catch (UiException | IOException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidad.getC_Id());
            return intent;
        }
    };
    private ComuDataAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        checkIsRegistered(activity.viewer);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

//    =============================================================================================

    @Test
    public void testModifyComuData_UP()
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        // Modificamos.
        typeComuCalleNumero("nombre via One", "123", "Tris");
        onView(withId(R.id.comu_data_ac_button)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test
    public void testModifyComuData_BACK()
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        // Modificamos.
        typeComuCalleNumero("nombre via One", "123", "Tris");
        onView(withId(R.id.comu_data_ac_button)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));

        checkBack(onView(withId(seeUserComuByUserFrRsId)), comuDataAcLayout, regComuFrLayout);
    }

    @Test
    public void test_GetViewerAsParent()
    {
        assertThat(activity.getInjectedParentViewer(), Matchers.<ViewerIf>is(activity.viewer));
    }

    //  =========================  TESTS FOR ACTIVITY LIFECYCLE  ===========================

    @Test
    public void test_OnCreate()
    {
        assertThat(activity, isA(InjectorOfParentViewerIf.class));
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, notNullValue());
        assertThat(activity.regComuFrg, notNullValue());

        assertThat(activity.viewer, isA(ParentViewerIf.class));
        assertThat(activity.regComuFrg.viewerInjector, is(activity));
        assertThat(activity.regComuFrg.viewer.getParentViewer(), is(activity.viewer));
    }

    @Test
    public void test_OnSaveInstanceState()
    {
        final SavedStateWrapper wrapper = new SavedStateWrapper();
        activity.viewer = new ViewerComuDataAc(null, activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                wrapper.saveState();
            }
        };
        activity.viewer.getActivity().runOnUiThread(() -> getInstrumentation().callActivityOnSaveInstanceState(activity, new Bundle(0)));
        waitAtMost(6, SECONDS).until(() -> wrapper.getFlagMethod().equals(AFTER_SaveState));
    }

    @Test
    public void testOnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

//     ==================== MENU ====================

    @SuppressWarnings("RedundantThrows")
    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkItem(activity);
        intended(hasExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidad.getC_Id()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }
}