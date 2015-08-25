package com.didekindroid.common.dominio;

import com.didekindroid.R;

import java.util.HashMap;
import java.util.Map;

/**
 * User: pedro@didekin
 * Date: 08/06/15
 * Time: 18:03
 */
public enum Rol {

    ADMINISTRADOR("adm", R.id.reg_usercomu_checbox_admin, R.string.reg_usercomu_checbox_admin_rot),
    PRESIDENTE("pre", R.id.reg_usercomu_checbox_pre, R.string.reg_usercomu_checkbox_pre_rot),
    PROPIETARIO("pro", R.id.reg_usercomu_checbox_pro, R.string.reg_usercomu_checbox_pro_rot),
    INQUILINO("inq", R.id.reg_usercomu_checbox_inq, R.string.reg_usercomu_checbox_inq_rot),
    ;

    private static final Map<String,Rol> functionToResorceString = new HashMap<>();

    static{
        for(Rol rol: values()){
            functionToResorceString.put(rol.function, rol);
        }
    }

    public String function;
    /*Resource id of the related checbox in reg_usercomu_fr.xml layout file*/
    public int resourceViewId;
    /* Resource string of the related checkbox in reg_usercomu_fr.xml and usercomu_list_item_view.xml layout files. */
    private int resourceStringId;

    Rol(String function, int resourceViewId, int resourceStringId)
    {
        this.function = function;
        this.resourceViewId = resourceViewId;
        this.resourceStringId = resourceStringId;
    }

    public static int getResourceStringId(String function){
        return functionToResorceString.get(function).resourceStringId;
    }
}
