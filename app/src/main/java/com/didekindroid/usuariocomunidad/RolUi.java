package com.didekindroid.usuariocomunidad;

import android.content.res.Resources;

import com.didekinaar.R;

import java.util.HashMap;
import java.util.Map;

import static com.didekin.usuariocomunidad.Rol.ADMINISTRADOR;
import static com.didekin.usuariocomunidad.Rol.INQUILINO;
import static com.didekin.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekin.usuariocomunidad.Rol.PROPIETARIO;

/**
 * User: pedro@didekin
 * Date: 08/06/15
 * Time: 18:03
 */
public enum RolUi {

    ADM(ADMINISTRADOR.function, R.id.reg_usercomu_checbox_admin, R.string.reg_usercomu_checbox_admin_rot),
    PRE(PRESIDENTE.function, R.id.reg_usercomu_checbox_pre, R.string.reg_usercomu_checkbox_pre_rot),
    PRO(PROPIETARIO.function, R.id.reg_usercomu_checbox_pro, R.string.reg_usercomu_checbox_pro_rot),
    INQ(INQUILINO.function, R.id.reg_usercomu_checbox_inq, R.string.reg_usercomu_checbox_inq_rot),;

    private static final Map<String, RolUi> functionToResorceString = new HashMap<>();

    /**
     *  Map relating a function with its RolUi instance.
     */
    static {
        for (RolUi rolUi : values()) {
            functionToResorceString.put(rolUi.function, rolUi);
        }
    }

    /**
     * The strings denominating roles in the database.
     */
    public final String function;
    /**
     * View id of the related checbox in the layout files.
     */
    public final int resourceViewId;
    /**
     * Resource string associtated to rol functions.
     */
    private final int resourceStringId;

    RolUi(String function, int resourceViewId, int resourceStringId)
    {
        this.function = function;
        this.resourceViewId = resourceViewId;
        this.resourceStringId = resourceStringId;
    }

    /**
     *  It obtains the resource string associated wiht a rol function.
     */
    public static int getResourceStringId(String function)
    {
        return functionToResorceString.get(function).resourceStringId;
    }

    /**
     * This method formats and concatenates the strings used in the app to denominate the rol functions.
     */
    public static String formatRolToString(String rolesString, Resources resources)
    {
        String[] rolFunctions = rolesString.split(",");
        StringBuilder builder = new StringBuilder();
        String resourceString;

        for (String rolFunction : rolFunctions) {
            resourceString = resources.getString(getResourceStringId(rolFunction));
            builder.append(resourceString).append(", ");
        }
        builder.deleteCharAt(builder.length() - 2);
        return builder.toString().trim();
    }
}
