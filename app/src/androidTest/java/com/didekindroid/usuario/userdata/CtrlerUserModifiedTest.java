package com.didekindroid.usuario.userdata;

import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
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

    CtrlerUserModified controller;

    // ..................................... INSTANCE METHODS ..........................................

    @Before
    public void setUp() throws Exception
    {
        controller = new CtrlerUserModified();
    }

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

        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

    public Usuario doNewUser(Usuario oldUsuario)
    {
        return new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(oldUsuario.getuId())
                .password(USER_PEPE.getPassword())
                .build();
    }

    // ..................................... HELPERS ..........................................

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