package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserComuFr;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekinlib.model.usuario.http.UsuarioServConstant.IS_USER_DELETED;
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
@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class ViewerUserComuDataAcTest {

    private UsuarioComunidad userComu;
    private UserComuDataAc activity;
    private boolean hasToClean = true;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                userComu = new UsuarioComunidad.UserComuBuilder(signUpGetComu(COMU_TRAV_PLAZUELA_PEPE), USER_PEPE)
                        .planta("One")
                        .roles(PROPIETARIO.function)
                        .build();
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(USERCOMU_LIST_OBJECT.key, userComu);
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer, notNullValue());
    }

    @After
    public void tearDown() throws Exception
    {
        if (hasToClean){
            cleanOptions(CLEAN_PEPE);
        }
    }

    // .............................. VIEWER ..................................

    @Test
    public void test_NewViewerUserComuDataAc()
    {
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
    }

    @Test
    public void test_DoViewInViewer()
    {
        assertThat(activity.viewer.userComuIntent, is(userComu));
        checkUserComuData(activity.viewer.userComuIntent);
        onView(withId(R.id.usercomu_data_ac_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.usercomu_data_ac_delete_button)).check(matches(isDisplayed()));
        waitAtMost(8, SECONDS).until(() -> activity.viewer.showMnOldestAdmonUser.get());
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
        hasToClean = false;
        // Before.
        assertThat(activity.viewer.getController().getTkCacher().isUserRegistered(), is(true));
        // Exec.
        onView(withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        // Check.
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(comuSearchAcLayout));
        intended(hasFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Test
    public void test_actionAfterDeleteUser_1() throws Exception
    {
        activity.viewer.actionAfterDeleteUser.accept(1);
        onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void test_actionAfterDeleteUser_2() throws Exception
    {
        activity.viewer.actionAfterDeleteUser.accept(IS_USER_DELETED);
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
    }

    // .............................. SUBSCRIBERS ..................................

    @Test
    public void test_ModifyComuObserver_1()
    {
        // Exec and check.
        just(1).subscribeWith(activity.viewer.new ModifyUserComuObserver(true));
        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));
        waitAtMost(4, SECONDS).untilTrue(activity.viewer.showMnOldestAdmonUser);
    }

    @Test
    public void test_ModifyComuObserver_2()
    {
        just(1).subscribeWith(activity.viewer.new ModifyUserComuObserver(false));
        waitAtMost(10, SECONDS).untilFalse(activity.viewer.showMnOldestAdmonUser);
        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));

    }

    @Test
    public void test_OldestObserver()
    {
        just(true).subscribeWith(activity.viewer.new OldestObserver());
        waitAtMost(6, SECONDS).until(() -> activity.viewer.showMnOldestAdmonUser.get());

        just(false).subscribeWith(activity.viewer.new OldestObserver());
        waitAtMost(6, SECONDS).until(() -> !activity.viewer.showMnOldestAdmonUser.get());
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnCreate()
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
}