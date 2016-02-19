package com.didekin.incidservice.gcm;

/**
 * User: pedro@didekin
 * Date: 03/12/15
 * Time: 12:53
 */
@SuppressWarnings("unused")
public interface GcmMsgData {

    String incidencia_type = "incidencia";
    String resolucion_type = "resolucion";

    /**
     * It corresponds to the class implementations field 'typeMsg'.
     */
    String type_message_extra = "typeMsg";

    /**
     * It returns one of the constant type in this class.
     */
    String getTypeMsg();
}
