package com.kin.ecosystem.core.bi;

import com.kin.ecosystem.core.network.ApiException;
import java.util.List;
import java.util.Map;
import com.kin.ecosystem.core.network.ApiCallback;

public class EventLoggerImpl implements EventLogger {

    private static EventLoggerImpl instance;

    private final EventsApi eventsApi;

    private EventLoggerImpl(EventsApi eventsApi) {
        this.eventsApi = eventsApi;
    }

    public static EventLoggerImpl getInstance() {
        if (instance == null) {
            synchronized (EventLoggerImpl.class) {
                if (instance == null) {
                    instance = new EventLoggerImpl(new EventsApi());
                }
            }
        }
        return  instance;
    }


    @Override
    public void send(Event event) {
        try {
            eventsApi.sendEventAsync(event, new ApiCallback<String>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {

                }

                @Override
                public void onSuccess(String result, int statusCode, Map<String, List<String>> responseHeaders) {

                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
