package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.exception.UiException;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlags;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekinlib.http.UsuarioServConstant.IS_USER_DELETED;
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

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        AtomicReference<ViewerUserComuDataAc> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

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

    @Test
    public void test_UpdateActivityMenu() throws Exception
    {
        // Precondition.
        waitForMenu();
        // Usuario inquilino, pero es el usuario más antiguo.
        MenuItem comuDataItem = activity.viewer.acMenu.findItem(R.id.comu_data_ac_mn);
        assertThat(comuDataItem.isEnabled(), is(true));
        assertThat(comuDataItem.isVisible(), is(true));
        // Check in the overflow menu.
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withText(activity.getString(R.string.comu_data_ac_mn))).check(matches(isDisplayed()));
    }

    @Test
    public void test_SetAcMenu() throws Exception
    {
        // Mock test.
        Menu mockMenu = activity.viewer.acMenu;
        activity.viewer.acMenu = null;
        CtrlerUsuarioComunidad mockController = new CtrlerUsuarioComunidad() {
            @Override
            public boolean checkIsOldestAdmonUser(DisposableSingleObserver<Boolean> observer, Comunidad comunidad)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };
        activity.viewer.setController(mockController);
        activity.viewer.setAcMenu(mockMenu);
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        assertThat(activity.viewer.acMenu, is(mockMenu));
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
        onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed()));
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
    public void test_AcMenuObserver_1()
    {
        // Precondition.
        waitForMenu();
        // Turn to false the initial values.
        final MenuItem comuDataItem = activity.viewer.acMenu.findItem(R.id.comu_data_ac_mn);
        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                comuDataItem.setVisible(false);
                comuDataItem.setEnabled(false);
                activity.onPrepareOptionsMenu(activity.viewer.acMenu);
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isRun);
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());

        ViewerUserComuDataAc.AcMenuObserver observer = activity.viewer.new AcMenuObserver();
        just(false).subscribeWith(observer);
        MenuItem newComuDataItem = activity.viewer.acMenu.findItem(R.id.comu_data_ac_mn);
        assertThat(comuDataItem.isEnabled(), is(false));
        assertThat(comuDataItem.isVisible(), is(false));
    }

    @Test
    public void test_AcMenuObserver_2()
    {
        // Precondition.
        waitForMenu();
        // Turn to false the initial values.
        final MenuItem comuDataItem = activity.viewer.acMenu.findItem(R.id.comu_data_ac_mn);
        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                comuDataItem.setVisible(false);
                comuDataItem.setEnabled(false);
                activity.onPrepareOptionsMenu(activity.viewer.acMenu);
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isRun);
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withText(R.string.comu_data_ac_mn)).check(doesNotExist());

        final ViewerUserComuDataAc.AcMenuObserver observer = activity.viewer.new AcMenuObserver();
        isRun.compareAndSet(true, false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                just(true).subscribeWith(observer);
                MenuItem newComuDataItem = activity.viewer.acMenu.findItem(R.id.comu_data_ac_mn);
                assertThat(comuDataItem.isEnabled(), is(true));
                assertThat(comuDataItem.isVisible(), is(true));
                activity.onPrepareOptionsMenu(activity.viewer.acMenu);
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isRun);
        // No es necesario abrir el menú: sigue abierto.
        onView(withText(R.string.comu_data_ac_mn)).check(matches(isDisplayed()));
    }

    @Test
    public void test_ModifyUserComuObserver()
    {
        ViewerUserComuDataAc.ModifyUserComuObserver observer = activity.viewer.new ModifyUserComuObserver();
        just(1).subscribeWith(observer);
        onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void test_DeleteUserComuObserver_1()
    {
        ViewerUserComuDataAc.DeleteUserComuObserver observer = activity.viewer.new DeleteUserComuObserver();
        just(IS_USER_DELETED).subscribeWith(observer);
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
        intended(hasFlags(FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_NEW_TASK));
    }

    @Test
    public void test_DeleteUserComuObserver_2()
    {
        ViewerUserComuDataAc.DeleteUserComuObserver observer = activity.viewer.new DeleteUserComuObserver();
        just(1).subscribeWith(observer);
        onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed()));
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnCreate() throws Exception
    {
        // Check for initialization of fragments viewers.
        ViewerParentInjectedIf viewerParent = activity.viewer;
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