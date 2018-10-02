package com.didekindroid.incidencia.activity;

import com.didekindroid.usuariocomunidad.UserComuBundleKey;

import org.junit.Test;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.comunidad.util.ComuBundleKey.intentPackage;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/08/15
 * Time: 12:25
 */
public class IncidBundleKeyTest {

    @Test
    public void testGetExtra()
    {
        assertThat(COMUNIDAD_LIST_OBJECT.key, is(intentPackage
                .concat(COMUNIDAD_LIST_OBJECT.name())));
        assertThat(COMUNIDAD_LIST_INDEX.key, is(intentPackage
                .concat(COMUNIDAD_LIST_INDEX.name())));
        assertThat(COMUNIDAD_SEARCH.key, is(intentPackage
                .concat(COMUNIDAD_SEARCH.name())));
        assertThat(COMUNIDAD_ID.key, is(intentPackage
                .concat(COMUNIDAD_ID.name())));
        assertThat(USERCOMU_LIST_OBJECT.key, is(UserComuBundleKey.intentPackage
                .concat(USERCOMU_LIST_OBJECT.name())));
    }
}