package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyToken;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyTokenIf;
import com.didekindroid.lib_one.usuario.notification.ViewerNotifyToken;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_B;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
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

    final static AtomicReference<String> flagMethodExec_2 = new AtomicReference<>(BEFORE_METHOD_EXEC);

    Comunidad comuReal, comuPlazuela5;


    @Rule
    public ActivityTestRule<IncidRegAc> activityRule = new ActivityTestRule<IncidRegAc>(IncidRegAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regTwoUserComuSameUser(makeListTwoUserComu());
                List<Comunidad> comunidades = userComuDao.getComusByUser();
                comuReal = comunidades.get(0);
                comuPlazuela5 = comunidades.get(1);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    ViewerIncidRegAc viewer;
    IncidRegAc activity;
    CtrlerNotifyTokenIf ctrlerFirebaseToken;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        AtomicReference<ViewerIncidRegAc> atomicViewer = new AtomicReference<>(null);
        atomicViewer.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
        ctrlerFirebaseToken = (CtrlerNotifyTokenIf) viewer.viewerFirebaseToken.getController();
    }

    @After
    public void clearUp() throws UiException
    {
        viewer.clearSubscriptions();
        cleanOptions(CLEAN_JUAN);
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
        // The flag should be turned to true.
        waitAtMost(6, SECONDS).until(ctrlerFirebaseToken.getTkCacher()::isGcmTokenSentServer);
        onView(withId(R.id.incid_reg_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_ac_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        checkSubscriptionsOnStop(activity, viewer.getController(), ctrlerFirebaseToken);
    }

    @Test
    public void testRegisterIncidencia_1() throws Exception
    {
        // Check change of activity.
        viewer.registerIncidencia(doIncidImportancia(), getErrorMsgBuilder(activity));
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(incidSeeByComuAcLayout)));

    }

    @Test
    public void testRegisterIncidencia_2() throws Exception
    {
        final StringBuilder errors = getErrorMsgBuilder(activity);
        errors.append(activity.getResources().getString(R.string.incid_reg_importancia)).append(LINE_BREAK.getRegexp());
        activity.runOnUiThread(() -> assertThat(viewer.registerIncidencia(null, errors), is(false)));
        // Check errors.
        waitAtMost(4, SECONDS).until(isToastInView(R.string.incid_reg_importancia, activity));
    }

    @Test
    public void testOnSuccessRegisterIncidImportancia_1() throws Exception
    {
        viewer.onSuccessRegisterIncidImportancia(comuPlazuela5);
        // Check change of activity.
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(incidSeeByComuAcLayout)));
        // Check comuSpinner initialization.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuPlazuela5.getNombreComunidad()));
    }

    @Test
    public void testOnSuccessRegisterIncidImportancia_2() throws Exception
    {
        // Check spinner initialization with the other comunidad.
        viewer.onSuccessRegisterIncidImportancia(comuReal);
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(comuReal.getNombreComunidad()));
    }

    @Test
    public void testSaveState()
    {
        ViewerNotifyToken viewerNotifyToken = new ViewerNotifyToken(activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec_2.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
            }
        };
        viewerNotifyToken.setController(new CtrlerNotifyToken());
        viewer.viewerFirebaseToken = viewerNotifyToken;
        viewer.saveState(new Bundle());
        assertThat(flagMethodExec_2.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    // ...................................... HELPERS ..........................................

    @NonNull
    private IncidImportancia doIncidImportancia()
    {
        return new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder()
                        .comunidad(new Comunidad.ComunidadBuilder().c_id(comuReal.getC_Id()).build())
                        .descripcion("Descripción válida")
                        .ambitoIncid(new AmbitoIncidencia((short) 4))
                        .build()
        ).build();
    }
}