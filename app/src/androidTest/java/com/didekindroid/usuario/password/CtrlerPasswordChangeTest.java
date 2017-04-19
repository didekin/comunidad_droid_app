package com.didekindroid.usuario.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.password.CtrlerPasswordChange.isPasswordChanged;
import static com.didekindroid.usuario.password.ViewerPasswordChange.newViewerPswdChange;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 13:44
 */
public class CtrlerPasswordChangeTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(user_name.key, USER_DROID.getUserName());
        }
    };

    CtrlerPasswordChangeIf controller;
    ViewerPasswordChangeIf viewer;
    private SpringOauthToken oldToken;

    @Before
    public void setUp()
    {
        viewer = newViewerPswdChange((PasswordChangeAc) activityRule.getActivity());
        controller = new CtrlerPasswordChange(viewer);
        oldToken = TKhandler.getTokenCache().get();
    }

    @Test
    public void testChangePasswordInRemote() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.changePasswordInRemote(USER_PEPE), is(true));
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testOnSuccessChangePswd() throws Exception
    {
        controller.onSuccessChangedPswd();
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    // ............................ OBSERVABLES ..................................

    @Test
    public void testIsPasswordChanged() throws Exception
    {
        Usuario usuario = new Usuario.UsuarioBuilder().userName(USER_PEPE.getUserName()).password("new_password").build();
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);

        try {
            trampolineReplaceIoScheduler();
            isPasswordChanged(usuario).test().assertComplete();
        } finally {
            reset();
        }
        checkCacheAndDeleteUser(true);
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    private void checkCacheAndDeleteUser(boolean isPswdUpdated) throws UiException
    {
        checkInitTokenCache();
        if (isPswdUpdated) {
            assertThat(TKhandler.getTokenCache().get(), not(is(oldToken)));
        } else {
            assertThat(TKhandler.getTokenCache().get(), is(oldToken));
        }
        usuarioDao.deleteUser();
    }

    static final class ViewerPasswordChangeForTest extends ViewerPasswordChange {

        private ViewerPasswordChangeForTest(PasswordChangeAc activity)
        {
            super(activity);
        }

        @Override
        public void replaceComponent(@NonNull Bundle bundle)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }
    }
}