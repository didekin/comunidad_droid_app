package com.didekindroid.usuario.activity.utils;

import com.didekindroid.R;

import java.util.HashMap;
import java.util.Map;

/**
 * User: pedro@didekin
 * Date: 08/06/15
 * Time: 18:03
 */
public enum RolCheckBox {

    ADMINISTRADOR("adm", R.id.reg_usercomu_checbox_admin, R.string.reg_usercomu_checbox_admin_rot),
    PRESIDENTE("pre", R.id.reg_usercomu_checbox_pre, R.string.reg_usercomu_checkbox_pre_rot),
    PROPIETARIO("pro", R.id.reg_usercomu_checbox_pro, R.string.reg_usercomu_checbox_pro_rot),
    INQUILINO("inq", R.id.reg_usercomu_checbox_inq, R.string.reg_usercomu_checbox_inq_rot),
    ;

    private static final Map<String,RolCheckBox> functionToResorceString = new HashMap<>();

    static{
        for(RolCheckBox rolCheckBox : values()){
            functionToResorceString.put(rolCheckBox.function, rolCheckBox);
        }
    }

    public final String function;
    /*Resource id of the related checbox in reg_usercomu_fr.xml layout file*/
    public final int resourceViewId;
    /* Resource string of the related checkbox in reg_usercomu_fr.xml and usercomu_list_item_view.xml layout files. */
    private final int resourceStringId;

    RolCheckBox(String function, int resourceViewId, int resourceStringId)
    {
        this.function = function;
        this.resourceViewId = resourceViewId;
        this.resourceStringId = resourceStringId;
    }

    public static int getResourceStringId(String function){
        return functionToResorceString.get(function).resourceStringId;
    }
}
