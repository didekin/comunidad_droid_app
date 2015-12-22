package com.didekindroid.common.utils;

import org.junit.Test;

import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_ID;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_SEARCH;
import static com.didekindroid.common.utils.AppKeysForBundle.USERCOMU_LIST_OBJECT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/08/15
 * Time: 12:25
 */
public class AppKeysForBundleTest {

    @Test
    public void testGetExtra()
    {
        assertThat(COMUNIDAD_LIST_OBJECT.extra, is(AppKeysForBundle.class.getName()
                .concat(".")
                .concat(COMUNIDAD_LIST_OBJECT.name())));
        assertThat(COMUNIDAD_LIST_INDEX.extra, is(AppKeysForBundle.class.getName()
                .concat(".")
                .concat(COMUNIDAD_LIST_INDEX.name())));
        assertThat(COMUNIDAD_SEARCH.extra, is(AppKeysForBundle.class.getName()
                .concat(".")
                .concat(COMUNIDAD_SEARCH.name())));
        assertThat(COMUNIDAD_ID.extra, is(AppKeysForBundle.class.getName()
                .concat(".")
                .concat(COMUNIDAD_ID.name())));
        assertThat(USERCOMU_LIST_OBJECT.extra, is(AppKeysForBundle.class.getName()
                .concat(".")
                .concat(USERCOMU_LIST_OBJECT.name())));
    }
}