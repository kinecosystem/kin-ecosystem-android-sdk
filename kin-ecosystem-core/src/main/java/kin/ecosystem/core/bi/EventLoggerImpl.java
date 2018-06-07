package kin.ecosystem.core.bi;

import kin.ecosystem.core.network.ApiException;

public class EventLoggerImpl implements EventLogger  {

    private static EventLoggerImpl instance;

    private final EventsApi eventsApi;

    private EventLoggerImpl(EventsApi eventsApi) {
        this.eventsApi = eventsApi;
    }

    public static EventLoggerImpl init() {
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
    public void send(Object event) {
        try {
            eventsApi.sendEventAsync(event, null);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
