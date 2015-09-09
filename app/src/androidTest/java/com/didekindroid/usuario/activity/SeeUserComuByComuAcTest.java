package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.DomainDataUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.UsuarioTestUtils.*;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_PLAZUELA_5;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/08/15
 * Time: 11:38
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByComuAcTest {

    SeeUserComuByComuAc mActivity;
    Intent intent;

    @Rule
    public ActivityTestRule<SeeUserComuByComuAc> mActivityRule =
            new ActivityTestRule<>(SeeUserComuByComuAc.class, true, false);

    @Before
    public void setUp() throws Exception
    {
        // User is registered, with a comunidad in the intent.
        signUpAndUpdateTk(DomainDataUtils.COMU_PLAZUELA5_JUAN);  // Comunidad2.
        // We insert a secondo user with the same comunidad.
        signUpAndUpdateTk(DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE);  // Comunidad3.
        // We put the comunidad in the intent.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, COMU_LA_PLAZUELA_5);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        mActivity = mActivityRule.launchActivity(intent);
        assertThat(mActivity,notNullValue());
        assertThat(isRegisteredUser(mActivity),is(true));
        assertThat((Comunidad) mActivity.getIntent().getSerializableExtra(COMUNIDAD_LIST_OBJECT.extra),
                is(COMU_LA_PLAZUELA_5));
        assertThat(mActivity.getFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg),notNullValue());

        onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewData_1(){


    }

    @After
    public void tearDown() throws Exception
    {
        cleanTwoUsers(DomainDataUtils.COMU_PLAZUELA5_JUAN.getUsuario(), DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE.getUsuario());
    }
}