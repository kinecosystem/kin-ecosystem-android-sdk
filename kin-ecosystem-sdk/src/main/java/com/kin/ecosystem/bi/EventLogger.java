package com.kin.ecosystem.bi;

public interface EventLogger {

    /**
     * Send event to the BI servers,
     *
     * @param event - the variables should be annotated with {@link com.google.gson.annotations.SerializedName} to
     * ensure correct serialization.
     */
    void send(Event event);
}
