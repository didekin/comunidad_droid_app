package com.didekindroid.usuariocomunidad.testutil;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.AuthTkCacher;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_EL_ESCORIAL;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_FUENTE;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_TRAV_PLAZUELA_11;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithGcmTk;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.usuariocomunidad.RolUi.ADM;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 11:38
 */
public final class UserComuTestData {

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
    public static final UsuarioComunidad COMU_REAL_JUAN = makeUsuarioComunidad(comu_real, USER_JUAN, "portal", "esc",
            "plantaX", "door12", PROPIETARIO.function);
    public static final UsuarioComunidad COMU_REAL_PEPE = makeUsuarioComunidad(comu_real, USER_PEPE, "portal",
            "esc", "plantaY", "door21", PRO.function);
    public static final UsuarioComunidad COMU_REAL_DROID = makeUsuarioComunidad(comu_real, USER_DROID, "portal",
            "esc", "plantaH", "door11", PRO.function);

    private UserComuTestData()
    {
    }

    public static Comunidad signUpGetComu(UsuarioComunidad usuarioComunidad)
    {
        regUserComuWithTkCache(usuarioComunidad);
        return userComuDao.getComusByUser().blockingGet().get(0);
    }

    public static Comunidad signUpWithGcmTkGetComu(UsuarioComunidad usuarioComunidad, String gcmTokenIn) throws UiException
    {
        regUserComuWithGcmTk(usuarioComunidad, gcmTokenIn);
        return userComuDao.getComusByUser(((AuthTkCacher) secInitializer.get().getTkCacher()).doAuthHeaderStrMock(gcmTokenIn))
                .map(httpInitializer.get()::getResponseBody)
                .blockingGet()
                .get(0);
    }

    public static UsuarioComunidad signUpGetUserComu(UsuarioComunidad userComuIn)
    {
        return userComuDao.getUserComuByUserAndComu(signUpGetComu(userComuIn).getC_Id()).blockingGet();
    }

    public static void regTwoUserComuSameUser(List<UsuarioComunidad> usuarioComunidadList)
    {
        regUserComuWithTkCache(usuarioComunidadList.get(0));
        userComuDao.regComuAndUserComu(usuarioComunidadList.get(1)).blockingAwait();
    }

    public static void regSeveralUserComuSameUser(UsuarioComunidad... userComus)
    {
        assertThat(userComus.length > 0, is(true));
        regUserComuWithTkCache(userComus[0]);
        for (int i = 1; i < userComus.length; i++) {
            userComuDao.regComuAndUserComu(userComus[i]).blockingAwait();
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
