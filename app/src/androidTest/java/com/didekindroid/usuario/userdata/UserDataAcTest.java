package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
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
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceComponent;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserData;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.DELETE_ME_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.userDataAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 16/07/15
 * Time: 14:25
 */
public class UserDataAcTest {

    UserDataAc activity;
    Usuario oldUsuario;

    @Rule
    public IntentsTestRule<? extends Activity> mActivityRule = new IntentsTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                oldUsuario = signUpAndUpdateTk(COMU_REAL_JUAN);
                assertThat(oldUsuario, notNullValue());
            } catch (Exception e) {
                fail();
            }
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = (UserDataAc) mActivityRule.getActivity();
        TimeUnit.MILLISECONDS.sleep(2000);
    }

    @After
    public void tearDown() throws Exception
    {
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    // ============================================================
    //    ................ INTEGRATION TESTS ..............
    // ============================================================

    @Test
    public void testOncreate()
    {
        assertThat(activity.viewer, notNullValue());

        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));

        onView(withId(reg_usuario_email_editT))
                .check(matches(withText(containsString(oldUsuario.getUserName()))));
        onView(withId(reg_usuario_alias_ediT))
                .check(matches(withText(containsString(oldUsuario.getAlias()))));
        onView(allOf(withId(user_data_ac_password_ediT),
                withHint(R.string.user_data_ac_password_hint)))
                .check(matches(withText(containsString(""))));

        onView(withId(user_data_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test  // Wrong password.
    public void testModifyUserDataWrongPswd() throws InterruptedException
    {
        typeUserData("new_juan@juan.es", USER_JUAN.getAlias(), "wrong_password");
        onView(withId(user_data_modif_button)).perform(scrollTo()).check(matches(isDisplayed())).perform(click());
        waitAtMost(4, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }

    @Test  // Modify user OK.
    public void testModifyUserDataAndUp() throws UiException
    {
        typeClickWait();
        // Verificamos navegación.
        checkUp(userDataAcRsId);
    }

    @Test  // Modify user OK.
    public void testModifyUserDataAndBack() throws UiException
    {
        typeClickWait();
        checkBack(onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed())), userDataAcRsId);
    }

    @Test
    public final void testOnStop() throws Exception
    {
        getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(activity.viewer.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void testReplaceRootView()
    {
        checkViewerReplaceComponent(activity.viewer, seeUserComuByUserFrRsId, null);
    }

    //    =================================  MENU TESTS ==================================

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        // NO navigate-up.
    }

    @Test
    public void testDeleteMeMn() throws InterruptedException
    {
        DELETE_ME_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        checkUp(userDataAcRsId);
    }

    @Test
    public void testPasswordChangeMn() throws InterruptedException
    {
        PASSWORD_CHANGE_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        checkUp(userDataAcRsId);
    }

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        checkUp(userDataAcRsId);
    }

    @Test
    public void testIncidSeeByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        checkUp(userDataAcRsId);
    }

    /*    =================================  HELPERS ==================================*/

    private void checkInitialData(String userName, String alias)
    {
        assertThat(((EditText) activity.acView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(), is(userName));
        assertThat(((EditText) activity.acView.findViewById(R.id.reg_usuario_alias_ediT)).getText().toString(), is(alias));
    }

    public void typeClickWait()
    {
        typeUserData("new@username.com", "new_alias", USER_JUAN.getPassword());
        onView(withId(user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }
}