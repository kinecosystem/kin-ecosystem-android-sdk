
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * when a user exceeded max wallet limit
 * 
 */
public class MaxWalletsExceededError implements Event {
    public static final String EVENT_NAME = "max_wallets_exceeded_error";
    public static final String EVENT_TYPE = "log";

    // Augmented by script
    public static MaxWalletsExceededError create(String initDate, Object localWalletList) {
        return new MaxWalletsExceededError(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            initDate,
            localWalletList);
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
    @SerializedName("initDate")
    @Expose
    private String initDate;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("localWalletList")
    @Expose
    private Object localWalletList;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MaxWalletsExceededError() {
    }

    /**
     * 
     * @param common
     * @param initDate
     * @param localWalletList

     * @param client

     * @param user
     */
    public MaxWalletsExceededError(Common common, User user, Client client, String initDate, Object localWalletList) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.initDate = initDate;
        this.localWalletList = localWalletList;
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
    public String getInitDate() {
        return initDate;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setInitDate(String initDate) {
        this.initDate = initDate;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Object getLocalWalletList() {
        return localWalletList;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setLocalWalletList(Object localWalletList) {
        this.localWalletList = localWalletList;
    }

}
