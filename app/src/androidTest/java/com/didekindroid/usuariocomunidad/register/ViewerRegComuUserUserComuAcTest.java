package com.didekindroid.usuariocomunidad.register;

import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.usuario.ViewerRegUserFr;
import com.didekindroid.usuario.LoginAc;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

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
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadDefault;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeUserNameAlias;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtil.focusOnView;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
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

    RegComuAndUserAndUserComuAc activity;

    static void execCheckCleanDialog(ViewerIf viewer) throws IOException
    {
        typeUserNameAlias(USER_PEPE.getUserName(), USER_PEPE.getAlias());
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        // Exec.
        onView(withId(R.id.reg_user_plus_button)).perform(scrollTo(), click());
        // Check.
        waitAtMost(8, SECONDS)
                .until(isViewDisplayed(onView(withText(R.string.receive_password_by_mail_dialog)).inRoot(isDialog())));
        assertThat(viewer.getController().isRegisteredUser(), is(true));
        // Exec.
        onView(withText(R.string.continuar_button_rot)).inRoot(isDialog()).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(loginAcResourceId));
        intended(hasExtra(user_name.key, USER_PEPE.getUserName()));
        // Clean.
        assertThat(usuarioMockDao.deleteUser(USER_PEPE.getUserName()).execute().body(), is(true));
    }

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        AtomicReference<ViewerRegComuUserUserComuAc> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
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
    public void test_NewViewerRegComuUserUserComuAc() throws Exception
    {
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        onView(withId(R.id.reg_user_plus_button)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void test_OnRegisterSuccess() throws Exception
    {
        /* Precondition: the user is registered and the cache is NOT initialized.*/
        activity.viewer.getController().updateIsRegistered(true);
        // Exec.
        activity.viewer.onRegisterSuccess(COMU_ESCORIAL_PEPE);
        // Check.
        checkTextsInDialog(R.string.receive_password_by_mail_dialog, R.string.continuar_button_rot);
    }

    @Test
    public void test_RegComuUserUserComuBtonListener_1() throws Exception
    {
        // Precondition:
        assertThat(activity.viewer.getController().isRegisteredUser(), is(false));
        // Data.
        typeComunidadDefault(new ComunidadAutonoma((short) 10, "Valencia"));
        // Data, exec and check.
        execCheckCleanDialog(activity.viewer);
    }

    @Test
    public void test_RegComuUserUserComuBtonListener_2() throws Exception
    {
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_user_plus_button;
        focusOnView(activity, buttonId);
        typeComunidadData();

        onView(withId(buttonId)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint,
                R.string.alias));
    }

    @Test
    public void test_RegComuUserUserComuBtonListener_3() throws Exception
    {
        typeUserNameAlias(USER_PEPE.getUserName(), USER_PEPE.getAlias());
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_user_plus_button;
        focusOnView(activity, buttonId);
        onView(withId(buttonId)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio));
    }

    /*  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================*/

    @Test
    public void test_OnCreate()
    {
        // Check for initialization of fragments viewers.
        ParentViewerIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegComuFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());
    }

    //  =========================  Helpers  ===========================

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }
}