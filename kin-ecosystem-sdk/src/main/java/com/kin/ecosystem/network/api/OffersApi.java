package com.kin.ecosystem.network.api;

import com.google.gson.reflect.TypeToken;
import com.kin.ecosystem.network.Configuration;
import kin.ecosystem.core.network.ApiCallback;
import kin.ecosystem.core.network.ApiClient;
import kin.ecosystem.core.network.ApiException;
import kin.ecosystem.core.network.ApiResponse;
import kin.ecosystem.core.network.Pair;
import kin.ecosystem.core.network.ProgressRequestBody;
import kin.ecosystem.core.network.ProgressResponseBody;
import com.kin.ecosystem.network.model.OfferList;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Response;


public class OffersApi {
    private ApiClient apiClient;

    public OffersApi() {
        this(Configuration.getApiClient());
    }

    public OffersApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }


    /**
     * Build call for getOffers
     *
     * @param X_REQUEST_ID            A unique id for the request. A retransmitted request will have the same id  (required)
     * @param limit                   maximum number of items in a list (optional)
     * @param after                   cursor that points to the end of the page of data that has been returned (optional)
     * @param before                  cursor that points to the start of the page of data that has been returned (optional)
     * @param progressListener        Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getOffersCall(String X_REQUEST_ID, Integer limit, String after, String before, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/offers";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (limit != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("limit", limit));
        if (after != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("after", after));
        if (before != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("before", before));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        if (X_REQUEST_ID != null)
            localVarHeaderParams.put("X-REQUEST-ID", apiClient.parameterToString(X_REQUEST_ID));

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
                "application/json", "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if (progressListener != null) {
            apiClient.addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }

        String[] localVarAuthNames = new String[]{};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private Call getOffersValidateBeforeCall(String X_REQUEST_ID, Integer limit, String after, String before, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {


        // verify the required parameter 'X_REQUEST_ID' is set
        if (X_REQUEST_ID == null) {
            throw new ApiException("Missing the required parameter 'X_REQUEST_ID' when calling getOffers(Async)");
        }


        Call call = getOffersCall(X_REQUEST_ID, limit, after, before, progressListener, progressRequestListener);
        return call;


    }

    /**
     * Return a list of offers
     * Return a **list** of offers
     *
     * @param X_REQUEST_ID A unique id for the request. A retransmitted request will have the same id  (required)
     * @param limit        maximum number of items in a list (optional)
     * @param after        cursor that points to the end of the page of data that has been returned (optional)
     * @param before       cursor that points to the start of the page of data that has been returned (optional)
     * @return OfferList
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public OfferList getOffers(String X_REQUEST_ID, Integer limit, String after, String before) throws ApiException {
        ApiResponse<OfferList> resp = getOffersWithHttpInfo(X_REQUEST_ID, limit, after, before);
        return resp.getData();
    }

    /**
     * Return a list of offers
     * Return a **list** of offers
     *
     * @param X_REQUEST_ID A unique id for the request. A retransmitted request will have the same id  (required)
     * @param limit        maximum number of items in a list (optional)
     * @param after        cursor that points to the end of the page of data that has been returned (optional)
     * @param before       cursor that points to the start of the page of data that has been returned (optional)
     * @return ApiResponse&lt;OfferList&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<OfferList> getOffersWithHttpInfo(String X_REQUEST_ID, Integer limit, String after, String before) throws ApiException {
        Call call = getOffersValidateBeforeCall(X_REQUEST_ID, limit, after, before, null, null);
        Type localVarReturnType = new TypeToken<OfferList>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Return a list of offers (asynchronously)
     * Return a **list** of offers
     *
     * @param X_REQUEST_ID A unique id for the request. A retransmitted request will have the same id  (required)
     * @param limit        maximum number of items in a list (optional)
     * @param after        cursor that points to the end of the page of data that has been returned (optional)
     * @param before       cursor that points to the start of the page of data that has been returned (optional)
     * @param callback     The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call getOffersAsync(String X_REQUEST_ID, Integer limit, String after, String before, final ApiCallback<OfferList> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        Call call = getOffersValidateBeforeCall(X_REQUEST_ID, limit, after, before, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<OfferList>() {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

}
