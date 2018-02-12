package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ViewerComuDataAc.newViewerComuDataAc;
import static com.didekindroid.comunidad.ViewerRegComuFr.newViewerRegComuFr;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.doTipoViaSpinner;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuDataAcLayout;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 14/05/17
 * Time: 16:25
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuDataAcTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    ViewerComuDataAc viewer;
    Comunidad comunidad;

    @Rule
    public ActivityTestRule<ComuDataAc> activityRule = new ActivityTestRule<ComuDataAc>(ComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                comunidad = signUpWithTkGetComu(COMU_PLAZUELA5_JUAN);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, comunidad.getC_Id());
            return intent;
        }
    };

    ComuDataAc activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        AtomicReference<ViewerComuDataAc> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
        viewer = viewerAtomic.get();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    ==================================== TESTS =========================================

    @Test
    public void test_NewViewerComuDataAc() throws Exception
    {
        assertThat(viewer.getController(), isA(CtrlerComunidad.class));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        onView(withId(comuDataAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed()));
    }

    @Test
    public void test_SetChildViewer() throws Exception
    {
        viewer = newViewerComuDataAc(activity);
        assertThat(viewer.getChildViewer(ViewerRegComuFr.class), nullValue());
        // After.
        viewer.setChildViewer(newViewerRegComuFr(activity.regComuFrg.getView(), viewer));
        assertThat(viewer.getChildViewer(ViewerRegComuFr.class), notNullValue());
    }

    @Test
    public void testComuDataAcButtonListener()
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre()); // Esperamos por los viejos datos.
        viewer.setController(new CtrlerComunidad() {
            @Override
            boolean modifyComunidadData(DisposableSingleObserver<Integer> observer, Comunidad comunidad)
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