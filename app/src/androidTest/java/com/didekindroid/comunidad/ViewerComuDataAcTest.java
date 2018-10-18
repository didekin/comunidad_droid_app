package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doTipoViaSpinner;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuDataAcLayout;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/05/17
 * Time: 16:25
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuDataAcTest {

    private ViewerComuDataAc viewer;
    private ComuDataAc activity;
    private static Comunidad comunidad;

    @Rule
    public ActivityTestRule<ComuDataAc> activityRule = new ActivityTestRule<ComuDataAc>(ComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(COMUNIDAD_ID.key, comunidad.getC_Id());
        }
    };

    @BeforeClass
    public static void setStatic() throws Exception
    {
        comunidad = signUpGetComu(COMU_PLAZUELA5_JUAN);
    }

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        waitAtMost(6, SECONDS).until(() -> activity.viewer != null);
        viewer = activity.viewer;
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    ==================================== TESTS =========================================

    @Test
    public void testComuDataAcButtonListener()
    {
        // test_DoViewInViewer
        assertThat(viewer.getController(), isA(CtrlerComunidad.class));
        onView(withId(comuDataAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed()));
        // test_SetChildViewer
        assertThat(viewer.getChildViewer(ViewerRegComuFr.class), notNullValue());

        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.

        AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
        viewer.setController(new CtrlerComunidad() {
            @Override
            public boolean modifyComunidadData(DisposableSingleObserver<Integer> observer, Comunidad comunidad)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        // No cambiamos nada y hacemos click.
        onView(withId(R.id.comu_data_ac_button)).perform(scrollTo(), click());
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));

        // Con errores:
        doTipoViaSpinner(new TipoViaValueObj(0, activity.getText(R.string.tipo_via_spinner).toString())); // Valor por defecto: no selección.
        onView(withId(R.id.comu_data_ac_button)).perform(scrollTo(), click());
        waitAtMost(6, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.tipo_via));
        assertThat(flagMethodExec.get(), is(BEFORE_METHOD_EXEC));
    }

    @Test
    public void test_ComuDataAcObserver()
    {
        ViewerComuDataAc.ComuDataAcObserver observer = viewer.new ComuDataAcObserver();
        just(1).subscribeWith(observer);
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(seeUserComuByUserFrRsId)));
    }
}