package com.didekindroid.lib_one.api.router;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 19:53
 */
@FunctionalInterface
public interface MnRouterIf {
    RouterActionIf getActionFromMnItemId(int menuItemRsId);
}
