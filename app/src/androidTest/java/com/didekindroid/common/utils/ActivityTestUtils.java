package com.didekindroid.common.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;

import com.didekin.common.oauth2.OauthToken;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.common.UiException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 15:29
 */
public final class ActivityTestUtils {

    private ActivityTestUtils()
    {
    }

    //    ============================ SECURITY ============================

    public static void updateSecurityData(String userName, String password) throws UiException
    {
        OauthToken.AccessToken token = Oauth2.getPasswordUserToken(userName, password);
        TKhandler.initKeyCacheAndBackupFile(token);
        updateIsRegistered(true, getContext());
    }

    //    ============================ TOASTS ============================

    public static void checkToastInTest(int resourceId, Activity activity, int... resourceFieldsErrorId)
    {
        Resources resources = DidekindroidApp.getContext().getResources();

        ViewInteraction toast = onView(
                withText(containsString(resources.getText(resourceId).toString())))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        if (resourceFieldsErrorId != null) {
            for (int field : resourceFieldsErrorId) {
                toast.check(matches(withText(containsString(resources.getText(field).toString()))));
            }
        }
    }

    public static void checkNoToastInTest(int resourceStringId, Activity activity)
    {
        Resources resources = DidekindroidApp.getContext().getResources();

        onView(
                withText(containsString(resources.getText(resourceStringId).toString())))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(doesNotExist());
    }
}
