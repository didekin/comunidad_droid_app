package com.didekindroid.incidencia.list;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ControllerIdentityAbs;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/03/17
 * Time: 12:18
 */
@RunWith(AndroidJUnit4.class)
public class IncidListObserverTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Test
    public void onSuccess() throws Exception
    {
        List<IncidenciaUser> list = new ArrayList<>(1);
        IncidenciaUser incidenciaUser = new IncidenciaUser.IncidenciaUserBuilder(
                new Incidencia.IncidenciaBuilder().incidenciaId(111L).userName("this_user_name").build()
        ).build();
        list.add(incidenciaUser);
        Maybe.just(list).subscribeWith(new IncidListObserver(new ControllerIncidSeeForTest()));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void onError() throws Exception
    {
        Maybe.<List<IncidenciaUser>>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new IncidListObserver(new ControllerIncidSeeForTest()));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ControllerIncidSeeForTest extends ControllerIdentityAbs implements
            ControllerIncidSeeIf<Resolucion> {

        @Override
        public ManagerIncidSeeIf.ViewerIf getViewer()
        {
            return null;
        }

        @Override
        public void loadIncidsByComu(long comunidadId)
        {
        }

        @Override
        public void processBackLoadIncidsByComu(List<IncidenciaUser> incidList)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
            assertThat(incidList.size(), is(1));
        }

        @Override
        public void dealWithIncidSelected(Incidencia incidencia)
        {
        }

        @Override
        public void processBackDealWithIncidencia(Resolucion itemBack)
        {
        }

        @Override
        public void processReactorError(Throwable e)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
            UiException ue = (UiException) e;
            assertThat(ue.getErrorBean().getMessage(), is(GENERIC_INTERNAL_ERROR.getHttpMessage()));
        }
    }
}