package com.didekindroid.testutil;

/**
 * User: pedro@didekin
 * Date: 07/03/17
 * Time: 12:23
 */
public final class ConstantExecution {

    // General.
    public static final String WRONG_FLAG_VALUE = "wrong flag value";
    public static final String BEFORE_METHOD_EXEC = "before_method_exec";
    public static final String AFTER_METHOD_EXEC = "after_method_exec";
    public static final String AFTER_METHOD_WITH_EXCEPTION_EXEC = "after execution of method with exception";

    // Viewer mock.
    public static final String VIEWER_FLAG_INITIAL = "viewer_flag_initial";
    public static final String VIEWER_AFTER_ERROR_CONTROL = "viewer_after_error_control";
    // Manager mock.
    public static final String MANAGER_AFTER_REPLACED_VIEW = "manager_after_replacedView";
    public static final String MANAGER_FLAG_INITIAL = "manager_flag_initial";
    public static final String MANAGER_AFTER_ERROR_CONTROL = "manager_after_error_control";
    // IdentityMock.
    public static final String IDENTITY_FLAG_INITIAL = "identityMock_flag_initial";
    public static final String IDENTITY_AFTER_IS_REGISTERED = "identityMock_after_isRegistered";
    public static final String IDENTITY_AFTER_UPDATE_REGISTERED = "identityMock_after_updateRegistered";

    private ConstantExecution()
    {
    }
}
