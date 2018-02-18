package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Menu;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserComuFr;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlags;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_B;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekinlib.http.usuario.UsuarioServConstant.IS_USER_DELETED;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/06/17
 * Time: 12:29
 */
@RunWith(AndroidJUnit4.class)
public class ViewerUserComuDataAcTest {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    UsuarioComunidad userComu;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Comunidad comunidad = null;
            try {
                comunidad = signUpWithTkGetComu(COMU_TRAV_PLAZUELA_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
            userComu = new UsuarioComunidad.UserComuBuilder(comunidad, USER_PEPE).planta("One").roles(PROPIETARIO.function).build();
            Intent intent = new Intent();
            intent.putExtra(USERCOMU_LIST_OBJECT.key, userComu);
            return intent;
        }
    };

    UserComuDataAc activity;
    ViewerUserComuDataAc viewer;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        AtomicReference<ViewerUserComuDataAc> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
        viewer = viewerAtomic.get();

    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    // .............................. VIEWER ..................................

    @Test
    public void test_NewViewerUserComuDataAc() throws Exception
    {
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        assertThat(activity.viewer.userComuIntent, is(userComu));
        checkUserComuData(activity.viewer.userComuIntent);
        onView(withId(R.id.usercomu_data_ac_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.usercomu_data_ac_delete_button)).check(matches(isDisplayed()));
    }

    // .............................. LISTENERS ..................................

    @Test
    public void test_ModifyButtonListener()
    {
        onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()))
                .perform(click()).check(matches(isNotChecked()));
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(click()).check(matches(isChecked()));

        onView(withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        // Verificación.
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));

    }

    @Test
    public void test_DeleteButtonListener()
    {
        // Mocking.
        CtrlerUsuarioComunidad mockController = new CtrlerUsuarioComunidad() {
            @Override
            public boolean deleteUserComu(DisposableSingleObserver<Integer> observer, Comunidad comunidad)
            {
                assertThat(comunidad, is(activity.viewer.userComuIntent.getComunidad()));
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };
        activity.viewer.setController(mockController);
        onView(withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    // .............................. SUBSCRIBERS ..................................

    @Test
    public void test_ModifyComuObserver_1()
    {
        // Exec and check.
        just(1).subscribeWith(viewer.new ModifyUserComuObserver(true));
        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));
        waitAtMost(4, SECONDS).untilTrue(viewer.showComuDataMn);
    }

    @Test
    public void test_ModifyComuObserver_2() throws InterruptedException
    {

        TimeUnit.SECONDS.sleep(4);// Exec and check.
        just(1).subscribeWith(viewer.new ModifyUserComuObserver(false));
        waitAtMost(10, SECONDS).untilFalse(viewer.showComuDataMn);
        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));

    }

    @Test
    public void test_DeleteUserComuObserver_1()
    {
        just(IS_USER_DELETED).subscribeWith(viewer.new DeleteUserComuObserver());
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
        intended(hasFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Test
    public void test_DeleteUserComuObserver_2()
    {
        just(1).subscribeWith(viewer.new DeleteUserComuObserver());
        onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed()));
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnCreate() throws Exception
    {
        // Check for initialization of fragments viewers.
        ParentViewerIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());
    }

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    //  =========================  HELPERS  ===========================

    public void waitForMenu()
    {
        AtomicReference<Menu> atomicItem = new AtomicReference<>(null);
        atomicItem.compareAndSet(null, activity.viewer.acMenu);
        waitAtMost(4, SECONDS).untilAtomic(atomicItem, notNullValue());
    }
}