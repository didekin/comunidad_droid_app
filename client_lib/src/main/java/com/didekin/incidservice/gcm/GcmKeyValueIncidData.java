package com.didekin.incidservice.gcm;

/**
 * User: pedro@didekin
 * Date: 06/06/16
 * Time: 19:03
 */
public class GcmKeyValueIncidData {
    /**
     * The value associated to typ_message_key for incidencia related messages.
     */
    public static final String incidencia_type = "incidencia";
    /**
     * The value associated to typ_message_key for resolucion related messages.
     */
    @SuppressWarnings("unused")
    public static final String resolucion_type = "resolucion";
    /**
     * The key to retrieve from the data payload the comunidaId.
     */
    public static final String comunidadId_key = "comunidadId";
}
