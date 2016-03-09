package com.didekindroid.common.activity;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum IntentAction {

    GET_INCID_RESOLUCION,
    ;

    private static final String intentPackage =  "com.didekindroid.common.activity.IntentAction.";

    public final String action;

    IntentAction()
    {
        action = intentPackage.concat(this.name());
    }
}
