package com.didekindroid.usuariocomunidad.testutil;

import com.didekin.comunidad.Comunidad;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.didekin.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekin.usuariocomunidad.Rol.PROPIETARIO;
import static com.didekinaar.testutil.AarActivityTestUtils.updateSecurityData;
import static com.didekinaar.usuario.UsuarioService.AarUserServ;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_DROID;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.comunidad.testutil.ComuTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.comunidad.testutil.ComuTestUtil.COMU_LA_FUENTE;
import static com.didekindroid.comunidad.testutil.ComuTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuTestUtil.COMU_REAL;
import static com.didekindroid.comunidad.testutil.ComuTestUtil.COMU_TRAV_PLAZUELA_11;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.RolUi.ADM;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 11:38
 */

public final class UserComuTestUtil {

    public static final UsuarioComunidad COMU_ESCORIAL_PEPE = makeUsuarioComunidad(COMU_EL_ESCORIAL, USER_PEPE,
            "portal22", "esc22", "planta22", "door22", PRESIDENTE.function.concat(",").concat(INQ.function));
    public static final UsuarioComunidad COMU_ESCORIAL_JUAN = makeUsuarioComunidad(COMU_EL_ESCORIAL, USER_JUAN,
            "portal21", "esc21", "planta21", "door21", PRO.function);
    public static final UsuarioComunidad COMU_LA_FUENTE_PEPE = makeUsuarioComunidad(COMU_LA_FUENTE, USER_PEPE,
            "portal33", "esc33", "planta33", "door33", ADM.function.concat(",").concat(PRE.function));
    public static final UsuarioComunidad COMU_PLAZUELA5_JUAN = makeUsuarioComunidad(COMU_LA_PLAZUELA_5, USER_JUAN, null,
            null, "planta3", "doorA", ADM.function);
    public static final UsuarioComunidad COMU_PLAZUELA5_PEPE = makeUsuarioComunidad(COMU_LA_PLAZUELA_5, USER_PEPE,
            "portal11", "esc11", "planta11", "door11", PRE.function.concat(",").concat(PRO.function));
    public static final UsuarioComunidad COMU_TRAV_PLAZUELA_PEPE = makeUsuarioComunidad(COMU_TRAV_PLAZUELA_11, USER_PEPE,
            "portalA", null, "planta2", null, INQ.function);
    public static final UsuarioComunidad COMU_REAL_JUAN = makeUsuarioComunidad(COMU_REAL, USER_JUAN, "portal", "esc",
            "plantaX", "door12", PROPIETARIO.function);
    public static final UsuarioComunidad COMU_REAL_PEPE = makeUsuarioComunidad(COMU_REAL, USER_PEPE, "portal",
            "esc", "plantaY", "door21", PRO.function);
    public static final UsuarioComunidad COMU_REAL_DROID = makeUsuarioComunidad(COMU_REAL, USER_DROID, "portal",
            "esc", "plantaH", "door11", PRO.function);

    private UserComuTestUtil()
    {
    }

    public static Usuario signUpAndUpdateTk(UsuarioComunidad usuarioComunidad) throws IOException, UiException
    {
        AppUserComuServ.regComuAndUserAndUserComu(usuarioComunidad).execute().body();
        updateSecurityData(usuarioComunidad.getUsuario().getUserName(), usuarioComunidad.getUsuario().getPassword());
        return AarUserServ.getUserData();
    }

    public static void regTwoUserComuSameUser(List<UsuarioComunidad> usuarioComunidadList) throws IOException, UiException
    {
        signUpAndUpdateTk(usuarioComunidadList.get(0));
        AppUserComuServ.regComuAndUserComu(usuarioComunidadList.get(1));
    }

    public static void regThreeUserComuSameUser(List<UsuarioComunidad> usuarioComunidadList, Comunidad comunidad) throws IOException, UiException
    {
        regTwoUserComuSameUser(usuarioComunidadList);
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad(comunidad, usuarioComunidadList.get(0).getUsuario(),
                null, null, "plan-5", null, ADM.function);
        AppUserComuServ.regComuAndUserComu(usuarioComunidad);
    }

    public static void regSeveralUserComuSameUser(UsuarioComunidad... userComus) throws IOException, UiException
    {
        Objects.equals(userComus.length > 0, true);
        signUpAndUpdateTk(userComus[0]);
        for (int i = 1; i < userComus.length; i++) {
            AppUserComuServ.regComuAndUserComu(userComus[i]);
        }
    }

    public static List<UsuarioComunidad> makeListTwoUserComu()
    {
        // Dos comunidades diferentes con un mismo userComu.
        List<UsuarioComunidad> userComuList = new ArrayList<>(2);
        userComuList.add(COMU_REAL_JUAN);
        userComuList.add(COMU_PLAZUELA5_JUAN);
        return userComuList;
    }

    public static UsuarioComunidad makeUsuarioComunidad(Comunidad comunidad, Usuario usuario, String portal, String escalera,
                                                        String planta,
                                                        String puerta,
                                                        String roles)
    {
        return new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal(portal)
                .escalera(escalera)
                .planta(planta)
                .puerta(puerta)
                .roles(roles).build();
    }

    public static UsuarioComunidad makeUserComuWithComunidadId(UsuarioComunidad usuarioComunidad, long comunidadId)
    {

        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadId).build();
        return new UsuarioComunidad.UserComuBuilder(comunidad, usuarioComunidad.getUsuario()).userComuRest(usuarioComunidad).build();
    }
}
