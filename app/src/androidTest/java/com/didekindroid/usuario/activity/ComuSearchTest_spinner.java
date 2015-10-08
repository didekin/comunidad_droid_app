package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/10/15
 * Time: 16:28
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchTest_spinner {

    private ComuSearchAc activity;
    private RegComuFr regComuFr;

    @Rule
    public ActivityTestRule<ComuSearchAc> mActivityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    @Test
    public void testPreconditions()
    {
        activity = mActivityRule.launchActivity(new Intent());
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        assertThat(activity, notNullValue());
        assertThat(regComuFr, notNullValue());
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.tipo_via_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.autonoma_comunidad_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.provincia_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.municipio_spinner)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is("municipio")))).check(matches(isDisplayed()));
    }

    @Test
    public void testNoComunidadSelected_1()
    {
        activity = mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "comunidad autónoma")).perform(click());

        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is("municipio")))).check(matches(isDisplayed()));
    }

    @Test
    public void testNoComunidadSelected_2()
    {
        activity = mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "Valencia")).perform(click());

        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("Valencia")))).check(matches(isDisplayed()));

        onView(withId(R.id.provincia_spinner)).perform(click());
        onData(withRowString(1, "Valencia/València")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("Valencia/València")))).check(matches(isDisplayed()));

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "comunidad autónoma")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is("municipio")))).check(matches(isDisplayed()));
    }
}