package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.userdata.CtrlerUserModified.userDataLoaded;
import static com.didekindroid.usuario.userdata.CtrlerUserModified.userModifiedTkUpdated;
import static com.didekindroid.usuario.userdata.CtrlerUserModified.userModifiedWithPswdValidation;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 14:29
 */
public class CtrlerUserModifiedTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerUserModified controller;

    @Before
    public void setUp() throws Exception
    {
        Activity activity = activityRule.getActivity();
        controller = new CtrlerUserModified();
    }

    // ..................................... OBSERVABLES ..........................................

    @Test
    public void testUserDataLoaded() throws IOException, UiException
    {

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        // Caso OK.
        userDataLoaded().test().assertResult(USER_PEPE);
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void test_UserModifiedTokenUpdated() throws Exception
    {
        Usuario oldUsuario = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        userModifiedTkUpdated(TKhandler.getTokenCache().get(), doNewUser(oldUsuario)).test().awaitDone(4, SECONDS).assertComplete();

        // El hecho de poder borrar implica que la cache se ha actualizado correctamente.
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    @Test
    public void test_UserModifiedWithPswdValidation() throws Exception
    {
        Usuario oldUsuario = new Usuario.UsuarioBuilder().copyUsuario(signUpAndUpdateTk(COMU_ESCORIAL_PEPE)).password(USER_PEPE.getPassword()).build();
        userModifiedWithPswdValidation(oldUsuario, doNewUser(oldUsuario)).test().assertResult(true);

        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    // ..................................... INSTANCE METHODS ..........................................

    @Test
    public void testLoadUserData() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadUserData(new TestDisposableSingleObserver<Usuario>()), is(true));
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
            assertThat(controller.modifyUser(new TestDisposableSingleObserver<Boolean>(), oldUser, doNewUser(oldUser)), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));

        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    // ..................................... HELPERS ..........................................

    public Usuario doNewUser(Usuario oldUsuario)
    {
        return new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(oldUsuario.getuId())
                .password(USER_PEPE.getPassword())
                .build();
    }

    static class TestDisposableSingleObserver<T> extends DisposableSingleObserver<T> {
        @Override
        public void onSuccess(T item)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void onError(Throwable e)
        {
            fail();
        }
    }
}