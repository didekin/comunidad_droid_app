package com.didekindroid.usuario.login;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.usuario.UsuarioBean;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.usuario.login.PasswordMailDialog.newInstance;
import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/01/2018
 * Time: 12:43
 */
@RunWith(AndroidJUnit4.class)
public class PasswordMailDialogTest {

    @Test
    public void test_NewInstance() throws Exception
    {
        try {
            newInstance(null);
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is(bean_fromView_should_be_initialized));
        }

        UsuarioBean usuarioBean = new UsuarioBean("email@mail.es", "alias", "password", "password");
        usuarioBean.validateLoginData(getTargetContext().getResources(), new StringBuilder(0));
        PasswordMailDialog dialog = newInstance(usuarioBean);
        assertThat(dialog.getArguments().getSerializable(usuario_object.key), notNullValue());
    }
}