
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User exit on page in new UI
 * 
 */
public class PageCloseTapped implements Event {
    public static final String EVENT_NAME = "page_close_tapped";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static PageCloseTapped create(PageCloseTapped.ExitType exitType, PageCloseTapped.PageName pageName) {
        return new PageCloseTapped(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            exitType,
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
    @SerializedName("exit_type")
    @Expose
    private PageCloseTapped.ExitType exitType;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("page_name")
    @Expose
    private PageCloseTapped.PageName pageName;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PageCloseTapped() {
    }

    /**
     * 
     * @param exitType
     * @param common

     * @param client

     * @param user
     * @param pageName
     */
    public PageCloseTapped(Common common, User user, Client client, PageCloseTapped.ExitType exitType, PageCloseTapped.PageName pageName) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.exitType = exitType;
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
    public PageCloseTapped.ExitType getExitType() {
        return exitType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setExitType(PageCloseTapped.ExitType exitType) {
        this.exitType = exitType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public PageCloseTapped.PageName getPageName() {
        return pageName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPageName(PageCloseTapped.PageName pageName) {
        this.pageName = pageName;
    }

    public enum ExitType {

        @SerializedName("background_app")
        BACKGROUND_APP("background_app"),
        @SerializedName("X_button")
        X_BUTTON("X_button"),
        @SerializedName("Android_navigator")
        ANDROID_NAVIGATOR("Android_navigator"),
        @SerializedName("host_app")
        HOST_APP("host_app");
        private final String value;
        private final static Map<String, PageCloseTapped.ExitType> CONSTANTS = new HashMap<String, PageCloseTapped.ExitType>();

        static {
            for (PageCloseTapped.ExitType c: values()) {
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

        public static PageCloseTapped.ExitType fromValue(String value) {
            PageCloseTapped.ExitType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum PageName {

        @SerializedName("onboarding")
        ONBOARDING("onboarding"),
        @SerializedName("main_page")
        MAIN_PAGE("main_page"),
        @SerializedName("my_kin_page")
        MY_KIN_PAGE("my_kin_page"),
        @SerializedName("settings ")
        SETTINGS("settings "),
        @SerializedName("dialogs_not_enough_kin")
        DIALOGS_NOT_ENOUGH_KIN("dialogs_not_enough_kin"),
        @SerializedName("dialogs_spend_confirmation_screen")
        DIALOGS_SPEND_CONFIRMATION_SCREEN("dialogs_spend_confirmation_screen"),
        @SerializedName("gifting_dialog")
        GIFTING_DIALOG("gifting_dialog");
        private final String value;
        private final static Map<String, PageCloseTapped.PageName> CONSTANTS = new HashMap<String, PageCloseTapped.PageName>();

        static {
            for (PageCloseTapped.PageName c: values()) {
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

        public static PageCloseTapped.PageName fromValue(String value) {
            PageCloseTapped.PageName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
