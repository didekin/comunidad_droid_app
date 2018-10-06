package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserComuFr;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
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

/**
 * User: pedro@didekin
 * Date: 07/06/17
 * Time: 12:29
 */
@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class ViewerUserComuDataAcTest {

    private static UsuarioComunidad userComu;
    private UserComuDataAc activity;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(USERCOMU_LIST_OBJECT.key, userComu);
        }
    };

    @BeforeClass
    public static void setStatic() throws Exception
    {
        userComu = new UsuarioComunidad.UserComuBuilder(signUpGetComu(COMU_TRAV_PLAZUELA_PEPE), USER_PEPE)
                .planta("One")
                .roles(PROPIETARIO.function)
                .build();
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer, notNullValue());
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_PEPE);
    }

    // .............................. VIEWER ..................................

    @Test
    public void test_DoViewInViewer()
    {
        // test_NewViewerUserComuDataAc
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));

        // test_OnCreate: Check for initialization of fragments viewers.
        ParentViewerIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());

        assertThat(activity.viewer.userComuIntent, is(userComu));
        checkUserComuData(activity.viewer.userComuIntent);
        onView(withId(R.id.usercomu_data_ac_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.usercomu_data_ac_delete_button)).check(matches(isDisplayed()));
        waitAtMost(8, SECONDS).until(() -> activity.viewer.showMnOldestAdmonUser.get());

        // test_OnStop
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
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
        waitAtMost(8, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }

    @Test
    public void test_actionAfterDeleteUser() throws Exception
    {
        activity.viewer.actionAfterDeleteUser.accept(1);
        onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed()));

        activity.viewer.actionAfterDeleteUser.accept(IS_USER_DELETED);
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
    }

    // .............................. SUBSCRIBERS ..................................

    @Test
    public void test_ModifyComuObserver()
    {
        // Wait for the initializaton by the activity: to 'true' (only one user).
        waitAtMost(6, SECONDS).untilTrue(activity.viewer.showMnOldestAdmonUser);

        just(1).subscribeWith(activity.viewer.new ModifyUserComuObserver(false));
        waitAtMost(6, SECONDS).untilFalse(activity.viewer.showMnOldestAdmonUser);
        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));
    }

    @Test
    public void test_OldestObserver()
    {
        // Wait for the initializaton by the activity: to 'true' (only one user).
        waitAtMost(6, SECONDS).untilTrue(activity.viewer.showMnOldestAdmonUser);

        just(false).subscribeWith(activity.viewer.new OldestObserver());
        waitAtMost(6, SECONDS).untilFalse(activity.viewer.showMnOldestAdmonUser);

        just(true).subscribeWith(activity.viewer.new OldestObserver());
        waitAtMost(6, SECONDS).untilTrue(activity.viewer.showMnOldestAdmonUser);
    }
}