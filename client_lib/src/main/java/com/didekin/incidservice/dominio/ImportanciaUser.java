package com.didekin.incidservice.dominio;

/**
 * User: pedro@didekin
 * Date: 27/03/16
 * Time: 15:34
 */
public class ImportanciaUser {
    final String userAlias;
    final short importancia;

    public ImportanciaUser(String userAlias, short importancia)
    {
        this.userAlias = userAlias;
        this.importancia = importancia;
    }

    public String getUserAlias()
    {
        return userAlias;
    }

    public short getImportancia()
    {
        return importancia;
    }
}
