package com.didekinaar.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.ErrorBean;
import com.didekinaar.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Response;

import static com.didekin.common.exception.DidekinExceptionMsg.NOT_FOUND;
import static com.didekin.oauth2.OauthClient.CL_USER;
import static com.didekinaar.AppInitializer.creator;
import static com.didekinaar.security.Oauth2DaoRemote.Oauth2;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:07
 */
@RunWith(AndroidJUnit4.class)
public class Oauth2DaoRemoteIfTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(1000);
    }

    @After
    public void cleaningUp() throws UiException
    {
        cleanOptions(CLEAN_NOTHING);
    }

    @Test
    public void testGetNotFoundMsg() throws IOException
    {
        Response<ErrorBean> response = Oauth2.getNotFoundMsg().execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(creator.get().getRetrofitHandler().getErrorBean(response).getMessage(), is(NOT_FOUND.getHttpMessage()));
    }

    @Test
    public void testDoAuthBasicHeader()
    {
        String encodedHeader = Oauth2.doAuthBasicHeader(CL_USER);
        assertThat(encodedHeader, equalTo("Basic dXNlcjo="));
    }
}