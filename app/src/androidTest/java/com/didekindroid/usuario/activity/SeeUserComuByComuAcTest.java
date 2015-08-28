package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.Comunidad;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.ui.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.common.DataUsuarioTestUtils.*;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
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
        signUpAndUpdateTk(USUARIO_COMUNIDAD_2);
        // We insert a secondo user with the same comunidad.
        signUpAndUpdateTk(USUARIO_COMUNIDAD_3);
        // We put the comunidad in the intent.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_LIST_OBJECT.extra,COMUNIDAD_2);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        mActivity = mActivityRule.launchActivity(intent);
        assertThat(mActivity,notNullValue());
        assertThat(isRegisteredUser(mActivity),is(true));
        assertThat((Comunidad) mActivity.getIntent().getSerializableExtra(COMUNIDAD_LIST_OBJECT.extra),is(COMUNIDAD_2));
        assertThat(mActivity.getFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg),notNullValue());

        onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanTwoUsers(USUARIO_COMUNIDAD_2.getUsuario(),USUARIO_COMUNIDAD_3.getUsuario());
    }
}