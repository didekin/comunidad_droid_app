package com.didekindroid.usuario.login;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDaoTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
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
public class CtrlerUsuario_Login_Test {

    CtrlerUsuario controller;

    @Before
    public void setUp() throws Exception
    {
        controller = new CtrlerUsuario();
    }

    @After
    public void cleanUp() throws UiException
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testValidateLogin() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.validateLogin(new TestDisposableSingleObserver(), USER_DROID), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        cleanOptions(CLEAN_DROID);
    }

    @Test   // With mock callable to avoid change identity data in cache.
    public void test_SendNewPassword() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.sendNewPassword(new UsuarioDaoTestUtil.SendPswdCallable(), new TestDisposableSingleObserver()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    static class TestDisposableSingleObserver extends DisposableSingleObserver<Boolean> {
        @Override
        public void onSuccess(Boolean aBoolean)
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