
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User views toast_messages New UI
 * 
 */
public class PopupMessageViewed implements Event {
    public static final String EVENT_NAME = "popup_message_viewed";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static PopupMessageViewed create(PopupMessageViewed.MessageType messageType) {
        return new PopupMessageViewed(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            messageType);
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
    @SerializedName("message_type")
    @Expose
    private PopupMessageViewed.MessageType messageType;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PopupMessageViewed() {
    }

    /**
     * 
     * @param common
     * @param messageType

     * @param client

     * @param user
     */
    public PopupMessageViewed(Common common, User user, Client client, PopupMessageViewed.MessageType messageType) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.messageType = messageType;
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
    public PopupMessageViewed.MessageType getMessageType() {
        return messageType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setMessageType(PopupMessageViewed.MessageType messageType) {
        this.messageType = messageType;
    }

    public enum MessageType {

        @SerializedName("no_wallet ")
        NO_WALLET("no_wallet "),
        @SerializedName("error ")
        ERROR("error "),
        @SerializedName("earn_confirmation ")
        EARN_CONFIRMATION("earn_confirmation "),
        @SerializedName("generic_native ")
        GENERIC_NATIVE("generic_native "),
        @SerializedName("what_is_kin")
        WHAT_IS_KIN("what_is_kin"),
        @SerializedName("spend_confirmation")
        SPEND_CONFIRMATION("spend_confirmation");
        private final String value;
        private final static Map<String, PopupMessageViewed.MessageType> CONSTANTS = new HashMap<String, PopupMessageViewed.MessageType>();

        static {
            for (PopupMessageViewed.MessageType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private MessageType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static PopupMessageViewed.MessageType fromValue(String value) {
            PopupMessageViewed.MessageType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
