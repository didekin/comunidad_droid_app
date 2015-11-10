package com.didekindroid.usuario.activity.utils;

import org.junit.Test;

import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USERCOMU_LIST_OBJECT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/08/15
 * Time: 12:25
 */
public class IntentExtrasTests {

    @Test
    public void testGetExtra()
    {
        assertThat(COMUNIDAD_LIST_OBJECT.extra, is(UserIntentExtras.class.getName()
                .concat(".")
                .concat(COMUNIDAD_LIST_OBJECT.name())));
        assertThat(COMUNIDAD_LIST_INDEX.extra, is(UserIntentExtras.class.getName()
                .concat(".")
                .concat(COMUNIDAD_LIST_INDEX.name())));
        assertThat(COMUNIDAD_SEARCH.extra, is(UserIntentExtras.class.getName()
                .concat(".")
                .concat(COMUNIDAD_SEARCH.name())));
        assertThat(COMUNIDAD_ID.extra, is(UserIntentExtras.class.getName()
                .concat(".")
                .concat(COMUNIDAD_ID.name())));
        assertThat(USERCOMU_LIST_OBJECT.extra, is(UserIntentExtras.class.getName()
                .concat(".")
                .concat(USERCOMU_LIST_OBJECT.name())));
    }
}