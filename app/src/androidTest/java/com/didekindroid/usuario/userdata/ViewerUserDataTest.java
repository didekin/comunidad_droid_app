package com.didekindroid.usuario.userdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.exception.UiExceptionRouter.GENERIC_APP_ACC;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlError;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserData;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.userDataAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/03/17
 * Time: 14:16
 */
@RunWith(AndroidJUnit4.class)
public class ViewerUserDataTest {

    Usuario usuario;

    @Rule
    public ActivityTestRule<UserDataAc> activityRule = new ActivityTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                usuario = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };

    UserDataAc activity;

    @BeforeClass
    public static void calm() throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(3);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        AtomicReference<ViewerUserData> atomicViewer = new AtomicReference<>(null);
        atomicViewer.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(atomicViewer, notNullValue());
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(USER_PEPE);
    }

    // ============================================================
    //    .................... TESTS ....................
    // ============================================================

    @Test
    public void test_NewViewerUserData() throws Exception
    {
        assertThat(activity.viewer.emailView, notNullValue());
        assertThat(activity.viewer.aliasView, notNullValue());
        assertThat(activity.viewer.passwordView, notNullValue());
        assertThat(activity.viewer.getController(), instanceOf(CtrlerUserModified.class));
        assertThat(activity.viewer.usuarioBean, notNullValue());
        assertThat(activity.viewer.oldUser, notNullValue());
        assertThat(activity.viewer.newUser, notNullValue());
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        waitAtMost(4, SECONDS).untilAtomic(activity.viewer.oldUser, is(usuario));
        checkUserDataLoaded();
    }

    @Test
    public void testProcessBackUserDataLoaded() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.viewer.processBackUserDataLoaded(usuario);
                checkUserDataLoaded();
            }
        });
    }

    @Test
    public void testCheckUserData_1() throws Exception
    {
        TimeUnit.SECONDS.sleep(2);

        typeUserData("newuser@user.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());
        runCheckUserData(true);
        Usuario usuario = activity.viewer.usuarioBean.get().getUsuario();

        assertThat(usuario.getUserName(), is("newuser@user.com"));
        assertThat(usuario.getAlias(), is(USER_JUAN.getAlias()));
        assertThat(usuario.getPassword(), is(USER_JUAN.getPassword()));
    }

    @Test
    public void testCheckUserData_2() throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(2);

        typeUserData("wrong_newuser.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());
        runCheckUserData(false);
        waitAtMost(6, SECONDS).until(isToastInView(R.string.email_hint, activity));
    }

    @Test
    public void testWhatDataChangeToMake() throws Exception
    {
        // Caso 1: datos de entrada (usuarioBean) == oldUser.
        activity.viewer.oldUser.set(new Usuario.UsuarioBuilder().alias(USER_JUAN.getAlias()).userName(USER_JUAN.getUserName()).build());
        typeUserData(USER_JUAN.getUserName(), USER_JUAN.getAlias(), USER_JUAN.getPassword());
        runWhatDataChange(nothing);

        // Caso 2: datos de entrada userName == oldUser.userName.
        typeUserData(USER_JUAN.getUserName(), "new_alias", USER_JUAN.getPassword());
        runWhatDataChange(alias_only);

        // Caso 3: datos de entrada userName != oldUser.userName.
        typeUserData("new@userName.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());
        runWhatDataChange(userName);

        // Caso 4: datos de entrada userName != oldUser.
        typeUserData("new@userName.com", "new_alias", USER_JUAN.getPassword());
        runWhatDataChange(userName);
    }

    public void testModifyUserData() throws Exception
    {
        runModifyUser(nothing);
        waitAtMost(3, SECONDS).until(isToastInView(R.string.no_user_data_to_be_modified, activity));

        runModifyUser(userName);
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));

        runModifyUser(alias_only);
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }

    @Test
    public void testProcessControllerError_1() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.viewer.onErrorInObserver(new UiException(new ErrorBean(BAD_REQUEST)));
            }
        });
        waitAtMost(3, SECONDS).until(isToastInView(R.string.password_wrong, activity));
        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void testProcessControllerError_2()
    {
        assertThat(checkProcessCtrlError(activity.viewer, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC), is(true));
    }

    @Test
    public void test_ReplaceComponent() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.viewer.replaceComponent(new Bundle(0));
            }
        });
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }

    // ============================================================
    //    .................... Helpers ....................
    // ============================================================

    public void checkUserDataLoaded()
    {
        assertThat(activity.viewer.oldUser.get(), is(usuario));
        assertThat(activity.viewer.emailView.getText().toString(), is(usuario.getUserName()));
        assertThat(activity.viewer.aliasView.getText().toString(), is(usuario.getAlias()));
        assertThat(activity.viewer.passwordView.getHint(), is(activity.getText(R.string.user_data_ac_password_hint)));
    }

    public void runModifyUser(final UserChangeToMake change)
    {
        final AtomicBoolean isModified = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                isModified.compareAndSet(false, activity.viewer.modifyUserData(change));
            }
        });
        waitAtMost(4, SECONDS).untilTrue(isModified);
    }

    public void runCheckUserData(final boolean isOk)
    {
        final AtomicBoolean isChecked = new AtomicBoolean(!isOk);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                boolean isUserDataOk = activity.viewer.checkUserData();
                isChecked.compareAndSet(!isOk, isUserDataOk);
            }
        });
        waitAtMost(6, SECONDS).untilAtomic(isChecked, is(isOk));
    }

    public void runWhatDataChange(final UserChangeToMake changeToMake)
    {
        final AtomicReference<UserChangeToMake> atomicChange = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.viewer.checkUserData();
                activity.viewer.whatDataChangeToMake();
                atomicChange.compareAndSet(null, changeToMake);
            }
        });
        waitAtMost(6, SECONDS).untilAtomic(atomicChange, is(changeToMake));
    }
}