package com.didekindroid.uiutils;

/**
 * User: pedro
 * Date: 12/07/15
 * Time: 16:13
 */
/**
 * Enum for activities views without explicit layout.
 */
public enum ViewsIDs {

    /* User's comunidades view. */
    SEE_USER_COMU_BY_USER(-1321),
    /* Comunidades found-searched view */
    COMU_SEARCH_RESULTS(-1323),
    /* Users signed-up in a comunidad */
    SEE_USERCOMU_BY_COMU(-1325),

    ;

    public int idView;

    ViewsIDs(int idView)
    {
        this.idView = idView;
    }
}
