package com.didekindroid.router;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlags;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.router.DidekinUiExceptionAction.generic;
import static com.didekindroid.router.DidekinUiExceptionAction.show_comunidad_duplicate;
import static com.didekindroid.router.DidekinUiExceptionAction.show_comunidad_search;
import static com.didekindroid.router.DidekinUiExceptionAction.show_incidReg;
import static com.didekindroid.router.DidekinUiExceptionAction.show_incid_open_list;
import static com.didekindroid.router.DidekinUiExceptionAction.show_login_noPowers;
import static com.didekindroid.router.DidekinUiExceptionAction.show_resolucionDup;
import static com.didekindroid.router.testutil.UserRouterMapUtil.checkUserExcepMsgMap;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.http.exception.GenericExceptionMsg.NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCID_IMPORTANCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@RunWith(AndroidJUnit4.class)
public class DidekinUiExceptionActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    private ActivityMock activity;
    private UiExceptionRouterIf router;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        router = routerInitializer.get().getExceptionRouter();
    }

    //  ===========================================================================

    @Test
    public void test_InitStaticMap()
    {
        checkUserExcepMsgMap();
    }

    @Test
    public void test_defaultAc()
    {
        assertThat(routerInitializer.get().getDefaultAc().equals(ComuSearchAc.class), is(true));
    }

    @Test
    public void test_GENERIC_INTERNAL_ERROR()
    {
        final UiException ue = new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        run(ue, generic, comuSearchAcLayout);
        intended(hasFlags(FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Test
    public void test_NOT_FOUND()
    {
        final UiException ue = new UiException(new ErrorBean(NOT_FOUND));
        run(ue, generic, comuSearchAcLayout);
        intended(hasFlags(FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Test
    public void test_COMUNIDAD_DUPLICATE()
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_DUPLICATE));
        run(ue, show_comunidad_duplicate, comuSearchAcLayout);
    }

    @Test
    public void test_COMUNIDAD_NOT_FOUND()
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_NOT_FOUND));
        run(ue, show_comunidad_search, comuSearchAcLayout);
    }

    @Test
    public void test_INCIDENCIA_NOT_REGISTERED() throws Exception
    {
        // Preconditions.
        regComuUserUserComuGetAuthTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_REGISTERED));
        run(ue, show_incidReg, incidRegAcLayout);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_INCIDENCIA_NOT_FOUND() throws Exception
    {
        // Preconditions.
        regComuUserUserComuGetAuthTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_FOUND));
        run(ue, show_incid_open_list, incidSeeGenericFrLayout);
        intended(hasExtra(INCID_CLOSED_LIST_FLAG.key, false));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_INCID_IMPORTANCIA_NOT_FOUND() throws Exception
    {
        // Preconditions.
        regComuUserUserComuGetAuthTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCID_IMPORTANCIA_NOT_FOUND));
        run(ue, show_incid_open_list, incidSeeGenericFrLayout);
        intended(hasExtra(INCID_CLOSED_LIST_FLAG.key, false));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_INCIDENCIA_USER_WRONG_INIT()
    {
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_USER_WRONG_INIT));
        run(ue, show_login_noPowers, loginAcResourceId);
    }

    @Test
    public void test_RESOLUCION_DUPLICATE() throws Exception
    {
        // Preconditions.
        regComuUserUserComuGetAuthTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(RESOLUCION_DUPLICATE));
        // Run.
        run(ue, show_resolucionDup, incidSeeGenericFrLayout);
        intended(hasExtra(INCID_CLOSED_LIST_FLAG.key, false));

        cleanOptions(CLEAN_JUAN);
    }

    // ============================  Helpers ==============================

    private void run(UiException ue, DidekinUiExceptionAction uiExceptionAction, int checkLayout)
    {
        activity.runOnUiThread(() -> router.getActionFromMsg(ue.getErrorHtppMsg()).handleExceptionInUi(activity));
        waitAtMost(8, SECONDS).until(isToastInView(uiExceptionAction.getResourceIdForToast(), activity));
        waitAtMost(8, SECONDS).until(isResourceIdDisplayed(checkLayout));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK));
    }
}