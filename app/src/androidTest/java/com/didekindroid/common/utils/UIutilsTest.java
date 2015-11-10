package com.didekindroid.common.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.usuario.activity.utils.UsuarioTestUtils;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 31/03/15
 * Time: 15:16
 */
@RunWith(AndroidJUnit4.class)
public class UIutilsTest extends TestCase {

    Context context;

    @Before
    public void setUp(){
        context = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void clean(){
        UsuarioTestUtils.cleanWithTkhandler();
    }

    @Test
    public void testNameFile() throws Exception {
        assertThat(UIutils.SharedPrefFiles.USER_PREF.toString(),
                is("com.didekindroid.common.utils.UIutils.SharedPrefFiles.USER_PREF"));
    }

    @Test
    public void testUpdateIsRegistered() throws Exception {
        updateIsRegistered(false, context);
        assertThat(isRegisteredUser(context), is(false));
        updateIsRegistered(true, context);
        assertThat(isRegisteredUser(context), is(true));
    }
}