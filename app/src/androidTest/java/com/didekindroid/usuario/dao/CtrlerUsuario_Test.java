package com.didekindroid.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUpdatedCacheAfterPswd;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 14:17
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerUsuario_Test {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    CtrlerUsuario controller;

    @Before
    public void setUp() throws Exception
    {
        controller = new CtrlerUsuario();
    }

    @After
    public void cleanUp() throws UiException
    {
        assertThat(controller.clearSubscriptions(), is(0));
        resetAllSchedulers();
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testChangePassword() throws Exception
    {
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        SpringOauthToken oldToken = TKhandler.getTokenCache().get();

        Usuario newUser = new Usuario.UsuarioBuilder().userName(USER_PEPE.getUserName()).password("new_password").build();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.changePassword(
                    new DisposableCompletableObserver() {
                        @Override
                        public void onComplete()
                        {
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            fail();
                        }
                    }, USER_PEPE, newUser),
                    is(true));
        } finally {
            resetAllSchedulers();
        }

        assertThat(controller.getSubscriptions().size(), is(1));
        // onComplete()
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
        checkUpdatedCacheAfterPswd(true, oldToken);
        usuarioDaoRemote.deleteUser();
    }

    @Test
    public void testDeleteMe() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.deleteMe(new DisposableSingleObserver<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean)
                {
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testLoadUserData() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadUserData(
                    new TestSingleObserver<Usuario>() {
                        @Override
                        public void onSuccess(Usuario usuario)
                        {
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                        }
                    }),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));

        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testModifyUser() throws Exception
    {
        Usuario oldUser = new Usuario.UsuarioBuilder().copyUsuario(signUpAndUpdateTk(COMU_ESCORIAL_PEPE)).password(USER_PEPE.getPassword()).build();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.modifyUser(
                    new TestSingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(Boolean item)
                        {
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                        }
                    }, oldUser, doNewUser(oldUser)),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));

        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

    @Test   // With mock callable to avoid change identity data in cache.
    public void test_SendNewPassword() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.sendNewPassword(
                    new UsuarioDaoTestUtil.SendPswdCallable(),
                    new TestSingleObserver<Boolean>()),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testValidateLogin() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.validateLogin(new TestSingleObserver<Boolean>(), USER_DROID), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        cleanOptions(CLEAN_DROID);
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    public Usuario doNewUser(Usuario oldUsuario)
    {
        return new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(oldUsuario.getuId())
                .password(USER_PEPE.getPassword())
                .build();
    }

    static class TestSingleObserver<T> extends DisposableSingleObserver<T> {

        @Override
        public void onSuccess(T successBack)
        {
            dispose();
        }

        @Override
        public void onError(Throwable e)
        {
            dispose();
            Timber.d("============= %s =============", e.getClass().getName());
            fail();
        }
    }
}