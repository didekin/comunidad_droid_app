package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.usuario.LoginAc;
import com.didekindroid.lib_one.usuario.ViewerRegUserFr;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.testutil.UiTestUtil.focusOnView;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserNameAlias;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.register.ViewerRegComuUserUserComuAcTest.execCheckCleanDialog;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 05/06/17
 * Time: 10:37
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegUserAndUserComuAcTest {

    private Comunidad comunidad;
    private RegUserAndUserComuAc activity;

    @Rule
    public IntentsTestRule<RegUserAndUserComuAc> intentRule = new IntentsTestRule<RegUserAndUserComuAc>(RegUserAndUserComuAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(LoginAc.class).startActivities();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                cleanOptions(CLEAN_TK_HANDLER);
                comunidad = signUpGetComu(COMU_PLAZUELA5_JUAN);
                cleanWithTkhandler();
                getInstance().deleteInstanceId();
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer != null);
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_RegUserAndUserComuButtonListener_1()
    {
        // Precondition:
        assertThat(requireNonNull(activity.viewer.getController()).isRegisteredUser(), is(false));

        // test_NewViewerRegUserAndUserComuAc
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
        // test_DoViewInViewer.
        onView(allOf(
                withId(R.id.descripcion_comunidad_text),
                withText(comunidad.getNombreComunidad())
        )).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_plus_button)).perform(scrollTo()).check(matches(isDisplayed()));
        // test_OnCreate: Check for initialization of fragments viewers.
        ParentViewerIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegUserFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());

        /* Error.*/
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE);
        int buttonId = R.id.reg_user_plus_button;
        focusOnView(activity, buttonId);
        onView(withId(buttonId)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint,
                R.string.alias));

        /* OK.*/
        typeUserNameAlias(USER_PEPE.getUserName(), USER_PEPE.getAlias());
        execCheckCleanDialog();
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }
}