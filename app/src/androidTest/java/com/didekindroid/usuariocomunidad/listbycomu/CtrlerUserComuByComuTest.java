package com.didekindroid.usuariocomunidad.listbycomu;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.listbycomu.CtrlerUserComuByComuList.comunidad;
import static com.didekindroid.usuariocomunidad.listbycomu.CtrlerUserComuByComuList.listByEntityId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 26/03/17
 * Time: 14:53
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerUserComuByComuTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    CtrlerUserComuByComuList controller;
    ActivityMock activity;
    Comunidad comunidad;
    Usuario usuario;

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                usuario = signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                comunidad = userComuDaoRemote.seeUserComusByUser().get(0).getComunidad();
            } catch (UiException | IOException e) {
                fail();
            }
        }
    };

    @Before
    public void setUp() throws IOException, UiException
    {
        activity = activityRule.getActivity();
    }

    @After
    public void clearUp() throws UiException
    {
        cleanOneUser(USER_JUAN);
    }

    // .................................... OBSERVABLES .................................

    @Test
    public void testListByEntityId() throws IOException, UiException
    {
        listByEntityId(comunidad.getC_Id()).test().assertOf(new Consumer<TestObserver<List<UsuarioComunidad>>>() {
            @Override
            public void accept(TestObserver<List<UsuarioComunidad>> listTestObserver) throws Exception
            {
                UsuarioComunidad usuarioComunidad = listTestObserver.values().get(0).get(0);
                assertThat(usuarioComunidad.getUsuario(), is(USER_JUAN));
            }
        });
    }

    @Test
    public void testComunidad() throws IOException, UiException
    {
        comunidad(comunidad.getC_Id()).test().assertResult(COMU_LA_PLAZUELA_5);
    }

    // .................................... INSTANCE METHODS .................................

    @Test
    public void testLoadItemsByEntitiyId() throws IOException, UiException
    {
        controller = new CtrlerUserComuByComuList();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();

            assertThat(controller.loadItemsByEntitiyId(new TestDisposableSingleObserver<List<UsuarioComunidad>>(), comunidad.getC_Id()), is(true));
            assertThat(controller.getSubscriptions().size(), is(1));
            // We test here controller.onSuccessLoadItemsInList() indirectly:
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
        } finally {
            resetAllSchedulers();
        }
    }

    @Test
    public void testComunidadData()
    {
        controller = new CtrlerUserComuByComuList();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();

            assertThat(controller.comunidadData(new TestDisposableSingleObserver<Comunidad>(), comunidad.getC_Id()), is(true));
            assertThat(controller.getSubscriptions().size(), is(1));
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
        } finally {
            resetAllSchedulers();
        }
    }

    // .................................... HELPERS .................................

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
