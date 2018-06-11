package com.kin.ecosystem.bi;

import static kin.ecosystem.core.network.ApiClient.APPLICATION_JSON_KEY;
import static kin.ecosystem.core.network.ApiClient.POST;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kin.ecosystem.core.network.ApiCallback;
import kin.ecosystem.core.network.ApiClient;
import kin.ecosystem.core.network.ApiException;
import kin.ecosystem.core.network.Pair;
import okhttp3.Call;

class EventsApi {

    private static final String basePath = "https://kin-bi.appspot.com";
    private ApiClient apiClient;

    /*
     * Constructor for EventsApi
     */
    EventsApi() {
        apiClient = new ApiClient();
        apiClient.setBasePath(basePath);
    }

    /**
     * Build call for sendEvent
     *
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call sendEventCall(Event event) throws ApiException {
        Object localVarPostBody = event;

        // create path and map variables
        String localVarPath = "/eco_";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        final String eventId = apiClient.parameterToString(getUniqueID());
        localVarHeaderParams.put("X-REQUEST-ID", eventId);

        event.getCommon().setEventId(eventId);
        event.getCommon().setTimestamp((double) System.currentTimeMillis());

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        String[] applicationJson = {APPLICATION_JSON_KEY};
        final String localVarAccept = apiClient.selectHeaderAccept(applicationJson);
        localVarHeaderParams.put("Accept", localVarAccept);

        final String localVarContentType = apiClient.selectHeaderContentType(applicationJson);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        return apiClient
            .buildCall(localVarPath, POST, localVarQueryParams, localVarCollectionQueryParams, localVarPostBody,
                localVarHeaderParams, localVarFormParams, null, null);
    }

    private Object getUniqueID() {
        return UUID.randomUUID();
    }

    @SuppressWarnings("rawtypes")
    private Call sendEventValidateBeforeCall(Event event) throws ApiException {
        Call call = sendEventCall(event);
        return call;
    }

    /**
     * Send event to BI
     *
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call sendEventAsync(Event event, final ApiCallback<String> callback)
        throws ApiException {
        Call call = sendEventValidateBeforeCall(event);
        Type localVarReturnType = new TypeToken<String>() {}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
