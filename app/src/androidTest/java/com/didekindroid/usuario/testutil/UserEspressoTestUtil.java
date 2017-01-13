package com.didekindroid.usuario.testutil;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;

import com.didekin.usuario.Usuario;
import com.didekindroid.R;

import org.hamcrest.Matchers;

import static android.support.test.espresso.Espresso.onView;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(ViewActions.scrollTo(), ViewActions.typeText(password));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_confirm_ediT)).perform(ViewActions.scrollTo(), ViewActions.typeText(passwordConfirm));
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(ViewActions.scrollTo(), ViewActions.typeText(email));
        onView(ViewMatchers.withId(R.id.reg_usuario_alias_ediT)).perform(ViewActions.scrollTo(), ViewActions.typeText(alias), ViewActions.closeSoftKeyboard());
    }

    public static void validaTypedUsuario(Usuario usuario, String email, String alias1, String password)
    {
        assertThat(usuario, notNullValue());
        assertThat(usuario.getUserName(), Matchers.is(email));
        assertThat(usuario.getAlias(), Matchers.is(alias1));
        assertThat(usuario.getPassword(), Matchers.is(password));
    }
}
