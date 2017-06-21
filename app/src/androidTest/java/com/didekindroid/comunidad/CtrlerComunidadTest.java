package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.MaybeObserverMock;
import com.didekindroid.api.SingleObserverMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.annotations.NonNull;

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_FUENTE;
import static com.didekindroid.comunidad.utils.ComuBundleKey.TIPO_VIA_ID;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoMain;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 17:11
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComunidadTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerComunidad controller;
    Comunidad comunidad;

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                comunidad = signUpWithTkGetComu(COMU_ESCORIAL_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    @Before
    public void setUp()
    {
        controller = new CtrlerComunidad();
    }

    @After
    public void clearUp() throws UiException
    {
        controller.clearSubscriptions();
        cleanOptions(CLEAN_PEPE);
    }

    //    =================================== TESTS ===================================

    @Test
    public void test_LoadComunidadData() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putLong(TIPO_VIA_ID.key, 999L);

        try {
            trampolineReplaceIoMain();
            assertThat(controller.loadComunidadData(
                    new SingleObserverMock<Comunidad>() {
                        @Override
                        public void onSuccess(Comunidad comunidadBack)
                        {
                            assertThat(comunidadBack, is(comunidad));
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                        }
                    },
                    comunidad.getC_Id()), is(true));
        } finally {
            resetAllSchedulers();
        }
        checkFinal(1, AFTER_METHOD_EXEC_A);
    }

    @Test
    public void test_ModifyComunidadData() throws Exception
    {
        Comunidad newComunidad = new Comunidad.ComunidadBuilder()
                .copyComunidadNonNullValues(comunidad).nombreVia("nuevo_nombre_via").build();

        try {
            trampolineReplaceIoMain();
            assertThat(controller.modifyComunidadData(new SingleObserverMock<Integer>() {
                @Override
                public void onSuccess(Integer rowModified)
                {
                    assertThat(rowModified, is(1));
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                }
            }, newComunidad), is(true));
        } finally {
            resetAllSchedulers();
        }
        checkFinal(1, AFTER_METHOD_EXEC_A);
    }

    @Test
    public void test_GetUserComu_1() throws Exception
    {
        final UsuarioComunidad userComuBack = new UsuarioComunidad.UserComuBuilder(comunidad, USER_PEPE).userComuRest(COMU_ESCORIAL_PEPE).build();
        try {
            trampolineReplaceIoMain();
            assertThat(controller.getUserComu(new MaybeObserverMock<UsuarioComunidad>() {
                @Override
                public void onSuccess(@NonNull UsuarioComunidad usuarioComunidad)
                {
                    assertThat(usuarioComunidad, is(userComuBack));
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onComplete()
                {
                    fail();
                }
            }, comunidad), is(true));
        } finally {
            resetAllSchedulers();
        }
        checkFinal(1, AFTER_METHOD_EXEC_B);
    }

    @Test
    public void test_LoadComunidadesFound_1() throws Exception
    {
        try {
            trampolineReplaceIoMain();
            assertThat(controller.loadComunidadesFound(new SingleObserverMock<List<Comunidad>>() {
                @Override
                public void onSuccess(@NonNull List<Comunidad> comunidades)
                {
                    assertThat(comunidades.get(0), is(comunidad));
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                }
            }, comunidad), is(true));
        } finally {
            resetAllSchedulers();
        }
        checkFinal(1, AFTER_METHOD_EXEC_B);
    }

    @Test  // Empty list.
    public void test_LoadComunidadesFound_2() throws Exception
    {
        try {
            trampolineReplaceIoMain();
            assertThat(controller.loadComunidadesFound(new SingleObserverMock<List<Comunidad>>() {
                @Override
                public void onSuccess(@NonNull List<Comunidad> comunidades)
                {
                    assertThat(comunidades.size(), is(0));
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                }
            }, COMU_LA_FUENTE), is(true));
        } finally {
            resetAllSchedulers();
        }
        checkFinal(1, AFTER_METHOD_EXEC_B);
    }

    //    =================================== HELPERS ===================================

    private void checkFinal(int size, String flagExec)
    {
        assertThat(controller.getSubscriptions().size(), is(size));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(flagExec));
    }
}