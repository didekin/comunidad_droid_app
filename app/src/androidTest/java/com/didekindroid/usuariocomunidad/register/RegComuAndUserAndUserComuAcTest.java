package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.focusOnButton;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserDataFull;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN2;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_User_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 07/07/15
 * Time: 10:26
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserAndUserComuAcTest {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, false);

    RegComuAndUserAndUserComuAc activity;
    Resources resources;
    int buttonId;
    CleanUserEnum whatToClean = CLEAN_NOTHING;

    @Before
    public void setUp() throws Exception
    {
        cleanOptions(CLEAN_TK_HANDLER);
        resources = getTargetContext().getResources();
        activity = mActivityRule.launchActivity(new Intent());
        buttonId = R.id.reg_com_usuario_usuariocomu_button;
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    //    ================================================================================

    @Test
    public void testRegComuAndUserComuAndUser_NotOk() throws InterruptedException
    {
        typeComunidad();
        Thread.sleep(1000);
        typeUserComuData("WRONG**", "escale_b", "planta-N", "puerta5", PRE, INQ);
        typeUserDataFull(USER_JUAN2.getUserName(), USER_JUAN2.getAlias(), USER_JUAN2.getPassword(), USER_JUAN2.getPassword());
        onView(withId(buttonId)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.reg_usercomu_portal_rot));
    }

    @Test
    public void testRegComuAndUserComuAndUser_OK() throws UiException, InterruptedException
    {
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        focusOnButton(activity, buttonId);
        typeComunidad();
        typeUserDataFull(USER_JUAN2.getUserName(), USER_JUAN2.getAlias(), USER_JUAN2.getPassword(), USER_JUAN2.getPassword());
        onView(withId(buttonId)).perform(scrollTo(), click());

        waitAtMost(5, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        waitAtMost(4, SECONDS).untilAtomic(TKhandler.getTokenCache(), notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));

        checkUp(regComu_User_UserComuAcLayout);
        whatToClean = CLEAN_JUAN2;
    }


    //    =================================== Life cycle ===================================

    @Test
    public void test_OnCreate() throws Exception
    {
        assertThat(activity, notNullValue());
        assertThat(activity.regComuFr, notNullValue());
        assertThat(activity.regUserComuFr, notNullValue());
        assertThat(activity.regUserFr, notNullValue());
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, isA(ViewerRegComuUserUserComuAc.class));

        onView(withId(regComu_User_UserComuAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void test_OnStop() throws Exception
    {
        checkSubscriptionsOnStop(activity.viewer.getController(), activity);
    }

    @Test
    public void test_GetViewerAsParent() throws Exception
    {
        assertThat(activity.getViewerAsParent(), CoreMatchers.<ViewerParentInjectedIf>is(activity.viewer));
        assertThat(activity.viewer, isA(ViewerParentInjectedIf.class));
    }

    @Test
    public void test_SetChildInViewer()
    {
        final ViewerMock viewerChild = new ViewerMock(activity);
        activity.setChildInViewer(viewerChild);
        assertThat(activity.viewer.getChildViewer(ViewerMock.class), is(viewerChild));
    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_NoToken() throws InterruptedException, UiException
    {
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.getTokenCache().get(), nullValue());

        LOGIN_AC.checkMenuItem_NTk(activity);
        checkUp(regComu_User_UserComuAcLayout);
    }

    //    =================================== HELPERS ===================================

    public void typeComunidad()
    {
        typeComunidadData(new ComunidadAutonoma((short) 10, "Valencia"),
                new Provincia((short) 12, "Castellón/Castelló"),
                new Municipio((short) 53, "Chilches/Xilxes", new Provincia((short) 12, "Castellón/Castelló")),
                new TipoViaValueObj(54, "Callejon"),
                "nombre via One", "123", "Tris");
    }
}