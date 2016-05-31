package com.didekin.usuario.controller;

/**
 * User: pedro@didekin
 * Date: 30/05/16
 * Time: 18:21
 */
public class GcmTokenWrapper {

    private String token;

    public GcmTokenWrapper(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return token;
    }
}
