package com.didekindroid.usuariocomunidad.register;

import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.usuario.LoginAc;
import com.didekindroid.lib_one.usuario.ViewerRegUserFr;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Response;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadDefault;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.testutil.UiTestUtil.focusOnView;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserNameAlias;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.UserComuMockDao.userComuMockDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/17
 * Time: 11:26
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegComuUserUserComuAcTest {

    private RegComuAndUserAndUserComuAc activity;

    @Rule
    public IntentsTestRule<RegComuAndUserAndUserComuAc> activityRule =
            new IntentsTestRule<RegComuAndUserAndUserComuAc>(RegComuAndUserAndUserComuAc.class, true, true) {
                @Override
                protected void beforeActivityLaunched()
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        create(getTargetContext()).addParentStack(LoginAc.class).startActivities();
                    }
                }
            };

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer != null);
    }

    @After
    public void cleanUp()
    {
        cleanWithTkhandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    //  =========================  TESTS  ===========================

    @Test
    public void test_RegComuUserUserComuBtonListener()
    {
        // Precondition:
        assertThat(requireNonNull(activity.viewer.getController()).isRegisteredUser(), is(false));

        // test_NewViewerRegComuUserUserComuAc
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
        // test_DoViewInViewer
        onView(withId(R.id.reg_user_plus_button)).perform(scrollTo()).check(matches(isDisplayed()));
        // test_OnCreate: Check for initialization of fragments viewers.
        ParentViewerIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegComuFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());

        // Error.
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_user_plus_button;
        focusOnView(activity, buttonId);

        onView(withId(buttonId)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint,
                R.string.alias,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio));

        // OK.
        typeComunidadDefault(new ComunidadAutonoma((short) 10, "Valencia"));
        typeUserNameAlias(USER_PEPE.getUserName(), USER_PEPE.getAlias());
        // Data, exec and check.
        execCheckCleanDialog();
    }

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    /*  =========================  Helpers  ===========================*/

    static void execCheckCleanDialog()
    {
        // Exec.
        onView(withId(R.id.reg_user_plus_button)).perform(scrollTo(), click());
        // Check.
        waitAtMost(8, SECONDS)
                .until(isViewDisplayed(onView(withText(R.string.receive_password_by_mail_dialog)).inRoot(isDialog())));
        // Exec.
        onView(withText(R.string.continuar_button_rot)).inRoot(isDialog()).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(loginAcResourceId));
        intended(hasExtra(user_name.key, USER_PEPE.getUserName()));
        // Clean.
        assertThat(userComuMockDao.deleteUser(USER_PEPE.getUserName()).map(Response::body).blockingGet(), is(true));
    }
}