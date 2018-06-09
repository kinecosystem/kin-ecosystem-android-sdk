
package kin.ecosystem.core.bi.threaded.events;

// Augmented by script
import kin.ecosystem.core.bi.threaded.Event;
import kin.ecosystem.core.bi.threaded.EventLoggerImpl;
import kin.ecosystem.core.bi.threaded.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Welcome screen page opened
 * 
 */
public class WelcomeScreenPageViewed implements Event {
    // Augmented by script
    public static WelcomeScreenPageViewed create() {
        return new WelcomeScreenPageViewed(
            EventName.WELCOME_SCREEN_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final WelcomeScreenPageViewed event = new WelcomeScreenPageViewed(
            EventName.WELCOME_SCREEN_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

        EventLoggerImpl.Send(event);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private WelcomeScreenPageViewed.EventName eventName;
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
    public WelcomeScreenPageViewed() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public WelcomeScreenPageViewed(WelcomeScreenPageViewed.EventName eventName, Common common, User user) {
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
    public WelcomeScreenPageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(WelcomeScreenPageViewed.EventName eventName) {
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

        @SerializedName("welcome_screen_page_viewed")
        WELCOME_SCREEN_PAGE_VIEWED("welcome_screen_page_viewed");
        private final String value;
        private final static Map<String, WelcomeScreenPageViewed.EventName> CONSTANTS = new HashMap<String, WelcomeScreenPageViewed.EventName>();

        static {
            for (WelcomeScreenPageViewed.EventName c: values()) {
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

        public static WelcomeScreenPageViewed.EventName fromValue(String value) {
            WelcomeScreenPageViewed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
