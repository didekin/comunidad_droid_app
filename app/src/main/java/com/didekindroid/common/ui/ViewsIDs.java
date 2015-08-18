package com.didekindroid.common.ui;

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
    COMUNIDADES_USER(-1321),
    /* Comunidades found-searched view */
    COMUNIDADES_FOUND(-1323),
    ;

    public int idView;

    ViewsIDs(int idView)
    {
        this.idView = idView;
    }

    public int getIdView()
    {
        return idView;
    }
}
