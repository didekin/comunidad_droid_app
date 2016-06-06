package com.didekinservice.common;

/**
 * User: pedro@didekin
 * Date: 02/06/16
 * Time: 10:07
 */
public abstract class GcmRequestData {

    final String typeMsg;

    protected GcmRequestData(String typeMsg)
    {
        this.typeMsg = typeMsg;
    }
}
