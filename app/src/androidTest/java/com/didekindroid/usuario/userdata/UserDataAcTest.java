package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
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
import static com.didekindroid.R.id.user_data_modif_button;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceComponent;
import static com.didekindroid.testutil.ActivityTestUtils.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtils.focusOnButton;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserData;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.DELETE_ME_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.userDataAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
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
@RunWith(AndroidJUnit4.class)
public class UserDataAcTest {

    UserDataAc activity;
    Usuario oldUsuario;
    Comunidad comunidad;
    TaskStackBuilder stackBuilder;

    @Rule
    public IntentsTestRule<? extends Activity> mActivityRule = new IntentsTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                oldUsuario = signUpAndUpdateTk(COMU_REAL_JUAN);
                comunidad = userComuDaoRemote.getComusByUser().get(0);
                assertThat(oldUsuario, notNullValue());
            } catch (Exception e) {
                fail();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(getTargetContext(), UserComuDataAc.class);
                intent.putExtra(USERCOMU_LIST_OBJECT.key,
                        new UsuarioComunidad.UserComuBuilder(comunidad, oldUsuario).userComuRest(COMU_REAL_JUAN).build());
                stackBuilder = create(getTargetContext());
                // Intent con UserComuDataAc y USERCOMU_LIST_OBJECT hace falta para checkUp en opción menú INCID_SEE_OPEN_BY_COMU_AC.
                stackBuilder.addNextIntent(intent).addParentStack(UserDataAc.class).startActivities();
            }
        }
    };

    @BeforeClass
    public static void calm() throws InterruptedException
    {
        SECONDS.sleep(3);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = (UserDataAc) mActivityRule.getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @After
    public void tearDown() throws Exception
    {
        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    // ============================================================
    //    ................ INTEGRATION TESTS ..............
    // ============================================================

    @Test
    public void testBackStack() throws ExecutionException, InterruptedException
    {
        List<Intent> intents = Arrays.asList(stackBuilder.getIntents());
        assertThat(intents.size(), is(3));
        // El intent con posición inferior es el primero que hemos añadido.
        assertThat(intents.get(0).getComponent().getClassName(), is("com.didekindroid.usuariocomunidad.data.UserComuDataAc"));
    }

    @Test
    public void testOncreate()
    {
        assertThat(activity.viewer, notNullValue());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(userDataAcRsId));
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(allOf(withId(R.id.reg_usuario_email_editT), withText(containsString(oldUsuario.getUserName())))));
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(allOf(withId(R.id.reg_usuario_alias_ediT), withText(containsString(oldUsuario.getAlias())))));
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(
                allOf(
                        withId(R.id.password_validation_ediT),
                        withText(containsString("")),
                        withHint(R.string.user_data_ac_password_hint)
                )));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.user_data_modif_button));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.appbar));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    @Test  // Wrong password.
    public void testModifyUserDataWrongPswd() throws InterruptedException
    {
        SECONDS.sleep(2);
        typeUserData("new_juan@juan.es", USER_JUAN.getAlias(), "wrong_password");
        onView(withId(user_data_modif_button)).perform(scrollTo()).check(matches(isDisplayed())).perform(click());
        waitAtMost(6, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }

    @Test  // Modify user OK.
    public void testModifyUserData_Up() throws UiException, InterruptedException
    {
        SECONDS.sleep(2);
        typeClickWait();
        // Verificamos navegación.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test  // Modify user OK.
    public void testModifyUserData_Back() throws UiException, InterruptedException
    {
        SECONDS.sleep(2);
        typeClickWait();
        checkBack(onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed())), userDataAcRsId);
    }

    @Test
    public final void testOnStop() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getInstrumentation().callActivityOnStop(activity);
                // Check.
                assertThat(activity.viewer.getController().getSubscriptions().size(), is(0));
            }
        });
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
        COMU_SEARCH_AC.checkItemRegisterUser(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        // NO navigate-up.
    }

    @Test
    public void testDeleteMeMn() throws InterruptedException
    {
        DELETE_ME_AC.checkItemRegisterUser(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        checkUp(userDataAcRsId);
    }

    @Test
    public void testPasswordChangeMn() throws InterruptedException
    {
        PASSWORD_CHANGE_AC.checkItemRegisterUser(activity);
        SECONDS.sleep(1);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        checkUp(userDataAcRsId);
    }

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkItemRegisterUser(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testIncidSeeByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(activity);
        intended(hasExtra(user_name.key, oldUsuario.getUserName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(userComuDataLayout);
        }
    }

    /*    =================================  HELPERS ==================================*/

    public void typeClickWait()
    {
        typeUserData("new@username.com", "new_alias", USER_JUAN.getPassword());
        focusOnButton(activity, user_data_modif_button);
        onView(withId(user_data_modif_button)).perform(scrollTo(), click());

        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }
}