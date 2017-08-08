package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.exception.UiException;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkRegComuFrViewEmpty;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSpinnersDoInViewerOffNull;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuListFrLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 15/05/15
 * Time: 09:53
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAcTest {

    @Rule
    public ActivityTestRule<ComuSearchAc> activityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    private ComuSearchAc activity;

    @Test
    public void test_OnCreate()
    {
        activity = activityRule.launchActivity(new Intent());

        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, notNullValue());
        assertThat(activity.regComuFrg, notNullValue());

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        // Es la actividad inicial de la aplicación.
        onView(allOf(
                withContentDescription(R.string.navigate_up_txt),
                isClickable())).check(doesNotExist());
        checkRegComuFrViewEmpty();
        assertThat(activity.regComuFrg.viewerInjector, CoreMatchers.<ViewerParentInjectorIf>is(activity));
        assertThat(activity.regComuFrg.viewer.getParentViewer(), CoreMatchers.<ViewerIf>is(activity.viewer));

        // Check spinners in fragment.viewer, after calling doInViewer.
        checkSpinnersDoInViewerOffNull(activity.regComuFrg.viewer);
    }

    @Test
    public void testWithResultsAndUp() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        typeComunidadData();

        onView(withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());
        // Check the view for comunidades list fragment.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(comuListFrLayout));

        checkUp(comuSearchAcLayout);

        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testWithResultsAndBack() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        typeComunidadData();

        onView(withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());
        // Check the view for comunidades list fragment.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(comuListFrLayout));
        // Back.
        checkBack(onView(withId(comuListFrLayout)), comuSearchAcLayout);

        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testComunidadesByUsuario() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        SEE_USERCOMU_BY_USER_AC.checkItemRegisterUser(activity);

        checkUp(comuSearchAcLayout);
        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testLogin_Registered() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());

        LOGIN_AC.checkItemRegisterUser(activity);
        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testLogin_Unregistered() throws InterruptedException
    {
        activity = activityRule.launchActivity(new Intent());
        assertThat(activity.viewer.getController().isRegisteredUser(), is(false));

        LOGIN_AC.checkItemNoRegisterUser(activity);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testGetDataUser_NotRegistered() throws InterruptedException
    {
        activity = activityRule.launchActivity(new Intent());
        assertThat(activity.viewer.getController().isRegisteredUser(), is(false));
        USER_DATA_AC.checkItemNoRegisterUser(activity);
    }

    @Test
    public void testGetDataUser_Registered() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        USER_DATA_AC.checkItemRegisterUser(activity);

        checkUp(comuSearchAcLayout);
        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testMenuNuevaComunidad_NotRegistered() throws InterruptedException
    {
        activity = activityRule.launchActivity(new Intent());
        assertThat(activity.viewer.getController().isRegisteredUser(), is(false));
        REG_COMU_USER_USERCOMU_AC.checkItemNoRegisterUser(activity);

        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testMenuNuevaComunidad_Registered() throws InterruptedException, UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = activityRule.launchActivity(new Intent());
        REG_COMU_USERCOMU_AC.checkItemRegisterUser(activity);

        checkUp(comuSearchAcLayout);
        cleanOneUser(USER_JUAN);
    }
}