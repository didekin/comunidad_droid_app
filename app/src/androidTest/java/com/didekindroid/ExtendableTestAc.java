package com.didekindroid;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekinlib.model.usuario.Usuario;


/**
 * User: pedro@didekin
 * Date: 19/12/16
 * Time: 11:32
 */
public interface ExtendableTestAc {

    void checkNavigateUp();

    int getNextViewResourceId();
}
