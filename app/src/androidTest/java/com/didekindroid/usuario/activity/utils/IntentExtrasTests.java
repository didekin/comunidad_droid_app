package com.didekindroid.usuario.activity.utils;

import org.junit.Test;

import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.IS_COMUNIDADES_BY_USER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * User: pedro@didekin
 * Date: 07/08/15
 * Time: 12:25
 */
public class IntentExtrasTests {

    @Test
    public void testGetExtra(){
        assertThat(COMUNIDAD_LIST_OBJECT.extra,is("com.didekindroid.ui.COMUNIDAD_LIST_OBJECT"));
        assertThat(COMUNIDAD_LIST_INDEX.extra,is("com.didekindroid.ui.COMUNIDAD_LIST_INDEX"));
        assertThat(IS_COMUNIDADES_BY_USER.extra,is("com.didekindroid.ui.IS_COMUNIDADES_BY_USER"));
    }

}