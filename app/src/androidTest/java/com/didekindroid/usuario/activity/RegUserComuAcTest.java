package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import com.didekindroid.usuario.common.UserIntentExtras;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.Usuario;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.common.DataUsuarioTestUtils.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.notNullValue;
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
        assertThat(activity,notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_fr),notNullValue());
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_fr)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception
    {

    }


}