
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Main_page  page views in new UI
 * 
 */
public class OffersPageViewed implements Event {
    public static final String EVENT_NAME = "offers_page_viewed";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static OffersPageViewed create(Boolean isEmpty) {
        return new OffersPageViewed(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            isEmpty);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private String eventName = EVENT_NAME;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_type")
    @Expose
    private String eventType = EVENT_TYPE;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("common")
    @Expose
    private Common common;
    /**
     * common user properties
     * (Required)
     * 
     */
    @SerializedName("user")
    @Expose
    private User user;
    /**
     * common properties for all client events
     * (Required)
     * 
     */
    @SerializedName("client")
    @Expose
    private Client client;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("is_empty")
    @Expose
    private Boolean isEmpty;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OffersPageViewed() {
    }

    /**
     * 
     * @param common
     * @param isEmpty

     * @param client

     * @param user
     */
    public OffersPageViewed(Common common, User user, Client client, Boolean isEmpty) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.isEmpty = isEmpty;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public Common getCommon() {
        return common;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public void setCommon(Common common) {
        this.common = common;
    }

    /**
     * common user properties
     * (Required)
     * 
     */
    public User getUser() {
        return user;
    }

    /**
     * common user properties
     * (Required)
     * 
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public Client getClient() {
        return client;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Boolean getIsEmpty() {
        return isEmpty;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setIsEmpty(Boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

}
