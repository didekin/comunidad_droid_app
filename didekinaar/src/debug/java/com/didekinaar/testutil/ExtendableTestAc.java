package com.didekinaar.testutil;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekin.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 19/12/16
 * Time: 11:32
 */
public interface ExtendableTestAc {

    Usuario registerUser() throws Exception;

    ActivityTestRule<? extends Activity> getActivityRule();

    void checkNavigateUp();

    int getNextViewResourceId();
}
