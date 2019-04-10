
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User exit on boarding page by tapping on background app or  X  or android navigator
 * 
 */
public class PopupMessageCloseTapped implements Event {
    public static final String EVENT_NAME = "popup_message_close_tapped";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static PopupMessageCloseTapped create(PopupMessageCloseTapped.ExitType exitType) {
        return new PopupMessageCloseTapped(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            exitType);
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
    @SerializedName("exit_type")
    @Expose
    private PopupMessageCloseTapped.ExitType exitType;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PopupMessageCloseTapped() {
    }

    /**
     * 
     * @param exitType
     * @param common

     * @param client

     * @param user
     */
    public PopupMessageCloseTapped(Common common, User user, Client client, PopupMessageCloseTapped.ExitType exitType) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.exitType = exitType;
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
    public PopupMessageCloseTapped.ExitType getExitType() {
        return exitType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setExitType(PopupMessageCloseTapped.ExitType exitType) {
        this.exitType = exitType;
    }

    public enum ExitType {

        @SerializedName("backround_app")
        BACKROUND_APP("backround_app"),
        @SerializedName("X_button")
        X_BUTTON("X_button"),
        @SerializedName("Android_navigator")
        ANDROID_NAVIGATOR("Android_navigator");
        private final String value;
        private final static Map<String, PopupMessageCloseTapped.ExitType> CONSTANTS = new HashMap<String, PopupMessageCloseTapped.ExitType>();

        static {
            for (PopupMessageCloseTapped.ExitType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private ExitType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static PopupMessageCloseTapped.ExitType fromValue(String value) {
            PopupMessageCloseTapped.ExitType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
