package com.didekindroid.usuario.delete;

import com.didekindroid.api.ControllerIf;

/**
 * User: pedro@didekin
 * Date: 23/12/16
 * Time: 11:46
 */
interface CtrlerDeleteMeIf extends ControllerIf {

    boolean deleteMeRemote();

    void onSuccessDeleteMeRemote(boolean isDeleted);
}
