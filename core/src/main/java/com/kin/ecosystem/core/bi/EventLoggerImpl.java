package com.kin.ecosystem.core.bi;

import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
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
//        try {
//            eventsApi.sendEventAsync(event, null);
//        } catch (ApiException e) {
//            Logger.log(new Log().withTag("EventLoggerImpl").text(e.getMessage()).priority(Log.ERROR));
//        }
    }
}
