package com.didekinservice.incidservice.gcm;

import com.didekinservice.common.GcmRequestData;

/**
 * This class is used for 'data' JSON payload in FCM incidencia messages.
 */
public class GcmIncidRequestData extends GcmRequestData {

    final long comunidadId;

    public GcmIncidRequestData(String typeMsg, long comunidadId)
    {
        super(typeMsg);
        this.comunidadId = comunidadId;
    }
}
