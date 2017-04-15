package com.didekindroid.incidencia.core.edit;

import android.view.View;

/**
 * User: pedro@didekin
 * Date: 13/04/17
 * Time: 16:32
 */
public class LinkToImportanciaUsersListener implements View.OnClickListener {

    private final LinkToImportanciaUsersClickable clickable;

    public LinkToImportanciaUsersListener(LinkToImportanciaUsersClickable clickable)
    {
        this.clickable = clickable;
    }

    @Override
    public void onClick(View v)
    {
        clickable.onClickLinkToImportanciaUsers(this);
    }
}
