package com.didekinservice.common;

/**
 * User: pedro@didekin
 * Date: 04/12/15
 * Time: 14:53
 */
@SuppressWarnings("unused")
public class GcmTokensHolder {

    private final String originalGcmTk;
    private final String newGcmTk;

    public GcmTokensHolder(String newGcmTk, String originalGcmTk)
    {
        this.newGcmTk = newGcmTk;
        this.originalGcmTk = originalGcmTk;
    }

    public String getNewGcmTk()
    {
        return newGcmTk;
    }

    public String getOriginalGcmTk()
    {
        return originalGcmTk;
    }
}