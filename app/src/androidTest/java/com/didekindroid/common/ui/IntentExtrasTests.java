package com.didekindroid.common.ui;

import org.junit.Test;

import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_BEAN_LIST;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_INDEX_LIST;
import static com.didekindroid.usuario.common.UserIntentExtras.IS_COMUNIDADES_BY_USER;
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
        assertThat(COMUNIDAD_BEAN_LIST.extra,is("com.didekindroid.common.ui.COMUNIDAD_BEAN_LIST"));
        assertThat(COMUNIDAD_INDEX_LIST.extra,is("com.didekindroid.common.ui.COMUNIDAD_INDEX_LIST"));
        assertThat(IS_COMUNIDADES_BY_USER.extra,is("com.didekindroid.common.ui.IS_COMUNIDADES_BY_USER"));
    }

}