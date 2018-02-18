package com.didekindroid.incidencia.testutils;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static com.didekindroid.lib_one.util.UiUtil.getStringFromInteger;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 12:37
 */
public final class IncidDataTestUtils {

    public static final String INCID_DEFAULT_DESC = "Incidencia_1";
    public static final String RESOLUCION_DEFAULT_DESC = "Resolucion_1";
    public static final String AVANCE_DEFAULT_DES = "Avance_1";
    public static final int COSTE_ESTIM_DEFAULT = 1122;
    public static final String COSTE_ESTIM_DEFAULT_String = getStringFromInteger(COSTE_ESTIM_DEFAULT);

    private IncidDataTestUtils()
    {
    }

    public static Incidencia doIncidencia(String userName, String descripcion, long comunidadId, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .userName(userName)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Incidencia doIncidenciaWithId(long incidenciaId, String descripcion, long comunidadId, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidenciaId)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Incidencia doSimpleIncidenciaWithId(long incidenciaId, Timestamp altaDate, Timestamp resolucionDate)
    {
        return new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidenciaId)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(987L).build())
                .descripcion("Simple description")
                .userName("simpleUser")
                .ambitoIncid(new AmbitoIncidencia((short) 33))
                .fechaAlta(altaDate)
                .fechaCierre(resolucionDate)
                .build();
    }

    public static void makeAndRegIncidImportancia(UsuarioComunidad userComu, short importancia) throws UiException
    {
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComu.getUsuario().getUserName(), INCID_DEFAULT_DESC, userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia(importancia)
                .build();

        incidenciaDao.regIncidImportancia(incidImportancia);
    }

    public static IncidImportancia makeRegGetIncidImportancia(UsuarioComunidad userComu, short importancia) throws UiException
    {
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComu.getUsuario().getUserName(), INCID_DEFAULT_DESC, userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia(importancia)
                .build();

        incidenciaDao.regIncidImportancia(incidImportancia);
        Incidencia incidenciaDb = incidenciaDao.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).get(0).getIncidencia();
        incidImportancia = incidenciaDao.seeIncidImportancia(incidenciaDb.getIncidenciaId()).getIncidImportancia();
        return incidImportancia;
    }

    public static IncidenciaUser doSimpleIncidenciaUser(long incidenciaId, Timestamp incidAltaDate, long usuarioId, Timestamp resolucionDate)
    {
        final Incidencia incidencia = doSimpleIncidenciaWithId(incidenciaId, incidAltaDate, resolucionDate);
        return new IncidenciaUser.IncidenciaUserBuilder(incidencia)
                .usuario(new Usuario.UsuarioBuilder()
                        .uId(usuarioId)
                        .userName(incidencia.getUserName())
                        .build())
                .fechaAltaResolucion(resolucionDate)
                .build();
    }

    public static List<IncidenciaUser> doIncidenciaUsers(IncidImportancia incidImportancia)
    {
        final List<IncidenciaUser> list = new ArrayList<>(3);
        Timestamp resolucionDate = incidImportancia.getIncidencia().getFechaCierre();
        Timestamp altaIncidDate = incidImportancia.getIncidencia().getFechaAlta();
        IncidenciaUser iu_1 = doSimpleIncidenciaUser(33L, altaIncidDate, 34L, resolucionDate);
        IncidenciaUser iu_2 = doSimpleIncidenciaUser(11L, altaIncidDate, 14L, resolucionDate);
        IncidenciaUser iu_3 = doSimpleIncidenciaUser(22L, altaIncidDate, 24L, resolucionDate);
        list.add(iu_1);
        list.add(iu_2);
        list.add(iu_3);
        return list;
    }

    public static IncidenciaUser insertGetIncidenciaUser(UsuarioComunidad userComu, int importancia) throws UiException
    {
        makeAndRegIncidImportancia(userComu, (short) importancia);
        return incidenciaDao.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).get(0);
    }

    public static IncidenciaUser insertGetIncidenciaUser(long incidenciaId, UsuarioComunidad userComu, int importancia) throws UiException
    {
        IncidImportancia incidImportancia =
                new IncidImportancia.IncidImportanciaBuilder(doIncidenciaWithId(incidenciaId, INCID_DEFAULT_DESC, userComu.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(userComu)
                        .importancia((short) importancia)
                        .build();
        incidenciaDao.regIncidImportancia(incidImportancia);
        return incidenciaDao.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).get(0);
    }

    public static IncidImportancia insertGetIncidImportancia(UsuarioComunidad userComu) throws IOException, UiException
    {
        signUpAndUpdateTk(userComu);
        UsuarioComunidad userComuDb = userComuDao.seeUserComusByUser().get(0);
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComuDb.getUsuario().getUserName(), INCID_DEFAULT_DESC, userComuDb.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComuDb)
                .importancia((short) 3).build();
        incidenciaDao.regIncidImportancia(incidImportancia);
        Incidencia incidenciaDb = incidenciaDao.seeIncidsOpenByComu(userComuDb.getComunidad().getC_Id()).get(0).getIncidencia();
        incidImportancia = incidenciaDao.seeIncidImportancia(incidenciaDb.getIncidenciaId()).getIncidImportancia();
        return incidImportancia;
    }

    public static IncidComment doComment(String descComment, Incidencia incidencia)
    {
        Incidencia incidenciaIn = new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidencia.getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder().c_id(incidencia.getComunidad().getC_Id()).build())
                .build();

        return new IncidComment.IncidCommentBuilder().descripcion(descComment)
                .incidencia(incidenciaIn)
                .redactor(null)
                .build();
    }

    public static Resolucion doResolucion(Incidencia incidencia, String descripcion, int costeEstimado, Timestamp fechaPrev)
    {
        return new Resolucion.ResolucionBuilder(incidencia)
                .descripcion(descripcion)
                .costeEstimado(costeEstimado)
                .fechaPrevista(fechaPrev)
                .build();
    }

    public static Resolucion insertGetResolucionNoAdvances(IncidImportancia incidImportancia) throws UiException
    {
        // Registramos resolución.
        Resolucion resolucion = doResolucion(incidImportancia.getIncidencia(),
                RESOLUCION_DEFAULT_DESC,
                COSTE_ESTIM_DEFAULT,
                incidImportancia.getFechaAlta());
        incidenciaDao.regResolucion(resolucion);
        resolucion = incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        return resolucion;
    }

    public static Resolucion insertGetDefaultResolucion(UsuarioComunidad userComu) throws UiException, InterruptedException
    {
        // Insertamos resolución.
        Incidencia incidencia = insertGetIncidenciaUser(userComu, 1).getIncidencia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, RESOLUCION_DEFAULT_DESC, 1122, new Timestamp(new Date().getTime()));
        assertThat(incidenciaDao.regResolucion(resolucion), is(1));
        return incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
    }
}