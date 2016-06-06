package com.didekinservice.common.gcm;

import com.didekin.common.controller.RetrofitHandler;
import com.didekinservice.common.GcmEndPointImp;
import com.didekinservice.common.GcmMulticastRequest;
import com.didekinservice.common.GcmRequest;
import com.didekinservice.common.GcmResponse;
import com.didekinservice.common.GcmResponse.Result;
import com.didekinservice.common.GcmTokensHolder;
import com.didekinservice.incidservice.gcm.GcmIncidRequestData;
import com.google.gson.Gson;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.didekin.incidservice.gcm.GcmKeyValueIncidData.incidencia_type;
import static com.didekinservice.common.GcmResponse.GcmErrorMessage.InvalidRegistration;
import static com.didekinservice.common.GcmResponse.GcmErrorMessage.NotRegistered;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 03/06/16
 * Time: 12:34
 */
public class GcmMockTest {

    private static final String MSG_ID_1 = "msg_id_1";
    private static final String MSG_ID_3 = "msg_id_3";
    private static final String REGISTRATION_ID_1_A = "registration_id_1A";
    private static final String REGISTRATION_ID_1_B = "registration_id_1B";
    private static final String REGISTRATION_ID_2_A = "registration_id_2A";
    private static final String REGISTRATION_ID_3_A = "registration_id_3A";
    private static final String REGISTRATION_ID_3_B = "registration_id_3B";
    private static final String REGISTRATION_ID_4_A = "registration_id_4A";

    MockWebServer server;
    static RetrofitHandler retrofitHandler;
    GcmEndPointImp endPointImp;
    List<String> gcmTokens = new ArrayList<>();


    @Rule
    public ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable
        {
            server = new MockWebServer();
        }

        @Override
        protected void after()
        {
            try {
                server.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Before
    public void setUp() throws Exception
    {
        retrofitHandler = new RetrofitHandler(server.url("/mock/").toString(), 60);
        endPointImp = new GcmEndPointImp(retrofitHandler);
    }

    /**
     *  Multicast with only one token.
     */
    @Test
    public void testMulticast_1() throws Exception
    {
        // Mock response.
        Result result1 = new Result(null, MSG_ID_1, REGISTRATION_ID_1_B);
        GcmResponse gcmResponseIn = new GcmResponse(1, 1001L, 1, 0, new Result[]{result1});
        String jsonResponse = new Gson().toJson(gcmResponseIn);
        server.enqueue(new MockResponse().setBody(jsonResponse));
        server.enqueue(new MockResponse().setBody(jsonResponse));

        gcmTokens.add(REGISTRATION_ID_1_A);
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_type, 999L)).build())
                .build();

        GcmResponse gcmResponse = endPointImp.sendGcmMulticastRequestImp(request);
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(1));
        assertThat(gcmResponse.getMulticast_id(), is(1001L));
        assertThat(gcmResponse.getTokensToProcess().get(0).getOriginalGcmTk(), is(REGISTRATION_ID_1_A));
        assertThat(gcmResponse.getTokensToProcess().get(0).getNewGcmTk(), is(REGISTRATION_ID_1_B));
    }

    /**
     *  Multicast with 4 tokens.
     */
    @Test
    public void testMulticast_2() throws Exception
    {
        // Mock responses.
        Result result0 = new Result(InvalidRegistration.httpMessage, null, null);
        Result result1 = new Result(null, MSG_ID_1, null);
        Result result2 = new Result(null, MSG_ID_3, REGISTRATION_ID_3_B);
        Result result3 = new Result(NotRegistered.httpMessage, null, null);
        GcmResponse gcmResponseIn = new GcmResponse(2, 2002, 2, 2, new Result[]{result0, result1, result2, result3});
        String jsonResponse = new Gson().toJson(gcmResponseIn);
        server.enqueue(new MockResponse().setBody(jsonResponse));

        gcmTokens.add(REGISTRATION_ID_2_A);
        gcmTokens.add(REGISTRATION_ID_1_A);
        gcmTokens.add(REGISTRATION_ID_3_A);
        gcmTokens.add(REGISTRATION_ID_4_A);
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(
                gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_type, 999L))
                        .build())
                .build();

        GcmResponse gcmResponse = endPointImp.sendGcmMulticastRequestImp(request);

        assertThat(gcmResponse.getResults().length, is(4));
        assertThat(gcmResponse.getSuccess(), is(2));
        assertThat(gcmResponse.getFailure(), is(2));

        // 1 success with new registration_id, to be updated in DB; 1 noRegistered failure, so the tokenId has to be deleted in DB.
        List<GcmTokensHolder> tokensHolders = gcmResponse.getTokensToProcess();
        assertThat(tokensHolders.size(), is(2));
        /* New tokenId:*/
        assertThat(tokensHolders.size(), is(2));
        assertThat(tokensHolders.get(0).getNewGcmTk(), is(REGISTRATION_ID_3_B));
        assertThat(tokensHolders.get(0).getOriginalGcmTk(), is(REGISTRATION_ID_3_A));
        assertThat(gcmResponse.getResults()[2].getError(), nullValue());
        /* Token to be deleted*/
        assertThat(tokensHolders.get(1).getOriginalGcmTk(), is(REGISTRATION_ID_4_A));
        assertThat(tokensHolders.get(1).getNewGcmTk(), nullValue());
        assertThat(gcmResponse.getResults()[3].getError(), is(NotRegistered.httpMessage));
    }
}
