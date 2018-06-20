package com.didekindroid.usuario.testutil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:21
 */
public final class UserEspressoTestUtil {

    private UserEspressoTestUtil()
    {
    }

    public static void typeUserNameAlias(String email, String alias)
    {
        onView(withId(com.didekindroid.lib_one.R.id.reg_usuario_email_editT)).perform(replaceText(email));
        onView(withId(com.didekindroid.lib_one.R.id.reg_usuario_alias_ediT)).perform(replaceText(alias), closeSoftKeyboard());
    }
}
