package com.didekindroid.usuariocomunidad.register;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekindroid.api.SingleObserverMock;
import com.didekindroid.api.Viewer;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Predicate;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.testutil.ActivityTestUtils.checkIsRegistered;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_C;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad.isOldestAdmonUser;
import static com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad.userAndComuRegistered;
import static com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad.userAndUserComuRegistered;
import static com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad.userComuAndComuRegistered;
import static com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad.userComuModified;
import static com.didekindroid.usuariocomunidad.register.CtrlerUsuarioComunidad.userComuRegistered;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMockDaoRemote.userComuMockDao;
import static com.didekinlib.http.UsuarioServConstant.IS_USER_DELETED;
import static com.didekinlib.model.usuariocomunidad.Rol.ADMINISTRADOR;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/05/17
 * Time: 09:44
 */
public class CtrlerUsuarioComunidadTest {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    Viewer<?, CtrlerUsuarioComunidad> viewer;
    CtrlerUsuarioComunidad controller;
    ActivityMock activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        viewer = new Viewer<>(null, activity, null);
        viewer.setController(new CtrlerUsuarioComunidad());
        controller = viewer.getController();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    @Test
    public void test_UserAndComuRegistered() throws Exception
    {
        checkNoInitCache();

        userAndComuRegistered(COMU_ESCORIAL_PEPE).test().assertComplete();
        checkIsRegistered(viewer);
        checkNoInitCache();

        assertThat(userComuMockDao.deleteUser(USER_PEPE.getUserName()).execute().body(), is(true));
    }

    @Test
    public void test_UserComuAndComuRegistered() throws Exception
    {
        Usuario pepe = signUpAndUpdateTk(COMU_LA_FUENTE_PEPE);
        userComuAndComuRegistered(new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, pepe).planta("uno").roles(PROPIETARIO.function).build()).test().assertResult(true);
        assertThat(userComuDaoRemote.seeUserComusByUser().size(), is(2));

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_UserAndUserComuRegistered() throws Exception
    {
        checkNoInitCache();
        Comunidad comunidad = signUpWithTkGetComu(COMU_REAL_JUAN);
        cleanWithTkhandler();

        userAndUserComuRegistered(new UsuarioComunidad.UserComuBuilder(comunidad, USER_PEPE).roles(ADMINISTRADOR.function).build()).test().assertComplete();
        checkIsRegistered(viewer);
        checkNoInitCache();

        assertThat(userComuMockDao.deleteUser(USER_PEPE.getUserName()).execute().body(), is(true));
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_UserComuRegistered() throws Exception
    {
        Comunidad comuReal = signUpWithTkGetComu(COMU_REAL_JUAN);
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        UsuarioComunidad userComu = makeUsuarioComunidad(comuReal, null, "portal", "esc", "planta2", "doorJ", PRO.function);
        userComuRegistered(userComu).test().assertResult(1);

        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    @Test
    public void test_IsOldestUser() throws Exception
    {
        isOldestAdmonUser(signUpWithTkGetComu(COMU_REAL_PEPE)).test().assertResult(true);

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_UserComuModified() throws Exception
    {
        UsuarioComunidad newUserComu = new UsuarioComunidad.UserComuBuilder(signUpWithTkGetComu(COMU_ESCORIAL_PEPE), null)
                .userComuRest(COMU_ESCORIAL_PEPE).escalera("new_esc").build();
        userComuModified(newUserComu).test().assertResult(1);

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_UserComuDeleted() throws Exception
    {
        signUpAndUpdateTk(COMU_LA_FUENTE_PEPE);
        controller.userComuDeleted(userComuDaoRemote.seeUserComusByUser().get(0).getComunidad()).test().assertResult(IS_USER_DELETED);
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void test_RegisterComuAndUser() throws Exception
    {
        syncTest(controller -> controller.registerUserAndComu(new ObserverCacheCleaner(viewer), COMU_LA_FUENTE_PEPE));
        assertThat(userComuMockDao.deleteUser(USER_PEPE.getUserName()).execute().body(), is(true));
    }

    @Test
    public void test_RegisterUserComuAndComu() throws Exception
    {
        syncTest(controller -> controller.registerUserComuAndComu(
                new SingleObserverMock<Boolean>() {
                    @Override
                    public void onSuccess(Boolean rowInserted)
                    {
                        assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                        assertThat(rowInserted, is(true));
                    }
                },
                new UsuarioComunidad.UserComuBuilder(
                        COMU_EL_ESCORIAL, signUpAndUpdateTk(COMU_LA_FUENTE_PEPE)
                ).planta("uno").roles(PROPIETARIO.function).build()
        ));
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_RegisterUserAndUserComu() throws Exception
    {
        final Comunidad comunidad = signUpWithTkGetComu(COMU_REAL_JUAN);
        cleanWithTkhandler();

        syncTest(controller -> controller.registerUserAndUserComu(new ObserverCacheCleaner(viewer),
                new UsuarioComunidad.UserComuBuilder(comunidad, USER_PEPE).roles(PRESIDENTE.function).build()));

        assertThat(userComuMockDao.deleteUser(USER_PEPE.getUserName()).execute().body(), is(true));
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_RegisterUserComu() throws Exception
    {
        syncTest(controller -> controller.registerUserComu(
                new SingleObserverMock<Integer>() {
                    @Override
                    public void onSuccess(Integer rowInserted)
                    {
                        assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                        assertThat(rowInserted, is(1));
                    }
                },
                new UsuarioComunidad.UserComuBuilder(
                        signUpWithTkGetComu(COMU_REAL_JUAN), signUpAndUpdateTk(COMU_LA_FUENTE_PEPE)
                ).planta("uno").roles(PROPIETARIO.function).build()
        ));
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));

        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    @Test
    public void test_CheckIsOldestUser() throws Exception
    {
        syncTest(controller -> controller.checkIsOldestAdmonUser(
                new SingleObserverMock<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isOldest)
                    {
                        assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_C), is(BEFORE_METHOD_EXEC));
                        assertThat(isOldest, is(true));
                    }
                },
                signUpWithTkGetComu(COMU_REAL_PEPE)
        ));
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_C));

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_ModifyUserComu() throws Exception
    {
        syncTest(comunidad -> controller.modifyUserComu(
                new SingleObserverMock<Integer>() {
                    @Override
                    public void onSuccess(Integer rowsUpdated)
                    {
                        assertThat(rowsUpdated, is(1));
                        assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                    }
                },
                new UsuarioComunidad.UserComuBuilder(signUpWithTkGetComu(COMU_ESCORIAL_PEPE), null)
                        .userComuRest(COMU_ESCORIAL_PEPE).escalera("new_esc").build()
        ));
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_DeleteUserComu() throws Exception
    {
        syncTest(comunidad -> controller.deleteUserComu(
                new SingleObserverMock<Integer>() {
                    @Override
                    public void onSuccess(Integer rowsUpdated)
                    {
                        assertThat(rowsUpdated, is(IS_USER_DELETED));
                        assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                    }
                },
                signUpWithTkGetComu(COMU_REAL_PEPE)));
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    // ............................ HELPERS ..................................

    private void syncTest(Predicate<CtrlerUsuarioComunidad> isSubscribed) throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            isSubscribed.test(controller);
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }
}