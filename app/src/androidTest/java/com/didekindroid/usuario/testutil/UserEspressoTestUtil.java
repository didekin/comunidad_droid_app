package com.didekindroid.usuario.testutil;

import com.didekindroid.R;
import com.didekinlib.model.usuario.Usuario;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.reg_usuario_alias_ediT;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.reg_usuario_password_ediT;
import static com.didekindroid.R.id.user_data_ac_password_ediT;
import static com.didekindroid.R.string.send_password_by_mail_NO;
import static com.didekindroid.R.string.send_password_by_mail_YES;
import static com.didekindroid.R.string.send_password_by_mail_dialog;
import static org.hamcrest.CoreMatchers.is;
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

    public static void typeUserDataFull(String email, String alias, String password, String passwordConfirm)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), replaceText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), replaceText(passwordConfirm));
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), replaceText(email));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), replaceText(alias), closeSoftKeyboard());
    }

    public static void typeUserData(String userName, String alias, String password)
    {
        onView(withId(reg_usuario_email_editT)).perform(replaceText(userName), closeSoftKeyboard());
        onView(withId(reg_usuario_alias_ediT)).perform(replaceText(alias));
        onView(withId(user_data_ac_password_ediT)).perform(replaceText(password), closeSoftKeyboard());
    }

    public static void validaTypedUserData(Usuario usuario, String email, String alias1, String password)
    {
        assertThat(usuario, notNullValue());
        assertThat(usuario.getUserName(), is(email));
        assertThat(usuario.getAlias(), is(alias1));
        assertThat(usuario.getPassword(), is(password));
    }

    public static void typePswdData(String password, String confirmation)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText(confirmation), closeSoftKeyboard());
    }

    public static void typeLoginData(String userName, String password)
    {
        onView(withId(reg_usuario_email_editT)).perform(typeText(userName));
        onView(withId(reg_usuario_password_ediT)).perform(typeText(password));
        /*onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());*/
    }

    public static void checkPswdSendByMailDialog()
    {
        onView(withText(send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(send_password_by_mail_NO)).inRoot(isDialog())
                .check(matches(isDisplayed()));
    }
}
