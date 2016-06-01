package com.didekindroid.incidencia.gcm;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.controller.RetrofitHandler;
import com.didekinservice.incidservice.gcm.GcmIncidRequest;
import com.didekinservice.incidservice.gcm.GcmResponse;
import com.didekinservice.incidservice.gcm.IncidGcmEndPointImp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.DidekindroidApp.getHttpTimeOut;
import static com.didekinservice.incidservice.gcm.GcmResponse.GcmErrorMessage.MissingRegistration;
import static com.didekinservice.incidservice.gcm.IncidGcmEndPoint.ACCEPT_ENCODING_IDENTITY;
import static com.didekinservice.incidservice.gcm.IncidGcmEndPoint.FCM_HOST_PORT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public class IncidenciaGcmResponseTest {

    static RetrofitHandler retrofitHandler;
    public static IncidGcmEndPointImp endPointImp;
    static FirebaseInstanceId firebaseInstanceId;
    String gcmToken;

    @BeforeClass
    public static void setFirebaseInstance()
    {
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        retrofitHandler = new RetrofitHandler(FCM_HOST_PORT, getHttpTimeOut());
        endPointImp = new IncidGcmEndPointImp(retrofitHandler, ACCEPT_ENCODING_IDENTITY);
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

//    =========================== TESTS =============================

    /**
     * Sinqle request.
     *
     * Returns a success response:
     * - failure = 0
     * - success = 1
     * - canonical_ids = 0
     * - results with only message_id (no error, no registration_id)
     * - multicast_id
     */
    @Test
    public void testSuccess_1() throws Exception
    {
        gcmToken = firebaseInstanceId.getToken();
        GcmIncidRequest.Single request = new GcmIncidRequest.Single(999L, gcmToken);
        GcmResponse gcmResponse = endPointImp.sendGcmIncidRequest(request);
        assertThat(gcmResponse.getResults()[0].getMessage_id().equals(gcmToken), is(false));
        assertThat(gcmResponse.getFailure(),is(0));
        assertThat(gcmResponse.getSuccess(),is(1));
        assertThat(gcmResponse.getCanonical_ids(),is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }

    /**
     * Sinqle request without gcmToken.
     *
     * Returns an error response:
     * - failure = 1
     * - success = 0
     * - canonical_ids = 0
     * - results with only error message (no message_id, no registration_id)
     * - multicast_id
     */
    @Test
    public void testError_1() throws Exception
    {
        gcmToken = firebaseInstanceId.getToken();
        GcmIncidRequest.Single request = new GcmIncidRequest.Single(999L, "");
        GcmResponse gcmResponse = endPointImp.sendGcmIncidRequest(request);
        assertThat(gcmResponse.getResults()[0].getError(), is(MissingRegistration.httpMessage));
        assertThat(gcmResponse.getFailure(),is(1));
        assertThat(gcmResponse.getSuccess(),is(0));
        assertThat(gcmResponse.getCanonical_ids(),is(0));
        assertThat(gcmResponse.getMulticast_id() > 1L, is(true));
    }
}
