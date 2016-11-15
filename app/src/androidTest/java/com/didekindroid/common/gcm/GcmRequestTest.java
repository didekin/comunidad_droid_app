package com.didekindroid.common.gcm;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.controller.RetrofitHandler;
import com.didekinservice.common.gcm.GcmEndPointImp;
import com.didekinservice.common.gcm.GcmMulticastRequest;
import com.didekinservice.common.gcm.GcmRequest;
import com.didekinservice.common.gcm.GcmResponse;
import com.didekinservice.common.gcm.GcmSingleRequest;
import com.didekinservice.incidservice.gcm.GcmIncidRequestData;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.didekin.incidservice.gcm.GcmKeyValueIncidData.incidencia_open_type;
import static com.didekindroid.DidekinApp.getHttpTimeOut;
import static com.didekinservice.common.gcm.GcmEndPoint.ACCEPT_ENCODING_IDENTITY;
import static com.didekinservice.common.gcm.GcmEndPoint.FCM_HOST_PORT;
import static com.didekinservice.common.gcm.GcmResponse.GcmErrorMessage.InvalidJson;
import static com.didekinservice.common.gcm.GcmResponse.GcmErrorMessage.InvalidRegistration;
import static com.didekinservice.common.gcm.GcmResponse.GcmErrorMessage.MissingRegistration;
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

    static RetrofitHandler retrofitHandler;
    public GcmEndPointImp endPointImp;
    static FirebaseInstanceId firebaseInstanceId;
    List<String> gcmTokens = new ArrayList<>();

    static final String secondToken = "d8qFf3QHu3A:APA91bEaQsPiV1bKGbGnZ5Mw9LdEtubtMMQ3Mget8mQ-iQ78lUKg3_Ego0sosuuWrOx0pjm104aUy4FoaY3tQeTdzfbMChi_ivIrQyUk7zQGS0Gwudb4jUv36ZbdTod3Ff_5G_a7LqG3";

    @BeforeClass
    public static void setFirebaseInstance()
    {
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        retrofitHandler = new RetrofitHandler(FCM_HOST_PORT, getHttpTimeOut());
    }

    @Before
    public void setUp() throws Exception
    {
        endPointImp = new GcmEndPointImp(retrofitHandler);
    }

    @After
    public void tearDown() throws Exception
    {
    }

//    =========================== TESTS =============================

    /**
     * Sinqle token.
     * <p/>
     * Returns a success response:
     * - failure = 0
     * - success = 1
     * - canonical_ids = 0
     * - results with only message_id (no error, no registration_id)
     * - multicast_id
     */
    @Test
    public void testSuccessSingle_1() throws Exception
    {
        Thread.sleep(2000);
        String gcmToken = firebaseInstanceId.getToken();
        GcmSingleRequest request = new GcmSingleRequest.Builder(gcmToken,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendGcmSingleRequest(request).execute().body();
        assertThat(gcmResponse.getResults()[0].getMessage_id().equals(gcmToken), is(false));
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));

        // Probamos con token tablet.
        request = new GcmSingleRequest.Builder(secondToken,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        gcmResponse = endPointImp.sendGcmSingleRequest(request).execute().body();
        assertThat(gcmResponse.getSuccess(), is(1));
    }

    /**
     * Two tokens.
     * <p/>
     * Returns a success response:
     * {
     * "multicast_id":6918528423807382172,
     * "success":2,
     * "failure":0,
     * "canonical_ids":0,
     * "results":
     *   [ {"message_id":"0:1464877318831775%165f9abf76712599"},
     *     {"message_id":"0:1464877318845757%165f9abf76712599"}]
     * }
     */
    @Test
    public void testSuccessMulticast_1() throws Exception
    {
        Thread.sleep(2000);
        gcmTokens.add(firebaseInstanceId.getToken());
        gcmTokens.add(secondToken);
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendGcmMulticastRequest(ACCEPT_ENCODING_IDENTITY, request).execute().body();
        assertThat(gcmResponse.getResults()[0].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getResults()[1].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(2));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * One token.
     * {
     *  "multicast_id":4812133188796339897,
     *  "success":1,
     *  "failure":0,
     *  "canonical_ids":0,
     *  "results":[
     *             {"message_id":"0:1464878753766395%165f9abf76712599"}]
     * }
     */
    @Test
    public void testSuccessMulticast_2() throws Exception
    {
        Thread.sleep(2000);
        gcmTokens.add(firebaseInstanceId.getToken());
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendGcmMulticastRequest(ACCEPT_ENCODING_IDENTITY, request).execute().body();
        Thread.sleep(500);
        assertThat(gcmResponse.getResults()[0].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * One token. GZIP encoding.
     */
    @Test
    public void testSuccessMulticast_3() throws Exception
    {
        Thread.sleep(2000);
        gcmTokens.add(firebaseInstanceId.getToken());
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        GcmResponse gcmResponse = endPointImp.sendGcmMulticastRequest(ACCEPT_ENCODING_IDENTITY,request).execute().body();
        assertThat(gcmResponse.getResults()[0].getMessage_id(), notNullValue());
        assertThat(gcmResponse.getFailure(), is(0));
        assertThat(gcmResponse.getSuccess(), is(1));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Sinqle gcmToken is an empty String.
     * <p/>
     * Returns a  'MissingRegistration' error response:
     * - failure = 1
     * - success = 0
     * - canonical_ids = 0
     * - results with only error message (no message_id, no registration_id)
     * - multicast_id
     */
    @Test
    public void testErrorMulticast_1() throws Exception
    {
        gcmTokens.add("");
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        Response<GcmResponse> response = endPointImp.sendGcmMulticastRequest(ACCEPT_ENCODING_IDENTITY,request).execute();
        assertThat(response.raw().code(), is(MissingRegistration.httpStatusCode));

        GcmResponse gcmResponse = response.body();
        assertThat(gcmResponse.getResults()[0].getError(), is(MissingRegistration.httpMessage));
        assertThat(gcmResponse.getFailure(), is(1));
        assertThat(gcmResponse.getSuccess(), is(0));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Three erroneous gcm tokens: empty string, null, wrong String.
     * <p/>
     * {
     * "multicast_id":5740385918962305887,
     * "success":1,
     * "failure":2,
     * "canonical_ids":0,
     * "results":
     * [
     * {"error":"MissingRegistration"},
     * {"message_id":"0:1464878152538131%165f9abf76712599"},
     * {"error":"InvalidRegistration"},
     * {"error":"InvalidRegistration"}]
     * }
     */
    @Test
    public void testErrorMulticast_2() throws Exception
    {
        gcmTokens.add("");
        String token = firebaseInstanceId.getToken();
        if (token == null){
            Thread.sleep(4000);
            token = firebaseInstanceId.getToken();
        }
        gcmTokens.add(token);
        gcmTokens.add(null);
        gcmTokens.add("wrong_token");
        GcmMulticastRequest request = new GcmMulticastRequest.Builder(gcmTokens,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        Response<GcmResponse> response = endPointImp.sendGcmMulticastRequest(ACCEPT_ENCODING_IDENTITY, request).execute();
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

    /**
     * Sinqle request gcmToken is null.
     * <p/>
     * It does not return an error response.
     */
    @Test
    public void testErrorSingle_1() throws Exception
    {
        GcmSingleRequest request = new GcmSingleRequest.Builder(null,
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        Response response = endPointImp.sendGcmSingleRequest(request).execute();
        assertThat(response.raw().code(), is(InvalidJson.httpStatusCode));
    }

    /**
     * Sinqle request gcmToken is a wrong non empty String.
     * <p/>
     * It returns an error message.
     */
    @Test
    public void testErrorSingle_2() throws Exception
    {
        GcmSingleRequest request = new GcmSingleRequest.Builder("wrong_token",
                new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, 999L)).build())
                .build();
        Response<GcmResponse> response = endPointImp.sendGcmSingleRequest(request).execute();
        assertThat(response.raw().code(), is(InvalidRegistration.httpStatusCode));

        GcmResponse gcmResponse = response.body();
        assertThat(gcmResponse.getResults()[0].getError(), is(InvalidRegistration.httpMessage));
        assertThat(gcmResponse.getFailure(), is(1));
        assertThat(gcmResponse.getSuccess(), is(0));
        assertThat(gcmResponse.getCanonical_ids(), is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }
}
