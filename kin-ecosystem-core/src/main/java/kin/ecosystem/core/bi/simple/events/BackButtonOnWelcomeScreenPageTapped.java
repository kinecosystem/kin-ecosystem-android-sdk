
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.simple.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Users close the welcome screen
 * 
 */
public class BackButtonOnWelcomeScreenPageTapped implements Event {
    // Augmented by script
    public static BackButtonOnWelcomeScreenPageTapped create() {
        return new BackButtonOnWelcomeScreenPageTapped(EventName.BACK_BUTTON_ON_WELCOME_SCREEN_PAGE_TAPPED, Store.common, Store.user);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private BackButtonOnWelcomeScreenPageTapped.EventName eventName;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("common")
    @Expose
    private Common common;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("user")
    @Expose
    private User user;

    /**
     * No args constructor for use in serialization
     * 
     */
    public BackButtonOnWelcomeScreenPageTapped() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public BackButtonOnWelcomeScreenPageTapped(BackButtonOnWelcomeScreenPageTapped.EventName eventName, Common common, User user) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
    }

    /**
     * 
     * (Required)
     * 
     */
    public BackButtonOnWelcomeScreenPageTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(BackButtonOnWelcomeScreenPageTapped.EventName eventName) {
        this.eventName = eventName;
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
     * common properties for all events
     * (Required)
     * 
     */
    public User getUser() {
        return user;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public void setUser(User user) {
        this.user = user;
    }

    public enum EventName {

        @SerializedName("back_button_on_welcome_screen_page_tapped")
        BACK_BUTTON_ON_WELCOME_SCREEN_PAGE_TAPPED("back_button_on_welcome_screen_page_tapped");
        private final String value;
        private final static Map<String, BackButtonOnWelcomeScreenPageTapped.EventName> CONSTANTS = new HashMap<String, BackButtonOnWelcomeScreenPageTapped.EventName>();

        static {
            for (BackButtonOnWelcomeScreenPageTapped.EventName c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private EventName(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static BackButtonOnWelcomeScreenPageTapped.EventName fromValue(String value) {
            BackButtonOnWelcomeScreenPageTapped.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
