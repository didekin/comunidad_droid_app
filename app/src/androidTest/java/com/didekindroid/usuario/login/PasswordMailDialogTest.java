package com.didekindroid.usuario.login;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.usuario.UsuarioBean;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.usuario.UsuarioBundleKey.usuario_object;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

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
        PasswordMailDialog dialog = PasswordMailDialog.newInstance(null);
        assertThat(dialog.getArguments(), notNullValue());
        assertThat(dialog.getArguments().getSerializable(usuario_object.key), nullValue());

        UsuarioBean usuarioBean = new UsuarioBean("email@mail.es","alias", "password", "password");
        usuarioBean.validateLoginData(getTargetContext().getResources(), new StringBuilder(0));
        dialog = PasswordMailDialog.newInstance(usuarioBean);
        assertThat(dialog.getArguments().getSerializable(usuario_object.key), notNullValue());
    }
}