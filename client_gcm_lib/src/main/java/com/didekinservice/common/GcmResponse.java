package com.didekinservice.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.didekinservice.common.GcmResponse.GcmErrorMessage.NotRegistered;

/**
 * User: pedro@didekin
 * Date: 30/11/15
 * Time: 18:43
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class GcmResponse {

    /**
     * Unique ID (number) identifying the multicast message.
     */
    private final long multicast_id;

    /**
     * Number of messages that were processed without an error.
     */
    private final int success;

    /**
     * Number of messages that could not be processed.
     */
    private final int failure;

    /**
     * Number of results that contain a canonical registration token.
     */
    private final int canonical_ids;

    /**
     * Array of objects representing the status of the messages processed. The objects are listed in the same order as the request
     * (i.e., for each registration ID in the request, its result is listed in the same index in the response).
     */
    private final Result[] results;

    /**
     * Pair of tokens (old and new one) to modify in the database with FCM tokens IDs.
     */
    private List<GcmTokensHolder> tokensToProcess;

    public GcmResponse(int canonical_ids, long multicast_id, int success, int failure, Result[] results)
    {
        this.canonical_ids = canonical_ids;
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.results = results;
    }

    public int getCanonical_ids()
    {
        return canonical_ids;
    }

    public int getFailure()
    {
        return failure;
    }

    public long getMulticast_id()
    {
        return multicast_id;
    }

    public Result[] getResults()
    {
        return results.clone();
    }

    public int getSuccess()
    {
        return success;
    }

    public List<GcmTokensHolder> getTokensToProcess()
    {
        return tokensToProcess.size() > 0 ? Collections.unmodifiableList(tokensToProcess) : tokensToProcess;
    }

    /**
     * Postconditions:
     * 1. A list of GcmTokenHolders is returned.
     * 2. If the GCM response result includes registration_id, both gcm tokens (old and new) are returned in the holder.
     * 3. If the result includes the error NotRegistered, the original gcm token is returned with a null value in the new token field.
     */
    public void setTokensToProcess(String[] gcmTokens)
    {
        if (gcmTokens.length > 0) {
            tokensToProcess = new ArrayList<>();
            for (int i = 0; i < results.length; i++) {
                if (results[i].getRegistration_id() != null) {
                    tokensToProcess.add(new GcmTokensHolder(results[i].getRegistration_id(), gcmTokens[i]));
                } else {
                    if (results[i].getError() != null && results[i].getError().equals(NotRegistered.httpMessage)) {
                        tokensToProcess.add(new GcmTokensHolder(null, gcmTokens[i]));
                    }
                }
            }
        } else {
            tokensToProcess = Collections.emptyList();
        }
    }

//    ==================================  INNER CLASSES ================================

    public static final class Result {

        /**
         * Message_id: String specifying a unique ID for each successfully processed message.
         */
        private final String message_id;

        /**
         * Registration_id: Optional string specifying the canonical registration token for the client app that the message was processed and sent to.
         * Sender should use this value as the registration token for future requests. Otherwise, the messages might be rejected.
         */
        private final String registration_id;

        /**
         * Error: String specifying the error that occurred when processing the message for the recipient.
         */
        private final String error;

        public Result(String error, String message_id, String registration_id)
        {
            this.error = error;
            this.message_id = message_id;
            this.registration_id = registration_id;
        }

        public String getError()
        {
            return error;
        }

        public String getMessage_id()
        {
            return message_id;
        }

        public String getRegistration_id()
        {
            return registration_id;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public enum GcmErrorMessage {

        /**
         * Action: Check that the request contains a registration token (in the registration_id in a plain text message,
         * or in the to or registration_ids field in JSON).
         */
        MissingRegistration(200, "Missing registration token"),

        /**
         * Action: Check the format of the registration token you pass to the server.
         */
        InvalidRegistration(200, "Invalid registration token"),

        /**
         * An existing registration token may cease to be valid in a number of scenarios, including:
         * - If the client app unregisters with FCM.
         * - If the client app is automatically unregistered, which can happen if the user uninstalls the application.
         * - If the registration token expires (for example, Google might decide to refresh registration tokens).
         * - If the client app is updated but the new version is not configured to receive messages.
         * <p/>
         * Action: For all these cases, remove this registration token from the app server and stop using it to send messages.
         */
        NotRegistered(200, "Unregistered device"),

        /**
         * Action: Make sure the message was addressed to a registration token whose package name matches
         * the value passed in the request.
         */
        InvalidPackageName(200, "Invalid package name"),

        /**
         * Action: A registration token is tied to a certain group of senders. When a client app registers for FCM,
         * it must specify which senders are allowed to send messages. You should use one of those sender IDs when
         * sending messages to the client app. If you switch to a different sender, the existing registration
         * tokens won't work.
         */
        MismatchSenderId(200, "Mismatched sender ID"),

        /**
         * Action: Check that the payload data does not contain a key (such as from, or gcm, or any value prefixed
         * by google) that is used internally by FCM.
         */
        InvalidDataKey(200, "Invalid data key"),

        /**
         * Action: Check that the JSON message is properly formatted and contains valid fields.
         * The error message may contains the name of the wrong field.
         */
        InvalidJson(400, null),

        /**
         * The sender account used to send a message couldn't be authenticated.
         * Possible causes are:
         * - Authorization header missing or with invalid syntax in HTTP request.
         * - Invalid project number sent as key.
         * - Key valid but with FCM service disabled.
         * - Request originated from a server not whitelisted in the Server key IPs.
         * <p/>
         * Action: Check that the token you're sending inside the Authentication header is the correct Server key associated with your project.
         */
        Unauthorized(401, "Authentication error"),

        /**
         * Action: The server couldn't process the request in time.
         * Retry the same request, but you must:
         * - Honor the Retry-After header if it is included in the response from the FCM Connection Server.
         * - Implement exponential back-off in your retry mechanism. (e.g. if you waited one second before the first retry, wait at least
         * two second before the next one, then 4 seconds and so on). If you're sending multiple messages, delay each one independently
         * by an additional random amount to avoid issuing a new request for all messages at the same time.
         * <p/>
         * Senders that cause problems risk being blacklisted.
         */
        Unavailable(500, "Timeout"),  // TODO: implement 'retry-after' with exponential back-off.

        /**
         * Action: The server encountered an error while trying to process the request. You could retry the same
         * request following the requirements listed in "Unavailable"
         */
        InternalServerError(500, "Internal server error"),;

        public final String httpMessage;
        public final int httpStatusCode;
        public final String description;

        GcmErrorMessage(int httpStatusCode, String description)
        {
            httpMessage = this.name();
            this.httpStatusCode = httpStatusCode;
            this.description = description;
        }
    }
}
