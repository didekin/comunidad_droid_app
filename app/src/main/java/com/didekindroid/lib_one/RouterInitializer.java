package com.didekindroid.lib_one;

import com.didekindroid.lib_one.api.router.RouterInitializerIf;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.model.common.dominio.BeanBuilder;

import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:13
 */
public final class RouterInitializer implements RouterInitializerIf {

    public static final AtomicReference<RouterInitializerIf> routerInitializer = new AtomicReference<>();
    private final UiExceptionRouterIf exceptionRouter;
    private final MnRouterIf mnRouter;
    private final ContextualRouterIf contextRouter;

    @SuppressWarnings("SyntheticAccessorCall")
    private RouterInitializer(RouterInitializerBuilder builder)
    {
        exceptionRouter = builder.exceptionRouter;
        mnRouter = builder.mnRouter;
        contextRouter = builder.contextRouter;
    }

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return exceptionRouter;
    }

    @Override
    public MnRouterIf getMnRouter()
    {
        return mnRouter;
    }

    @Override
    public ContextualRouterIf getContextRouter()
    {
        return contextRouter;
    }

    //    ==================== BUILDER ====================

    public static class RouterInitializerBuilder implements BeanBuilder<RouterInitializer> {

        private UiExceptionRouterIf exceptionRouter;
        private MnRouterIf mnRouter;
        private ContextualRouterIf contextRouter;

        public RouterInitializerBuilder exceptionRouter(UiExceptionRouterIf exceptionRouterIn)
        {
            exceptionRouter = exceptionRouterIn;
            return this;
        }

        public RouterInitializerBuilder mnRouter(MnRouterIf mnRouterIn)
        {
            mnRouter = mnRouterIn;
            return this;
        }

        public RouterInitializerBuilder contexRouter(ContextualRouterIf contextRouterIn)
        {
            contextRouter = contextRouterIn;
            return this;
        }

        @SuppressWarnings("SyntheticAccessorCall")
        @Override
        public RouterInitializer build()
        {
            Timber.d("build()");
            return new RouterInitializer(this);
        }
    }
}
