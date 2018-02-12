package com.didekindroid.router;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.exception.UiExceptionRouterIf;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.router.UiExceptionAction.generic;
import static com.didekindroid.router.UiExceptionAction.show_comunidad_duplicate;
import static com.didekindroid.router.UiExceptionAction.show_comunidad_search;
import static com.didekindroid.router.UiExceptionAction.show_incidReg;
import static com.didekindroid.router.UiExceptionAction.show_incid_open_list;
import static com.didekindroid.router.UiExceptionAction.show_login_noPowers;
import static com.didekindroid.router.UiExceptionAction.show_login_noUser;
import static com.didekindroid.router.UiExceptionAction.show_login_tokenNull;
import static com.didekindroid.router.UiExceptionAction.show_resolucionDup;
import static com.didekindroid.router.UiExceptionAction.show_userData_wrongMail;
import static com.didekindroid.router.UiExceptionRouter.uiException_router;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.loginAcResourceId;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.userDataAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.TOKEN_NULL;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USERCOMU_WRONG_INIT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class UiExceptionActionTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    ActivityMock activity;
    UiExceptionRouterIf router = uiException_router;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    //  ===========================================================================

    @Test
    public void test_generic() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        runAndCheck(ue, generic, comuSearchAcLayout, FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Test
    public void test_show_comunidad_duplicate() throws Exception
    {
        // Precondition to avoid exception in next Activity.
        TKhandler.updateIsRegistered(false);
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_DUPLICATE));
        runAndCheck(ue, show_comunidad_duplicate, comuSearchAcLayout);
    }

    @Test
    public void test_show_comunidad_search() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_NOT_FOUND));
        runAndCheck(ue, show_comunidad_search, comuSearchAcLayout);
    }

    @Test
    public void test_show_incidReg() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_REGISTERED));
        runAndCheck(ue, show_incidReg, incidRegAcLayout);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_incid_open_list() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_FOUND));
        runAndCheck(ue, show_incid_open_list, incidSeeGenericFrLayout);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_login_noPowers() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_USER_WRONG_INIT));
        runAndCheck(ue, show_login_noPowers, loginAcResourceId);
    }

    @Test
    public void test_show_login_noUser() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(USERCOMU_WRONG_INIT));
        runAndCheck(ue, show_login_noUser, loginAcResourceId);
    }

    @Test
    public void test_show_login_tokenNull() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(TOKEN_NULL));
        runAndCheck(ue, show_login_tokenNull, loginAcResourceId);
    }

    @Test
    public void test_show_resolucionDup() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final Intent intentIn = new Intent(activity, IncidEditAc.class);
        IncidAndResolBundle resolBundle = new IncidAndResolBundle(
                makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0),
                        (short) 3),
                false);
        intentIn.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);

        final UiException ue = new UiException(new ErrorBean(RESOLUCION_DUPLICATE));
        runAndCheck(ue, show_resolucionDup, incidSeeGenericFrLayout);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_userData_wrongMail() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(PASSWORD_NOT_SENT));
        runAndCheck(ue, show_userData_wrongMail, userDataAcRsId);

        cleanOptions(CLEAN_JUAN);
    }

    // ============================  Helpers ==============================

    private void runAndCheck(UiException ue, UiExceptionAction uiExceptionAction, int checkLayout)
    {
        activity.runOnUiThread(() -> router.getActionFromMsg(ue.getErrorHtppMsg()).initActivity(activity));
        waitAtMost(5, SECONDS).until(isToastInView(uiExceptionAction.getResourceIdForToast(), activity));
        onView(withId(checkLayout)).check(matches(isDisplayed()));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK));
    }

    private void runAndCheck(UiException ue, UiExceptionAction uiExceptionAction, int checkLayout, int flags)
    {
        runAndCheck(ue, uiExceptionAction, checkLayout);
        intended(hasFlag(flags));
    }
}