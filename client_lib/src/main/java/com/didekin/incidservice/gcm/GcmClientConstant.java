package com.didekin.incidservice.gcm;

/**
 * User: pedro@didekin
 * Date: 03/12/15
 * Time: 12:53
 * <p/>
 * Abstract class with the constants to initialize 'data' payload in firebase gcm messages.
 */
@SuppressWarnings({"unused"})
public abstract class GcmClientConstant {

//    ============================== VALUES ==================================
    /**
     * The value associated to typ_message_key for incidencia related messages.
     */
    public static final String incidencia_type = "incidencia";
    /**
     * The value associated to typ_message_key for resolucion related messages.
     */
    public static final String resolucion_type = "resolucion";

//    ============================== KEYS =====================================
    /**
     * The key to retrieve from the data payload the type of message.
     */
    public static final String type_message_key = "typeMsg";
}
