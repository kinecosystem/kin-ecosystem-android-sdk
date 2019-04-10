
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
public class ErrorMessageCloseTapped implements Event {
    public static final String EVENT_NAME = "error_message_close_tapped";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static ErrorMessageCloseTapped create(ErrorMessageCloseTapped.ExitType exitType) {
        return new ErrorMessageCloseTapped(
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
    private ErrorMessageCloseTapped.ExitType exitType;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ErrorMessageCloseTapped() {
    }

    /**
     * 
     * @param exitType
     * @param common

     * @param client

     * @param user
     */
    public ErrorMessageCloseTapped(Common common, User user, Client client, ErrorMessageCloseTapped.ExitType exitType) {
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
    public ErrorMessageCloseTapped.ExitType getExitType() {
        return exitType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setExitType(ErrorMessageCloseTapped.ExitType exitType) {
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
        private final static Map<String, ErrorMessageCloseTapped.ExitType> CONSTANTS = new HashMap<String, ErrorMessageCloseTapped.ExitType>();

        static {
            for (ErrorMessageCloseTapped.ExitType c: values()) {
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

        public static ErrorMessageCloseTapped.ExitType fromValue(String value) {
            ErrorMessageCloseTapped.ExitType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
