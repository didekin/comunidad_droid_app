package com.didekindroid.incidencia.core;

import com.didekindroid.ManagerIf;

/**
 * User: pedro@didekin
 * Date: 17/01/17
 * Time: 14:01
 */

public interface ControllerFirebaseTokenIf extends ManagerIf.ControllerIf {

    void checkGcmToken();

    void checkGcmTokenSync();

    void updateIsGcmTokenSentServer(boolean b);


    interface FirebaseTokenReactorIf {

        boolean checkGcmToken(ControllerFirebaseTokenIf controller);

        void checkGcmTokenSync(ControllerFirebaseTokenIf controller);
    }
}
