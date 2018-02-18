package com.didekindroid.usuario;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake;
import com.didekindroid.usuario.UserDataAc;
import com.didekindroid.usuario.ViewerUserData;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeUserNameAliasPswd;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.userName;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
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
    private boolean isClean;

    @Before
    public void setUp() throws Exception
    {
        isClean = false;
        activity = activityRule.getActivity();
        AtomicReference<ViewerUserData> atomicViewer = new AtomicReference<>(null);
        atomicViewer.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(atomicViewer, notNullValue());
    }

    @After
    public void cleanUp() throws UiException
    {
        if (isClean) {
            return;
        }
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
        assertThat(activity.viewer.getController(), instanceOf(CtrlerUsuario.class));
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
        activity.runOnUiThread(() -> {
            activity.viewer.processBackUserDataLoaded(usuario);
            checkUserDataLoaded();
        });
    }

    @Test
    public void testCheckUserData_1() throws Exception
    {
        SECONDS.sleep(2);

        typeUserNameAliasPswd("newuser@user.com", USER_PEPE.getAlias(), USER_PEPE.getPassword());
        runCheckUserData(true);
        Usuario usuario = activity.viewer.usuarioBean.get().getUsuario();

        assertThat(usuario.getUserName(), is("newuser@user.com"));
        assertThat(usuario.getAlias(), is(USER_PEPE.getAlias()));
        assertThat(usuario.getPassword(), is(USER_PEPE.getPassword()));
    }

    @Test
    public void testCheckUserData_2() throws InterruptedException
    {
        SECONDS.sleep(2);

        typeUserNameAliasPswd("wrong_newuser.com", USER_PEPE.getAlias(), USER_PEPE.getPassword());
        runCheckUserData(false);
        waitAtMost(6, SECONDS).until(isToastInView(R.string.email_hint, activity));
    }

    @Test
    public void testWhatDataChangeToMake() throws Exception
    {
        // Caso 1: datos de entrada (usuarioBean) == oldUser.
        activity.viewer.oldUser.set(new Usuario.UsuarioBuilder().alias(USER_PEPE.getAlias()).userName(USER_PEPE.getUserName()).build());
        typeUserNameAliasPswd(USER_PEPE.getUserName(), USER_PEPE.getAlias(), USER_PEPE.getPassword());
        runWhatDataChange(nothing);

        // Caso 2: datos de entrada userName == oldUser.userName.
        typeUserNameAliasPswd(USER_PEPE.getUserName(), "new_alias", USER_PEPE.getPassword());
        runWhatDataChange(alias_only);

        // Caso 3: datos de entrada userName != oldUser.userName.
        typeUserNameAliasPswd("new@userName.com", USER_PEPE.getAlias(), USER_PEPE.getPassword());
        runWhatDataChange(userName);

        // Caso 4: datos de entrada userName != oldUser.
        typeUserNameAliasPswd("new@userName.com", "new_alias", USER_PEPE.getPassword());
        runWhatDataChange(userName);
    }

    @Test
    public void testModifyUserData_1() throws Exception
    {
        // No change.
        waitAtMost(6, SECONDS).untilAtomic(activity.viewer.oldUser, is(usuario));
        activity.runOnUiThread(() -> activity.viewer.modifyUserData(nothing));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.no_user_data_to_be_modified, activity));
    }

    @Test
    public void testModifyUserData_2() throws Exception
    {
        // Datos de entrada userName == oldUser.userName.
        waitAtMost(6, SECONDS).untilAtomic(activity.viewer.oldUser, is(usuario));
        activity.viewer.oldUser.set(new Usuario.UsuarioBuilder().copyUsuario(usuario).password(USER_PEPE.getPassword()).build());
        activity.viewer.newUser.set(new Usuario.UsuarioBuilder().copyUsuario(activity.viewer.oldUser.get()).build());
        activity.runOnUiThread(() -> activity.viewer.modifyUserData(alias_only));
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }

    @Test
    public void testModifyUserData_3() throws Exception
    {
        // Datos de entrada userName != oldUser.userName.
        waitAtMost(6, SECONDS).untilAtomic(activity.viewer.oldUser, is(usuario));
        activity.viewer.oldUser.set(new Usuario.UsuarioBuilder().copyUsuario(usuario).password(USER_PEPE.getPassword()).build());
        activity.viewer.newUser.set(new Usuario.UsuarioBuilder().copyUsuario(activity.viewer.oldUser.get()).userName(USER_DROID.getUserName()).build());
        activity.runOnUiThread(() -> activity.viewer.modifyUserData(userName));
        checkTextsInDialog(R.string.receive_password_by_mail_dialog, R.string.continuar_button_rot);

        assertThat(usuarioMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
        isClean = true;
    }

    @Test
    public void testProcessControllerError_1() throws Exception
    {
        activity.runOnUiThread(() -> activity.viewer.onErrorInObserver(new UiException(new ErrorBean(BAD_REQUEST))));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.password_wrong, activity));
        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));
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

    public void runCheckUserData(final boolean isOk)
    {
        final AtomicBoolean isChecked = new AtomicBoolean(!isOk);
        activity.runOnUiThread(() -> {
            boolean isUserDataOk = activity.viewer.checkUserData();
            isChecked.compareAndSet(!isOk, isUserDataOk);
        });
        waitAtMost(6, SECONDS).untilAtomic(isChecked, is(isOk));
    }

    public void runWhatDataChange(final UserChangeToMake changeToMake)
    {
        final AtomicReference<UserChangeToMake> atomicChange = new AtomicReference<>(null);
        activity.runOnUiThread(() -> {
            activity.viewer.checkUserData();
            activity.viewer.whatDataChangeToMake();
            atomicChange.compareAndSet(null, changeToMake);
        });
        waitAtMost(6, SECONDS).untilAtomic(atomicChange, is(changeToMake));
    }
}