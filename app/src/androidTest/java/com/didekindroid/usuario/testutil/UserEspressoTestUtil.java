package com.didekindroid.usuario.testutil;

import com.didekindroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.reg_usuario_password_ediT;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 12:21
 */

public final class UserEspressoTestUtil {

    private UserEspressoTestUtil()
    {
    }

    public static void typeUserDataFull(String email, String alias/*, String password, String passwordConfirm*/)
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), replaceText(email));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), replaceText(alias), closeSoftKeyboard());
    }

    public static void typeUserData(String userName, String alias, String password)
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(userName));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(replaceText(alias));
        onView(withId(R.id.password_validation_ediT)).perform(replaceText(password), closeSoftKeyboard());
    }

    public static void typePswdData(String password, String confirmation)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText(confirmation), closeSoftKeyboard());
    }

    public static void typePswdDataWithPswdValidation(String password, String confirmation, String currentPassword)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText(confirmation));
        onView(withId(R.id.password_validation_ediT)).perform(replaceText(currentPassword), closeSoftKeyboard());
    }

    public static void typeLoginData(String userName, String password)
    {
        onView(withId(reg_usuario_email_editT)).perform(replaceText(userName));
        if (password != null) {
            onView(withId(reg_usuario_password_ediT)).perform(typeText(password));
        }
    }

    public static void checkTextsInDialog(int... textsDialogs)
    {
        for (int textsDialog : textsDialogs) {
            waitAtMost(6, SECONDS).until(isViewDisplayed(onView(withText(textsDialog)).inRoot(isDialog())));
        }
    }
}
