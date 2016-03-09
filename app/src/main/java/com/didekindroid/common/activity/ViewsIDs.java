package com.didekindroid.common.activity;

/**
 * User: pedro
 * Date: 12/07/15
 * Time: 16:13
 */
/**
 * Enum for activities views without explicit layout.
 */
public enum ViewsIDs {

    COMU_SEARCH_RESULTS(-1323),
    SEE_USERCOMU_BY_COMU(-1325),
    SEE_USERCOMU_BY_USER(-1321),
    ;

    public final int idView;

    ViewsIDs(int idView)
    {
        this.idView = idView;
    }
}
