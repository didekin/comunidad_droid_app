package com.didekindroid.usuario.activity.utils;

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
    INCID_SEE_BY_USER(-1327),
    SEE_USERCOMU_BY_COMU(-1325),
    SEE_USERCOMU_BY_USER(-1321),
    ;

    public final int idView;

    ViewsIDs(int idView)
    {
        this.idView = idView;
    }
}
