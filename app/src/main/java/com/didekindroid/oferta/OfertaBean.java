package com.didekindroid.oferta;

import java.util.Date;

/**
 * User: pedro
 * Date: 04/02/15
 * Time: 13:59
 */
public class OfertaBean {

    private static final String TAG = "OfertaBean";

    private final Date dateOffer;
    private final String title;
    private final String description;

    public OfertaBean(String title, String description)
        {

            this.dateOffer = new Date();
            this.title = title;
            this.description = description;
        }

    public String getTitle()
    {
        return title;
    }

    public Date getDateOffer()
    {
        return dateOffer;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String toString()
    {
        return getTitle();
    }
}
