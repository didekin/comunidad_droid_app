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
    public static final String incidencia_open_type = "incidencia_open";

    /**
     * The value associated to typ_message_key for resolucion related messages.
     */
    public static final String incidencia_closed_type = "incidencia_closed";

    /**
     * The value associated to typ_message_key for resolucion related messages.
     */
    public static final String resolucion_open_type = "resolucion_open";

    /**
     * The key to retrieve from the data payload the comunidaId.
     */
    public static final String comunidadId_key = "comunidadId";
}
