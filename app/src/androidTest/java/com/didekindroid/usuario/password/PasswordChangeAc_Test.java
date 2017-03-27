package com.didekindroid.usuario.password;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.user_data_ac_layout;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.password.ViewerPasswordChange.newViewerPswdChange;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typePswdData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 17:45
 */
@RunWith(AndroidJUnit4.class)
public class PasswordChangeAc_Test implements ExtendableTestAc {

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {

        @Override
        protected Intent getActivityIntent()
        {
            Usuario usuario = null;
            try {
                usuario = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };

    PasswordChangeAc activity;
    private int activityLayoutId = R.id.password_change_ac_layout;
    ViewerPasswordChange viewer;

    @BeforeClass
    public static void relax() throws InterruptedException
    {
        TimeUnit.MILLISECONDS.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
        viewer = (ViewerPasswordChange) newViewerPswdChange(activity);
    }

    @Override
    public void checkNavigateUp()
    {
        fail("NO NAVIGATE-UP in PasswordChange manager");
    }

    @Override
    public int getNextViewResourceId()
    {
        return user_data_ac_layout;
    }

    @Test
    public void testOnCreate() throws Exception
    {
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));

        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_confirm_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password_change_ac_button)).check(matches(withText(R.string.password_change_ac_button_txt)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();

        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testPasswordChange_OK() throws UiException, InterruptedException
    {
        typePswdData("new_pepe_password", "new_pepe_password");
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_remote_change, activity);
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        usuarioDao.deleteUser();
    }

    @Test
    public final void testReplaceView()
    {
        // TODO.
        /*checkViewerReplaceView(activity, getNextViewResourceId());
        checkToastInTest(R.string.password_remote_change, activity);*/
    }

    @Test
    public final void testOnStop() throws Exception
    {
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(viewer.getController().getSubscriptions().size(), is(0));
    }
}