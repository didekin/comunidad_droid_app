package com.didekindroid.router;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekindroid.lib_one.util.BundleKey;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.Serializable;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
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
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
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
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class UiExceptionActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    private ActivityMock activity;
    private UiExceptionRouterIf router = uiException_router;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

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
        run(ue, generic, comuSearchAcLayout);
        intended(hasFlag(FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Test
    public void test_show_comunidad_duplicate() throws Exception
    {
        // Precondition to avoid exception in next Activity.
        secInitializer.get().getTkCacher().updateIsRegistered(false);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isRegisteredUser, is(false));
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_DUPLICATE));
        run(ue, show_comunidad_duplicate, comuSearchAcLayout);
    }

    @Test
    public void test_show_comunidad_search() throws Exception       // TODO: fail
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_NOT_FOUND));
        run(ue, show_comunidad_search, comuSearchAcLayout);
    }

    @Test
    public void test_show_incidReg() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_REGISTERED));
        run(ue, show_incidReg, incidRegAcLayout);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_incid_open_list_1() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_FOUND));
        run(ue, show_incid_open_list, incidSeeGenericFrLayout);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_incid_open_list_2() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_FOUND));
        run(ue, show_incid_open_list, incidSeeGenericFrLayout, null, null);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_incid_open_list_3() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_FOUND));
        run(ue, show_incid_open_list, incidSeeGenericFrLayout, () -> "key_test", "key_value");

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_login_noPowers() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_USER_WRONG_INIT));
        run(ue, show_login_noPowers, loginAcResourceId);
    }

    @Test
    public void test_show_login_noUser() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(USERCOMU_WRONG_INIT));
        run(ue, show_login_noUser, loginAcResourceId);
    }

    @Test
    public void test_show_login_tokenNull() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(TOKEN_NULL));
        run(ue, show_login_tokenNull, loginAcResourceId);
    }

    @Test
    public void test_show_resolucionDup_1() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(RESOLUCION_DUPLICATE));
        // Run.
        run(ue, show_resolucionDup, incidSeeGenericFrLayout);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_resolucionDup_2() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(RESOLUCION_DUPLICATE));
        // Run.
        run(ue, show_resolucionDup, incidSeeGenericFrLayout, null, null);

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_resolucionDup_3() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(RESOLUCION_DUPLICATE));
        // Run.
        run(ue, show_resolucionDup, incidSeeGenericFrLayout, () -> "key_test", "key_value");

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_userData_wrongMail() throws Exception  // TODO: fail
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(PASSWORD_NOT_SENT));
        // Run.
        run(ue, show_userData_wrongMail, userDataAcRsId);

        cleanOptions(CLEAN_JUAN);
    }

    // ============================  Helpers ==============================

    private void run(UiException ue, UiExceptionAction uiExceptionAction, int checkLayout)
    {
        activity.runOnUiThread(() -> router.getActionFromMsg(ue.getErrorHtppMsg()).initActivity(activity));
        check(uiExceptionAction, checkLayout);
    }

    private void run(UiException ue, UiExceptionAction uiExceptionAction, int checkLayout, BundleKey bundleKey, Serializable bundleObject)
    {
        Bundle bundle = (bundleKey != null && bundleObject != null)  ? bundleKey.getBundleForKey(bundleObject) : null;
        activity.runOnUiThread(() -> router.getActionFromMsg(ue.getErrorHtppMsg()).initActivity(activity, bundle));
        check(uiExceptionAction, checkLayout);
        if (bundle != null){
            intended(hasExtras(hasEntry(bundleKey.getKey(), bundleObject)));
        }
    }

    private void check(UiExceptionAction uiExceptionAction, int checkLayout)
    {
        waitAtMost(10, SECONDS).until(isToastInView(uiExceptionAction.getResourceIdForToast(), activity));
        waitAtMost(8, SECONDS).until(isResourceIdDisplayed(checkLayout));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK));
    }
}