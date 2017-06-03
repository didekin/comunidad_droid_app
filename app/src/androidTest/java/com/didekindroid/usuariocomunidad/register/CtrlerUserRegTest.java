package com.didekindroid.usuariocomunidad.register;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.register.CtrlerUserReg.userAndComuRegistered;
import static com.didekindroid.usuariocomunidad.register.CtrlerUserReg.userComuAndComuRegistered;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/05/17
 * Time: 09:44
 */
public class CtrlerUserRegTest {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    CtrlerUserReg controller;
    ActivityMock activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        controller = new CtrlerUserReg();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    @Test
    public void test_UserAndComuRegistered() throws Exception
    {
        checkNoInitCache();

        userAndComuRegistered(COMU_ESCORIAL_PEPE).test().assertComplete();
        checkInitTokenCache();
        assertThat(userComuDaoRemote.seeUserComusByUser().get(0), is(COMU_ESCORIAL_PEPE));
    }

    @Test
    public void test_UserComuAndComuRegistered() throws Exception
    {
        Usuario pepe = signUpAndUpdateTk(COMU_LA_FUENTE_PEPE);
        userComuAndComuRegistered(new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, pepe).planta("uno").roles(PROPIETARIO.function).build()).test().assertResult(true);
        assertThat(userComuDaoRemote.seeUserComusByUser().size(), is(2));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void test_RegisterComuAndUser() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            controller.registerUserAndComu(new ObserverCacheCleaner(controller), COMU_LA_FUENTE_PEPE);
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void test_RegisterUserComuAndComu() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            controller.registerUserComuAndComu(
                    new DisposableSingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(Boolean rowInserted)
                        {
                            assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                            assertThat(rowInserted, is(true));
                        }
                        @Override
                        public void onError(Throwable e)
                        {
                            fail();
                        }
                    },
                    new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, signUpAndUpdateTk(COMU_LA_FUENTE_PEPE))
                            .planta("uno").roles(PROPIETARIO.function).build());
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }
}