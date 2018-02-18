package com.didekindroid.lib_one.api.router;

/**
 * User: pedro@didekin
 * Date: 18/02/2018
 * Time: 13:41
 */
@SuppressWarnings({"AbstractClassWithoutAbstractMethods"})
public abstract class RouterInitializerMock implements RouterInitializerIf {

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return null;
    }

    @Override
    public MnRouterIf getMnRouter()
    {
        return null;
    }

    @Override
    public ContextualRouterIf getContextRouter()
    {
        return null;
    }
}
