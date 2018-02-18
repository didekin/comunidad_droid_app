package com.didekindroid.lib_one.usuario.dao;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.security.SecurityTestUtils.checkUpdatedCacheAfterPswd;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_B;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
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
        // Precondition.
        regUserComuWithTkCache(comu_real_rodrigo);

        SpringOauthToken oldToken = controller.getTkCacher().getTokenCache().get();
        Usuario newUser = new Usuario.UsuarioBuilder().userName(user_crodrigo.getUserName()).password("new_password").build();

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
                    }, user_crodrigo, newUser),
                    is(true));
        } finally {
            resetAllSchedulers();
        }

        assertThat(controller.getSubscriptions().size(), is(1));
        // onComplete()
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
        checkUpdatedCacheAfterPswd(true, oldToken, controller.getTkCacher());
        usuarioDaoRemote.deleteUser();
    }

    @Test
    public void testDeleteMe() throws Exception
    {
        regUserComuWithTkCache(comu_real_rodrigo);

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
        regUserComuWithTkCache(comu_real_rodrigo);
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

        cleanOneUser(user_crodrigo);
    }

    @Test
    public void testModifyUserName() throws Exception
    {
        Usuario oldUser = new Usuario.UsuarioBuilder().copyUsuario(regGetUserComu(comu_real_rodrigo)).password(user_crodrigo.getPassword()).build();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.modifyUserName(
                    new TestSingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(Boolean item)
                        {
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                        }
                    }, oldUser, new Usuario.UsuarioBuilder()
                            .copyUsuario(oldUser)
                            .userName(USER_DROID.getUserName())
                            .password(user_crodrigo.getPassword())
                            .build()),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));

        assertThat(usuarioMockDao.deleteUser(USER_DROID.getUserName()).execute().body(), is(true));
        cleanWithTkhandler();
    }

    @Test
    public void testModifyUserAlias() throws Exception
    {
        Usuario oldUser = new Usuario.UsuarioBuilder().copyUsuario(regGetUserComu(comu_real_rodrigo)).password(user_crodrigo.getPassword()).build();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.modifyUserAlias(
                    new TestSingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(Boolean item)
                        {
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                        }
                    }, oldUser, new Usuario.UsuarioBuilder().copyUsuario(oldUser).alias("new_pepe_alias").build()),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));

        cleanOneUser(user_crodrigo);
    }

    @Test   // With mock callable to avoid change identity data in cache.
    public void test_SendNewPassword() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.sendNewPassword(
                    new UsuarioDaoTestUtil.SendPswdCallable(),
                    new TestSingleObserver<>()),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testValidateLogin() throws Exception
    {
        regUserComuWithTkCache(comu_real_rodrigo);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.validateLogin(new TestSingleObserver<>(), user_crodrigo), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        cleanOneUser(user_crodrigo);
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

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