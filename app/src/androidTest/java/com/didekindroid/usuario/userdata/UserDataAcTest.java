package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.widget.EditText;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
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
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceComponent;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserData;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.DELETE_ME_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.userdata.ViewerUserData.newViewerUserData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 16/07/15
 * Time: 14:25
 */
public class UserDataAcTest implements ExtendableTestAc {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    UserDataAc activity;
    ViewerUserData viewer;
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
        viewer = (ViewerUserData) newViewerUserData(activity);
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
        typeUserData("new_juan@juan.es", USER_JUAN.getAlias(), "wrong_password");
        onView(withId(user_data_modif_button)).perform(scrollTo()).check(matches(isDisplayed())).perform(click());
        waitAtMost(1, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }

    @Test  // Integration test: modify user OK.
    public void testModifyUserData_B() throws UiException
    {
        typeUserData("new@username.com", "new_alias", USER_JUAN.getPassword());
        onView(withId(user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(getNextViewResourceId()));
        // Verificamos navegación.
        checkNavigateUp();
    }

    @Test
    public final void testOnStop() throws Exception
    {
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(viewer.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void testReplaceRootView()
    {
        checkViewerReplaceComponent(viewer, getNextViewResourceId(), null);
    }

    //    =================================  MENU TESTS ==================================

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        COMU_SEARCH_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, viewer.oldUser.get().getUserName()));
        // NO navigate-up.
    }

    @Test
    public void testDeleteMeMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        DELETE_ME_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, viewer.oldUser.get().getUserName()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testPasswordChangeMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        PASSWORD_CHANGE_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, viewer.oldUser.get().getUserName()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, viewer.oldUser.get().getUserName()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testIncidSeeByComuMn() throws InterruptedException
    {
        // Preconditions.
        waitAtMost(1500, MILLISECONDS).until(fieldIn(activity).ofType(Usuario.class).andWithName("oldUser"), equalTo(registeredUser));
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key,viewer.oldUser.get().getUserName()));
        checkUp(activityLayoutId);
    }

    /*    =================================  HELPERS ==================================*/

    private void checkInitialData(String userName, String alias)
    {
        assertThat(((EditText) activity.acView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(), is(userName));
        assertThat(((EditText) activity.acView.findViewById(R.id.reg_usuario_alias_ediT)).getText().toString(), is(alias));
    }
}