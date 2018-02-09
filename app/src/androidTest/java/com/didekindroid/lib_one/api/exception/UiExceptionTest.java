package com.didekindroid.lib_one.api.exception;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.router.UiExceptionRouter.generic;
import static com.didekindroid.router.UiExceptionRouter.getExceptionRouter;
import static com.didekindroid.router.UiExceptionRouter.show_comunidad_duplicate;
import static com.didekindroid.router.UiExceptionRouter.show_comunidad_search;
import static com.didekindroid.router.UiExceptionRouter.show_incidReg;
import static com.didekindroid.router.UiExceptionRouter.show_incid_open_list;
import static com.didekindroid.router.UiExceptionRouter.show_login_noPowers;
import static com.didekindroid.router.UiExceptionRouter.show_login_noUser;
import static com.didekindroid.router.UiExceptionRouter.show_login_tokenNull;
import static com.didekindroid.router.UiExceptionRouter.show_resolucionDup;
import static com.didekindroid.router.UiExceptionRouter.show_userData_wrongMail;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class UiExceptionTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    ActivityMock activity;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    //  ===========================================================================

    @Test
    public void testSetUp()
    {
        assertThat(activity, notNullValue());
    }

    @Test
    public void test_generic() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));

        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));

        waitAtMost(5, SECONDS).until(isToastInView(generic.getResourceIdForToast(), activity));
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_show_comunidad_duplicate() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_DUPLICATE));

        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));
        waitAtMost(5, SECONDS).until(isToastInView(show_comunidad_duplicate.getResourceIdForToast(), activity));
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_show_comunidad_search() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_NOT_FOUND));

        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));
        waitAtMost(5, SECONDS).until(isToastInView(show_comunidad_search.getResourceIdForToast(), activity));
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_show_incidReg() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_REGISTERED));
        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));

        waitAtMost(5, SECONDS).until(isToastInView(show_incidReg.getResourceIdForToast(), activity));
        onView(withId(incidRegAcLayout)).check(matches(isDisplayed()));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_incid_open_list() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_FOUND));
        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));

        waitAtMost(5, SECONDS).until(isToastInView(show_incid_open_list.getResourceIdForToast(), activity));
        onView(withId(incidSeeGenericFrLayout)).check(matches(isDisplayed())); // Lista de incidencias abiertas.

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_login_noPowers() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_USER_WRONG_INIT));

        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));

        waitAtMost(5, SECONDS).until(isToastInView(show_login_noPowers.getResourceIdForToast(), activity));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }

    @Test
    public void test_show_login_noUser() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(USERCOMU_WRONG_INIT));

        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));

        waitAtMost(5, SECONDS).until(isToastInView(show_login_noUser.getResourceIdForToast(), activity));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }

    @Test
    public void test_show_login_tokenNull() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(TOKEN_NULL));

        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed((loginAcResourceId)));
        waitAtMost(4, SECONDS).until(isToastInView(show_login_tokenNull.getResourceIdForToast(), activity));
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

        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));
        waitAtMost(5, SECONDS).until(isToastInView(show_resolucionDup.getResourceIdForToast(), activity));
        onView(withId(incidEditAcLayout)).check(matches(isDisplayed()));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_show_userData_wrongMail() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        final UiException ue = new UiException(new ErrorBean(PASSWORD_NOT_SENT));
        activity.runOnUiThread(() -> getExceptionRouter(ue.getErrorHtppMsg()).initActivity(activity));

        waitAtMost(5, SECONDS).until(isToastInView(show_userData_wrongMail.getResourceIdForToast(), activity));
        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));

        cleanOptions(CLEAN_JUAN);
    }
}