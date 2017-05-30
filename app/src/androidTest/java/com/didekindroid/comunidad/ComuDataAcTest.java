package com.didekindroid.comunidad;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.comunidad.utils.ComuBundleKey;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComuCalleNumero;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuDataAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.nextComuDataAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.regComuFrLayout;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_COMU_AC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
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

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    Comunidad comunidad;

    @Rule
    public IntentsTestRule<ComuDataAc> intentRule = new IntentsTestRule<ComuDataAc>(ComuDataAc.class) {
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

    ComuDataAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

//    =============================================================================================

    @Test
    public void testModifyComuData_UP() throws UiException, InterruptedException
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        // Modificamos.
        typeComuCalleNumero("nombre via One", "123", "Tris");
        onView(withId(R.id.comu_data_ac_button)).perform(scrollTo(), click());
        onView(withId(nextComuDataAcLayout)).check(matches(isDisplayed()));

        checkUp(comuDataAcLayout);
    }

    @Test
    public void testModifyComuData_BACK() throws UiException
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        // Modificamos.
        typeComuCalleNumero("nombre via One", "123", "Tris");
        onView(withId(R.id.comu_data_ac_button)).perform(scrollTo(), click());
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(nextComuDataAcLayout));
        checkBack(onView(withId(nextComuDataAcLayout)), comuDataAcLayout, regComuFrLayout);
    }

    @Test
    public void test_GetViewerAsParent() throws Exception
    {
        assertThat(activity.getViewerAsParent(), Matchers.<ViewerIf>is(activity.viewer));
    }

    //  =========================  TESTS FOR ACTIVITY LIFECYCLE  ===========================

    @Test
    public void test_OnCreate() throws Exception
    {
        assertThat(activity, isA(ViewerParentInjectorIf.class));
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, notNullValue());
        assertThat(activity.regComuFrg, notNullValue());

        assertThat(activity.viewer, isA(ViewerParentInjectedIf.class));
        assertThat(activity.regComuFrg.viewerInjector, CoreMatchers.<ViewerParentInjectorIf>is(activity));
        assertThat(activity.regComuFrg.viewer.getParentViewer(), CoreMatchers.<ViewerIf>is(activity.viewer));
    }

    @Test
    public void test_OnSaveInstanceState()
    {
        activity.viewer = new ViewerComuDataAc(activity.acView, activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), Matchers.is(BEFORE_METHOD_EXEC));
            }

            @Override
            public int clearSubscriptions()  // It is called from onStop() and gives problems.
            {
                return 0;
            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getInstrumentation().callActivityOnSaveInstanceState(activity, new Bundle(0));
            }
        });
        waitAtMost(6, SECONDS).untilAtomic(flagMethodExec, Matchers.is(AFTER_METHOD_EXEC_B));
    }

    @Test
    public void testOnStop() throws Exception
    {
        checkSubscriptionsOnStop(activity.viewer.getController(), activity);
    }

//     ==================== MENU ====================

    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidad.getC_Id()));
        checkUp(comuDataAcLayout);
    }
}