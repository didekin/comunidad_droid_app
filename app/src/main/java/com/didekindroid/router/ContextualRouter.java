package com.didekindroid.router;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.router.ContextualNameIf;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;

import java.util.Map;

import timber.log.Timber;

import static com.didekindroid.router.ContextualAction.contextualAcMap;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:55
 */

public final class ContextualRouter implements ContextualRouterIf {

    public static final ContextualRouterIf context_router = new ContextualRouter(contextualAcMap);
    private final Map<ContextualNameIf, ContextualAction> contextualActionMap;

    private ContextualRouter(Map<ContextualNameIf, ContextualAction> contextualAcMap)
    {
        contextualActionMap = contextualAcMap;
    }

    @Override
    public ContextualAction getActionFromContextNm(@NonNull ContextualNameIf contextualName)
    {
        Timber.d("getActionFromContextNm()");
        return contextualActionMap.get(contextualName);
    }
}
