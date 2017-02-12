package com.didekindroid.util;

/**
 * User: pedro@didekin
 * Date: 30/01/17
 * Time: 10:22
 */

public final class CommonAssertionMsg {

    public static final String activity_reactor_not_null = "activity reactor must have been initialized";
    public static final String activity_tokencacher_not_null = "activity token cache must have been initialized";
    static final String subscriptions_should_be_zero = "subscriptions_should_be_zero";
    public static final String bean_fromView_should_be_initialized = "Bean with view data should be initialized";
    public static final String fragment_should_be_initialized = "Fragment should be initialized";
    public static final String intent_extra_should_be_initialized = "Intent extra should be initialized";
    static final String cursor_should_be_closed = "Database cursor should be closed";

    private CommonAssertionMsg()
    {
    }
}
