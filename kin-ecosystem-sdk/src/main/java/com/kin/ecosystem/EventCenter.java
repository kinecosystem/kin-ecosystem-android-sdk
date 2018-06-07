package com.kin.ecosystem;

import com.kin.ecosystem.event.BlockchainSdkError;
import kin.ecosystem.bi.EventLogger;

public class EventCenter {

    private final EventLogger eventLogger;
//    private BaseEventProperties baseEventProperties;

    public EventCenter(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    public void blockchainSdkError(String error) {
        //TODO create the object and send
        // eventLogger.send();
    }
}
