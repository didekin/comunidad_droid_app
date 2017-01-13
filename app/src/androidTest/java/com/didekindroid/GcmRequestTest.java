package com.didekindroid;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.retrofit.RetrofitHandler;
import com.didekinservice.common.gcm.GcmMulticastRequest;
import com.didekinservice.common.gcm.GcmRequest;
import com.didekinservice.common.gcm.GcmResponse;
import com.didekinservice.common.gcm.GcmSingleRequest;
import com.didekinservice.incidservice.gcm.GcmIncidRequestData;
import com.didekinservice.retrofit.GcmEndPointImp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.didekin.incidencia.gcm.GcmKeyValueIncidData.incidencia_open_type;
import static com.didekindroid.incidencia.testutils.GcmConstantForTests.PACKAGE_TEST;
import static com.didekindroid.incidencia.testutils.GcmConstantForTests.test_api_key_header;
import static com.didekinservice.common.GcmServConstant.FCM_HOST_PORT;
import static com.didekinservice.common.GcmServConstant.IDENTITY;
import static com.didekinservice.common.gcm.GcmErrorMessage.InvalidJson;
import static com.didekinservice.common.gcm.GcmErrorMessage.InvalidRegistration;
import static com.didekinservice.common.gcm.GcmErrorMessage.MissingRegistration;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public class GcmRequestTest {

    private GcmEndPointImp endPointImp;
    private FirebaseInstanceId firebaseInstanceId;
    private List<String> gcmTokens = new ArrayList<>();

    @Before
    public void setUp() throws Exception
    {
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        RetrofitHandler retrofitHandler = new RetrofitHandler(FCM_HOST_PORT, 60);
        endPointImp = new GcmEndPointImp(retrofitHandler);
    }

    @After
    public void tearDown() throws Exception
    {
    }

//    =========================== TESTS =============================

    /**
     * Message:
     * {
     *   "collapse_key":"incidencia_open",
     *    "data":
     *     {
     *       "comunidadId":999,"typeMsg":"incidencia_open"
     *     },
     *   "to":"dSTSQVcvvFY:APA91bGNnRhG4Xg2XdzSo5vQWnfzIrx9n8KN3hvfdP_1p8hWS-BM5OvW7Ayk9x1mX5QkTapvKElRh1lYk5LNovT6CC0MI8oV2ynm0KfZaykISLgfmHwoaiysjqTYXb3yc3ZjpZ5_UG0K",
     *   "priority":"normal",
     *   "restricted_package_name":"com.didekin.open.message",
     *   "time_to_live":1724,
     *   "delay_while_idle":true
     * }
     * <p/>
     * Returns a success response:
     * {
     *   "multicast_id":6325292282044173802,
     *   "success":1,
     *   "failure":0,
     *   "canonical_ids":0,
     *   "results":
     *     [
     *       { "message_id":"0:1484156536127962%1c4870d416025130" }
     *     ]
     *  }
     */
    @Test
    public void testSuccessSingle_1() throws Exception
    {
        String gcmToken = firebaseInstanceId.getToken();
        GcmSingleRequest request = new GcmSingleRequest.Builder(gcmToken,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendGcmSingleRequest(IDENTITY, test_api_key_header, request).execute().body();
        assertThat(gcmResponse.getResults()[0].getMessage_id().equals(gcmToken), is(false));
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Single with one token and GZIP encoding.
     * We leave 'accept encoding' header as null.
     */
    @Test
    public void testSuccessSingle_2() throws Exception
    {
        String gcmToken = firebaseInstanceId.getToken();
        GcmSingleRequest request = new GcmSingleRequest.Builder(gcmToken,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendGcmSingleRequest(null, test_api_key_header, request).execute().body();
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(1));
    }

    /**
     * Sinqle request gcmToken is null.
     * {
     *   "collapse_key":"incidencia_open",
     *   "data":
     *     {
     *       "comunidadId":999,
     *       "typeMsg":"incidencia_open"
     *     },
     *   "priority":"normal",
     *   "restricted_package_name":"com.didekin.open.message",
     *   "time_to_live":1724,
     *   "delay_while_idle":true
     * }
     * <p/>
     * It does not return an error response, only:
     *  400 Bad Request
     *  to
     */
    @Test
    public void testErrorSingle_1() throws Exception
    {
        GcmSingleRequest request = new GcmSingleRequest.Builder(null,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        Response response = endPointImp.sendGcmSingleRequest(IDENTITY, test_api_key_header, request).execute();
        assertThat(response.raw().code(), is(InvalidJson.httpStatusCode));
    }

    /**
     * Sinqle request gcmToken is a wrong non empty String.
     * <p/>
     * It returns an error message:
     * {
     *   "multicast_id":7944507699481397986,
     *   "success":0,"failure":1,
     *   "canonical_ids":0,
     *   "results":[
     *     {"error":"InvalidRegistration"}
     *   ]
     * }
     */
    @Test
    public void testErrorSingle_2() throws Exception
    {
        GcmSingleRequest request = new GcmSingleRequest.Builder("wrong_token",
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        Response<GcmResponse> response = endPointImp.sendGcmSingleRequest(IDENTITY, test_api_key_header, request).execute();
        assertThat(response.raw().code(), is(InvalidRegistration.httpStatusCode));

        GcmResponse gcmResponse = response.body();
        assertThat(gcmResponse.getResults()[0].getError(), is(InvalidRegistration.httpMessage));
        assertThat(gcmResponse.getFailure(), is(1));
        assertThat(gcmResponse.getSuccess(), is(0));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Two equal tokens.
     * {
     *  "collapse_key":"incidencia_open",
     *  "data":
     *    { "comunidadId":999,
     *      "typeMsg":"incidencia_open"
     *    },
     *  "restricted_package_name":"com.didekin.open.message",
     *  "priority":"normal",
     *  "registration_ids": [
     *     "dSTSQVcvvFY:APA91bGNnRhG4Xg2XdzSo5vQWnfzIrx9n8KN3hvfdP_1p8hWS-BM5OvW7Ayk9x1mX5QkTapvKElRh1lYk5LNovT6CC0MI8oV2ynm0KfZaykISLgfmHwoaiysjqTYXb3yc3ZjpZ5_UG0K",
     *     "dSTSQVcvvFY:APA91bGNnRhG4Xg2XdzSo5vQWnfzIrx9n8KN3hvfdP_1p8hWS-BM5OvW7Ayk9x1mX5QkTapvKElRh1lYk5LNovT6CC0MI8oV2ynm0KfZaykISLgfmHwoaiysjqTYXb3yc3ZjpZ5_UG0K"
     *   ],
     *   "delay_while_idle":true,
     *   "time_to_live":1724
     * }
     * <p/>
     * Returns a success response:
     * {
     *  "multicast_id":7063623012347955082,
     *  "success":2,
     *  "failure":0,
     *  "canonical_ids":0,
     *  "results":[
     *    {"message_id":"0:1484211780115622%1c4870d416025130"},
     *    {"message_id":"0:1484211780115956%1c4870d416025130"}
     *   ]
     * }
     */
    @Test
    public void testSuccessMulticast_1() throws Exception
    {
        String token = firebaseInstanceId.getToken();
        gcmTokens.add(token);
        // We use the same token again.
        gcmTokens.add(token);
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendMulticast(IDENTITY, test_api_key_header, request);
        assertThat(gcmResponse.getResults()[0].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getResults()[1].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(2));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Multicast with one token.
     * {
     *   "collapse_key":"incidencia_open",
     *   "data":
     *     {
     *       "comunidadId":999,
     *       "typeMsg":"incidencia_open"},
     *       "restricted_package_name":"com.didekin.open.message",
     *       "priority":"normal",
     *       "registration_ids":[
     *         "dSTSQVcvvFY:APA91bGNnRhG4Xg2XdzSo5vQWnfzIrx9n8KN3hvfdP_1p8hWS-BM5OvW7Ayk9x1mX5QkTapvKElRh1lYk5LNovT6CC0MI8oV2ynm0KfZaykISLgfmHwoaiysjqTYXb3yc3ZjpZ5_UG0K"
     *        ],
     *        "delay_while_idle":true,"time_to_live":1724}
     *
     * Response:
     * {
     *   "multicast_id":5983532832643241790,
     *   "success":1,"failure":0,"canonical_ids":0,
     *   "results":[
     *     {"message_id":"0:1484212175345232%1c4870d416025130"}
     *   ]
     * }
     */
    @Test
    public void testSuccessMulticast_2() throws Exception
    {
        gcmTokens.add(firebaseInstanceId.getToken());
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendMulticast(IDENTITY, test_api_key_header, request);
        Thread.sleep(500);
        assertThat(gcmResponse.getResults()[0].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Multicast with one token and GZIP encoding.
     * We leave 'accept encoding' header as null.
     */
    @Test
    public void testSuccessMulticast_3() throws Exception
    {
        gcmTokens.add(firebaseInstanceId.getToken());
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendMulticast(null, test_api_key_header, request);
        assertThat(gcmResponse.getResults()[0].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Sinqle gcmToken is an empty String.
     *  {
     *    "collapse_key":"incidencia_open",
     *    "data":{
     *      "comunidadId":999,
     *      "typeMsg":"incidencia_open"
     *    },
     *    "restricted_package_name":"com.didekin.open.message",
     *    "priority":"normal",
     *    "registration_ids":[""],
     *    "delay_while_idle":true,
     *    "time_to_live":1724
     *  }
     * <p/>
     * Returns:
     * {
     *   "multicast_id":8747202076195423392,
     *   "success":0,
     *   "failure":1,
     *   "canonical_ids":0,
     *   "results":[
     *      {"error":"MissingRegistration"}
     *    ]
     * }
     */
    @Test
    public void testErrorMulticast_1() throws Exception
    {
        gcmTokens.add("");
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        Response<GcmResponse> response = endPointImp.sendGcmMulticastRequest(IDENTITY, test_api_key_header, request).execute();
        assertThat(response.raw().code(), is(MissingRegistration.httpStatusCode));

        GcmResponse gcmResponse = response.body();
        assertThat(gcmResponse.getResults()[0].getError(), is(MissingRegistration.httpMessage));
        assertThat(gcmResponse.getFailure(), is(1));
        assertThat(gcmResponse.getSuccess(), is(0));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * One correct token and three erroneous gcm tokens: empty string, null, wrong String.
     * <p/>
     * {
     *   "multicast_id":4685823509304870829,
     *   "success":1,
     *   "failure":3,
     *   "canonical_ids":0,
     *   "results":[
     *     {"error":"MissingRegistration"},
     *     {"message_id":"0:1484215901788122%1c4870d416025130"},
     *     {"error":"InvalidRegistration"},
     *     {"error":"InvalidRegistration"}
     *   ]
     * }
     */
    @Test
    public void testErrorMulticast_2() throws Exception
    {
        gcmTokens.add("");
        gcmTokens.add(firebaseInstanceId.getToken());
        gcmTokens.add(null);
        gcmTokens.add("wrong_token");
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L), PACKAGE_TEST).build())
                .build();
        Response<GcmResponse> response = endPointImp.sendGcmMulticastRequest(IDENTITY, test_api_key_header, request).execute();
        assertThat(response.raw().code(), is(200));

        GcmResponse gcmResponse = response.body();
        assertThat(gcmResponse.getResults()[0].getMessage_id(), nullValue());
        assertThat(gcmResponse.getResults()[1].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getResults()[2].getMessage_id(), nullValue());
        assertThat(gcmResponse.getResults()[3].getMessage_id(), nullValue());
        assertThat(gcmResponse.getResults()[0].getError(), is(MissingRegistration.httpMessage));
        assertThat(gcmResponse.getResults()[2].getError(), is(InvalidRegistration.httpMessage));
        assertThat(gcmResponse.getResults()[3].getError(), is(InvalidRegistration.httpMessage));
        assertThat(gcmResponse.getFailure(), is(3));
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }
}
