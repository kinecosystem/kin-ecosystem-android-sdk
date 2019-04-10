
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User views page in new UI
 * 
 */
public class APageViewed implements Event {
    public static final String EVENT_NAME = "a_page_viewed";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static APageViewed create(APageViewed.PageName pageName) {
        return new APageViewed(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            pageName);
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
    @SerializedName("page_name")
    @Expose
    private APageViewed.PageName pageName;

    /**
     * No args constructor for use in serialization
     * 
     */
    public APageViewed() {
    }

    /**
     * 
     * @param common

     * @param client

     * @param user
     * @param pageName
     */
    public APageViewed(Common common, User user, Client client, APageViewed.PageName pageName) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.pageName = pageName;
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
    public APageViewed.PageName getPageName() {
        return pageName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPageName(APageViewed.PageName pageName) {
        this.pageName = pageName;
    }

    public enum PageName {

        @SerializedName("onboarding")
        ONBOARDING("onboarding"),
        @SerializedName("my_kin_page")
        MY_KIN_PAGE("my_kin_page"),
        @SerializedName("settings ")
        SETTINGS("settings "),
        @SerializedName("dialogs_not_enough_kin")
        DIALOGS_NOT_ENOUGH_KIN("dialogs_not_enough_kin"),
        @SerializedName("dialogs_spend_confirmation_screen")
        DIALOGS_SPEND_CONFIRMATION_SCREEN("dialogs_spend_confirmation_screen");
        private final String value;
        private final static Map<String, APageViewed.PageName> CONSTANTS = new HashMap<String, APageViewed.PageName>();

        static {
            for (APageViewed.PageName c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private PageName(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static APageViewed.PageName fromValue(String value) {
            APageViewed.PageName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
