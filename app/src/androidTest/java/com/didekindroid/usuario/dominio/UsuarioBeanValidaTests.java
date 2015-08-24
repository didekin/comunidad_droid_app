package com.didekindroid.usuario.dominio;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.didekindroid.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/06/15
 * Time: 18:47
 */
@RunWith(AndroidJUnit4.class)
public class UsuarioBeanValidaTests {

    private static final String TAG = UsuarioBeanValidaTests.class.getCanonicalName();
    private StringBuilder errors;
    private Resources resources;

    @Before
    public void doBefore()
    {
        Log.d(TAG, "doBefore()");
        resources = InstrumentationRegistry.getTargetContext().getResources();
        errors = new StringBuilder(resources.getText(R.string.error_validation_msg));
    }

    @Test
    public void checkPreconditions()
    {
        assertThat(errors, notNullValue());
        assertThat(resources, notNullValue());
    }

    @Test
    public void testValidate() throws Exception
    {
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "password1", "password1", "001", "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("", "alias1", "password1", "password1", "001", "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), containsString(resources.getText(R.string.email_hint).toString()));
        usuarioBean = new UsuarioBean("", "alias1", "password1", "", "001", "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), allOf(containsString(resources.getText(R.string.password_different).toString())
                , containsString(resources.getText(R.string.password_different).toString())));
    }

    @Test
    public void testValidatePassword() throws Exception
    {
        /*PASSWORD("[0-9a-zA-Z_]{6,60}")*/
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1", "001",
                "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB__s_word1", "23AB__s_word1", "001",
                "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB*_s_word1", "23AB*_s_word1", "001",
                "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23AB_s_word1", "23AB*_s_word1", "001",
                "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        assertThat(errors.toString(), allOf(containsString(resources.getText(R.string.password_different).toString())
                , containsString(resources.getText(R.string.password_different).toString())));
    }

    @Test
    public void testValidatePrefixTf() throws Exception
    {
        /*PREFIX("[0-9]{1,4}")*/
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1", "2",
                "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1", "2*",
                "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1", "23452",
                "12345678");
        assertThat(usuarioBean.validate(resources, errors), is(false));
    }

    @Test
    public void testValidateNumeroTf() throws Exception
    {
        /*TELEFONO("[0-9]{6,15}")*/
        UsuarioBean usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1", "2",
                "123456");
        assertThat(usuarioBean.validate(resources, errors), is(true));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1", "2",
                "12345678912345678");
        assertThat(usuarioBean.validate(resources, errors), is(false));
        usuarioBean = new UsuarioBean("user@name.com", "alias1", "23pas_sword1", "23pas_sword1", "2",
                "123456A_2");
        assertThat(usuarioBean.validate(resources, errors), is(false));
    }

    @Test
    public void testValidateUserName() throws Exception
    {
        /*EMAIL("[\\w\\._\\-]{1,48}@[\\w\\-_]{1,40}\\.[\\w&&[^0-9]]{1,10}")*/
        UsuarioBean usuarioBean = new UsuarioBean("user_@name.com", "alias1", "23pas_sword1", "23pas_sword1", "2",
                "123456");
        assertThat(usuarioBean.validate(resources, errors), is(true));
    }
}