package com.didekindroid.incidencia.core.reg;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static io.reactivex.android.plugins.RxAndroidPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 15:24
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidRegAcTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerIncidRegAc controller;
    IncidRegAc activity;
    Usuario pepe;
    UsuarioComunidad pepeUserComu;

    @Rule
    public ActivityTestRule<IncidRegAc> activityRule = new ActivityTestRule<IncidRegAc>(IncidRegAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        controller = new CtrlerIncidRegAc(new ViewerIncidRegAc(activity) {
            @Override
            void onSuccessRegisterIncidencia(int rowInserted)
            {
                assertThat(rowInserted, is(2));
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }
        });
        assertThat(controller.getSubscriptions().size(), is(0));
    }

    @After
    public void clearUp() throws UiException
    {
        controller.clearSubscriptions();
        reset();
        cleanOptions(CLEAN_PEPE);
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testRegisterIncidencia() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.registerIncidencia(doIncidImportancia()), is(true));
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    //    .................................... HELPER METHODS .................................

    @NonNull
    private IncidImportancia doIncidImportancia()
    {
        return new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43)
        )
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 3)
                .build();
    }
}