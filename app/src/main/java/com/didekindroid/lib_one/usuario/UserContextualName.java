package com.didekindroid.lib_one.usuario;

import com.didekindroid.lib_one.api.router.ContextualNameIf;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 16:12
 */

public enum UserContextualName implements ContextualNameIf {

    default_reg_user,
    default_no_reg_user,
    login_just_done,
    new_comu_user_usercomu_just_registered,
    new_user_usercomu_just_registered,
    user_alias_just_modified,
    user_name_just_modified,
    pswd_just_modified,
    pswd_just_sent_to_user,
    user_just_deleted,
    ;
}
