package com.didekindroid.router;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;

import java.util.Map;

import timber.log.Timber;

import static com.didekindroid.router.MnRouterAction.resourceIdToMnItem;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 19:50
 */

public final class MnRouter  implements MnRouterIf {

    public static final MnRouterIf mn_router = new MnRouter(resourceIdToMnItem);
    private final Map<Integer, MnRouterAction> mnActionMap;

    private MnRouter(Map<Integer, MnRouterAction> mnActionMap)
    {
         this.mnActionMap = mnActionMap;
    }

    @Override
    public RouterActionIf getActionFromMnItemId(@NonNull int menuItemRsId)
    {
        Timber.d("getActionFromMnItemId()");
        return mnActionMap.get(menuItemRsId);
    }
}
