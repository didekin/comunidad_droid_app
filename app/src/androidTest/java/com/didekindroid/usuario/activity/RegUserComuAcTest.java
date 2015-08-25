package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import com.didekindroid.common.ui.UIutils;
import com.didekindroid.usuario.common.UserIntentExtras;
import com.didekindroid.usuario.dominio.Comunidad;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.common.DataUsuarioTestUtils.*;
import static com.didekindroid.usuario.common.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 17:23
 */
@RunWith(AndroidJUnit4.class)
public class RegUserComuAcTest {

    private RegUserComuAc activity;
    private Intent intent;

    @Rule
    public ActivityTestRule<RegUserComuAc> mActivityRule =
            new ActivityTestRule<>(RegUserComuAc.class, true, false);

    @Before
    public void setUp() throws Exception
    {
        // User and comunidad are already registered.
        // Comunidad was registered by another user.
        signUpAndUpdateTk(USUARIO_COMUNIDAD_1);
        List<Comunidad> comunidadesUserOne = ServOne.getComunidadesByUser();
        Comunidad comunidad = comunidadesUserOne.get(0);

        // We use that comunidad as the one to associate to the present user.
        intent = new Intent();
        intent.putExtra(UserIntentExtras.COMUNIDAD_LIST_OBJECT.extra, comunidad);
        // Segundo usuarioComunidad, con diferente usuario y comunidad.
        signUpAndUpdateTk(USUARIO_COMUNIDAD_3);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);

        assertThat(UIutils.isRegisteredUser(activity), is(true));
        List<Comunidad> comunidadesUserOne = ServOne.getComunidadesByUser();
        assertThat(comunidadesUserOne.size(), is(1));
        Comunidad comunidad = comunidadesUserOne.get(0);
        assertThat(comunidad, not(is(USUARIO_COMUNIDAD_1.getComunidad())));
        assertThat(comunidad, is(USUARIO_COMUNIDAD_3.getComunidad()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_fr), notNullValue());
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_fr)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnclick_1()
    {
        // Validation errors.
        activity = mActivityRule.launchActivity(intent);

        typeRegUserComuData("portal?", "select *", "planta!", "puerta_1", null);
        onView(withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        makeErrorValidationToast(activity, R.string.reg_usercomu_portal_hint, R.string.reg_usercomu_escalera_hint, R
                .string.reg_usercomu_planta_hint, R.string.reg_usercomu_role_rot);
    }

    @Test
    public void testOnclick_2()
    {

    }

    @After
    public void tearDown() throws Exception
    {
        // User2 cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));

        // User1 cleanup. We update user credentiasl first.
        updateSecurityData(USUARIO_1.getUserName(), USUARIO_1.getPassword());
        isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));

        if (TKhandler.getRefreshTokenFile().exists()) {
            TKhandler.getRefreshTokenFile().delete();
        }
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
    }
}