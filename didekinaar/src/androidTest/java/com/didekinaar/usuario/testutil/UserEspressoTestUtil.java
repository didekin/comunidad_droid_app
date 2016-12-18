package com.didekinaar.usuario.testutil;

import com.didekin.usuario.Usuario;
import com.didekinaar.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:21
 */

public final class UserEspressoTestUtil {

    private UserEspressoTestUtil()
    {
    }

    public static void typeUserData(String email, String alias, String password, String passwordConfirm)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText(passwordConfirm));
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText(email));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText(alias), closeSoftKeyboard());
    }

    public static void validaTypedUsuario(Usuario usuario, String email, String alias1, String password)
    {
        assertThat(usuario, notNullValue());
        assertThat(usuario.getUserName(), is(email));
        assertThat(usuario.getAlias(), is(alias1));
        assertThat(usuario.getPassword(), is(password));
    }
}
