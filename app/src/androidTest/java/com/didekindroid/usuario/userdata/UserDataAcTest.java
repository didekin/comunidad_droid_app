package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.UsuarioBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.reg_usuario_alias_ediT;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.user_data_ac_password_ediT;
import static com.didekindroid.R.id.user_data_modif_button;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.exception.UiExceptionRouter.GENERIC_APP_ACC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.isViewOnView;
import static com.didekindroid.testutil.ActivityTestUtils.testProcessCtrlError;
import static com.didekindroid.testutil.ActivityTestUtils.testProcessCtrlErrorOnlyToast;
import static com.didekindroid.testutil.ActivityTestUtils.testReplaceViewStd;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.DELETE_ME_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 16/07/15
 * Time: 14:25
 */
public class UserDataAcTest implements ExtendableTestAc {

    static AtomicInteger flagForExecution = new AtomicInteger(0);
    UserDataAc activity;
    Usuario registeredUser;

    @Rule
    public IntentsTestRule<? extends Activity> mActivityRule = new IntentsTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                registeredUser = signUpAndUpdateTk(COMU_REAL_JUAN);
                assertThat(registeredUser, notNullValue());
            } catch (Exception e) {
                fail();
            }
        }
    };

    ControllerUserDataIf controller;
    int activityLayoutId = R.id.user_data_ac_layout;

    @BeforeClass
    public static void relax() throws InterruptedException
    {
        TimeUnit.MILLISECONDS.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = (UserDataAc) mActivityRule.getActivity();
        // Default initialization.
        controller = new ControllerUserData(activity);
    }

    @After
    public void tearDown() throws Exception
    {
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    @Override
    public void checkNavigateUp()
    {
        checkUp(activityLayoutId);
    }

    @Override
    public int getNextViewResourceId()
    {
        return R.id.see_usercomu_by_user_frg;
    }

    // ============================================================
    //    ................ INTEGRATION TESTS ..............
    // ============================================================

    @Test
    public void testOncreate()
    {
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));

        onView(withId(reg_usuario_email_editT))
                .check(matches(withText(containsString(registeredUser.getUserName()))));
        onView(withId(reg_usuario_alias_ediT))
                .check(matches(withText(containsString(registeredUser.getAlias()))));

        onView(allOf(withId(user_data_ac_password_ediT),
                withHint(R.string.user_data_ac_password_hint)))
                .check(matches(withText(containsString(""))));

        onView(withId(user_data_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test  // Integration test: wrong password.
    public void testModifyUserData_A() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        typeUserData("new_juan@juan.es", USER_JUAN.getAlias(), "wrong_password");
        onView(withId(user_data_modif_button)).perform(scrollTo()).check(matches(isDisplayed())).perform(click());
        waitAtMost(1, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }

    @Test  // Integration test: modify user OK.
    public void testModifyUserData_B() throws UiException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));

        typeUserData("new@username.com", "new_alias", USER_JUAN.getPassword());
        onView(withId(user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        waitAtMost(2, SECONDS).until(isViewOnView(getNextViewResourceId()));
        // Verificamos navegación.
        checkNavigateUp();
    }

    // ============================================================
    //    ................. VIEWER TESTS ..................
    // ============================================================

    @Test
    public void testProcessControllerError_1() throws InterruptedException
    {
        testProcessCtrlErrorOnlyToast(activity, BAD_REQUEST, R.string.password_wrong, activityLayoutId);
    }

    @Test
    public void testProcessControllerError_2()
    {
        testProcessCtrlError(activity, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC);
    }

    @Test
    public void testClearControllerSubscriptions()
    {
        controller = new ControllerUserDataForTest(activity);
        activity.clearControllerSubscriptions();
        assertThat(flagForExecution.getAndSet(0), is(29));
    }

    @Test
    public void testReplaceView()
    {
        testReplaceViewStd(activity, getNextViewResourceId());
    }

    // ============================================================
    //    ............. VIEWER USER DATA TESTS ...............
    // ============================================================

    @Test
    public void testInitUserDataInView()
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        final AtomicBoolean isRun = new AtomicBoolean(false);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.initUserDataInView();
                assertThat(isRun.getAndSet(true), is(false));
            }
        });

        waitAtMost(1, SECONDS).untilTrue(isRun);
        checkInitialData(USER_JUAN.getUserName(), USER_JUAN.getAlias());
    }

    @Test
    public void testGetDataChangedFromView()
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        typeUserData("newuser@user.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());

        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getDataChangedFromView();
                assertThat(isRun.getAndSet(true), is(false));
            }
        });
        waitAtMost(1, SECONDS).untilTrue(isRun);
        assertThat(activity.getDataChangedFromView()[0], is("newuser@user.com"));
        assertThat(activity.getDataChangedFromView()[1], is(USER_JUAN.getAlias()));
        assertThat(activity.getDataChangedFromView()[2], is(USER_JUAN.getPassword()));
    }

    @Test
    public void testCheckUserData_1()
    {
        typeUserData("newuser@user.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());

        activity.usuarioBean = null;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(activity.checkUserData(), is(true));
            }
        });
        waitAtMost(1, SECONDS).until(fieldIn(activity.usuarioBean).ofType(String.class).andWithName("userName"), is("newuser@user.com"));
        assertThat(activity.usuarioBean.getUserName(), is("newuser@user.com"));
        assertThat(activity.usuarioBean.getAlias(), is(USER_JUAN.getAlias()));
        assertThat(activity.usuarioBean.getPassword(), is(USER_JUAN.getPassword()));
    }

    @Test
    public void testCheckUserData_2()
    {
        typeUserData("wrong_newuser.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());

        activity.usuarioBean = null;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(activity.checkUserData(), is(false));
            }
        });

        waitAtMost(1, SECONDS).until(fieldIn(activity).ofType(UsuarioBean.class), notNullValue());
        checkToastInTest(R.string.email_hint, activity);
    }

    @Test
    public void testWhatDataChangeToMake()
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        onView(withId(user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(activity.checkUserData(), is(true));
                assertThat(activity.whatDataChangeToMake(), is(nothing));
                assertThat(isRun.getAndSet(true), is(false));
            }
        });
        waitAtMost(1, SECONDS).untilTrue(isRun);

        typeUserData(USER_JUAN.getUserName(), "new_alias", USER_JUAN.getPassword());
        isRun.set(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(activity.checkUserData(), is(true));
                assertThat(activity.whatDataChangeToMake(), is(alias_only));
                assertThat(isRun.getAndSet(true), is(false));
            }
        });
        waitAtMost(1, SECONDS).untilTrue(isRun);

        typeUserData("new@username.com", "new_alias", USER_JUAN.getPassword());
        isRun.set(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(activity.checkUserData(), is(true));
                assertThat(activity.whatDataChangeToMake(), is(userName));
                assertThat(isRun.getAndSet(true), is(false));
            }
        });
        waitAtMost(1, SECONDS).untilTrue(isRun);
    }

    @Test
    public void testModifyUserData() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        // No modificamos ningún dato.
        onView(withId(user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(activity.checkUserData(), is(true));
                activity.modifyUserData(activity.whatDataChangeToMake());
            }
        });
        await().atMost(2, SECONDS).until(isToastInView(R.string.no_user_data_to_be_modified, activity));
    }

    @Test
    public void processBackUsuarioInView()
    {
        // Preconditions.
        assertThat(activity.oldUser, not(is(USER_DROID)));
        // Execute.
        activity.processBackUsuarioInView(USER_DROID);
        // Check.
        assertThat(activity.oldUser, is(USER_DROID));
        checkInitialData(USER_DROID.getUserName(), USER_DROID.getAlias());
        assertThat(activity.intentForMenu.getStringExtra(user_name.key), is(activity.oldUser.getUserName()));
    }

    //    =================================  MENU TESTS ==================================

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        COMU_SEARCH_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, activity.oldUser.getUserName()));
        // NO navigate-up.
    }

    @Test
    public void testDeleteMeMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        DELETE_ME_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, activity.oldUser.getUserName()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testPasswordChangeMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        PASSWORD_CHANGE_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, activity.oldUser.getUserName()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, activity.oldUser.getUserName()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testIncidSeeByComuMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, activity.oldUser.getUserName()));
        checkUp(activityLayoutId);
    }

    /*    =================================  HELPERS ==================================*/

    private void typeUserData(String userName, String alias, String password)
    {
        onView(withId(reg_usuario_email_editT)).perform(replaceText(userName), closeSoftKeyboard());
        onView(withId(reg_usuario_alias_ediT)).perform(replaceText(alias));
        onView(withId(user_data_ac_password_ediT)).perform(replaceText(password), closeSoftKeyboard());
    }

    private void checkInitialData(String userName, String alias)
    {
        assertThat(((EditText) activity.acView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(), is(userName));
        assertThat(((EditText) activity.acView.findViewById(R.id.reg_usuario_alias_ediT)).getText().toString(), is(alias));
    }

    class ControllerUserDataForTest extends ControllerUserData{

        ControllerUserDataForTest(ViewerUserDataIf<View, Object> viewer)
        {
            super(viewer);
        }

        @Override
        public int clearSubscriptions()
        {
            assertThat(flagForExecution.getAndSet(29), is(0));
            return 99;
        }
    }
}