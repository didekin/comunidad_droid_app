package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.firebase.CtrlerFirebaseToken;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenIf;
import com.didekindroid.usuario.firebase.ViewerFirebaseToken;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.testutil.ActivityTestUtils.gcmTokenSentFlag;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 15:24
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidRegAcTest {

    final static AtomicReference<String> flagMethodExec_1 = new AtomicReference<>(BEFORE_METHOD_EXEC);
    final static AtomicReference<String> flagMethodExec_2 = new AtomicReference<>(BEFORE_METHOD_EXEC);

    UsuarioComunidad pepeUserComu;

    @Rule
    public ActivityTestRule<IncidRegAc> activityRule = new ActivityTestRule<IncidRegAc>(IncidRegAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    ViewerIncidRegAc viewer;
    IncidRegAc activity;
    CtrlerFirebaseTokenIf ctrlerFirebaseToken;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        viewer = ViewerIncidRegAc.newViewerIncidRegAc(activity);
        ctrlerFirebaseToken = (CtrlerFirebaseTokenIf) viewer.viewerFirebaseToken.getController();
    }

    @After
    public void clearUp() throws UiException
    {
        viewer.clearSubscriptions();
        activity.viewer.clearSubscriptions();
        cleanOptions(CLEAN_PEPE);
    }

    //  ================================ TESTS ===================================

    @Test
    public void testNewViewerIncidRegAc() throws Exception
    {
        assertThat(viewer.viewerFirebaseToken, notNullValue());
        assertThat(viewer.getController(), notNullValue());
        assertThat(viewer.getController().isRegisteredUser(), is(true));
        assertThat(ctrlerFirebaseToken.isRegisteredUser(), is(true));
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        // Activity onCreate() turns the flag to true.
        waitAtMost(6, SECONDS).until(gcmTokenSentFlag(ctrlerFirebaseToken));
        // Flag initialized to false.
        ctrlerFirebaseToken.updateIsGcmTokenSentServer(false);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.doViewInViewer(null, null);
            }
        });
        // doViewInViewer turns again the flag to true.
        waitAtMost(6, SECONDS).until(gcmTokenSentFlag(ctrlerFirebaseToken));
        onView(withId(R.id.incid_reg_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_ac_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        addSubscription(viewer.getController());
        addSubscription(ctrlerFirebaseToken);

        assertThat(viewer.clearSubscriptions(), is(0));
        assertThat(viewer.getController().getSubscriptions().size(), is(0));
        assertThat(ctrlerFirebaseToken.getSubscriptions().size(), is(0));
    }

    @Test
    public void testRegisterIncidencia_1() throws Exception
    {
        assertThat(viewer.registerIncidencia(doIncidImportancia(), getErrorMsgBuilder(activity)), is(true));
    }

    @Test
    public void testRegisterIncidencia_2() throws Exception
    {
        final StringBuilder errors = getErrorMsgBuilder(activity);
        errors.append(activity.getResources().getString(R.string.incid_reg_importancia)).append(LINE_BREAK.getRegexp());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(viewer.registerIncidencia(null, errors), is(false));
            }
        });
    }

    @Test
    public void testOnSuccessRegisterIncidencia() throws Exception
    {
        viewer.onSuccessRegisterIncidImportancia(2);
        // Check change of activity.
        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_open_by_comu_ac)));
    }

    @Test
    public void testSaveState()
    {
        ViewerIncidRegFr viewerIncidRegFr = new ViewerIncidRegFr(null, activity, viewer) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec_1.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }
        };
        ViewerFirebaseToken viewerFirebaseToken = new ViewerFirebaseToken(activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec_2.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
            }
        };
        viewerFirebaseToken.setController(new CtrlerFirebaseToken());
        viewer.viewerFirebaseToken = viewerFirebaseToken;
        viewer.saveState(new Bundle());
        assertThat(flagMethodExec_1.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        assertThat(flagMethodExec_2.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    // ...................................... HELPERS ..........................................

    @NonNull
    private IncidImportancia doIncidImportancia()
    {
        return new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder()
                        .comunidad(new Comunidad.ComunidadBuilder().c_id(11L).build())
                        .incidenciaId(12L)
                        .descripcion("Descripción válida")
                        .ambitoIncid(new AmbitoIncidencia((short) 4))
                        .build()
        ).build();
    }
}