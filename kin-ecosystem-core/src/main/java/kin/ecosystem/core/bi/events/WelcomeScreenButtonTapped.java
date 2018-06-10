
package kin.ecosystem.core.bi.events;

// Augmented by script
import kin.ecosystem.core.bi.Event;
import kin.ecosystem.core.bi.EventLoggerImpl;
import kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Users taps "Let's get started"
 * 
 */
public class WelcomeScreenButtonTapped implements Event {
    // Augmented by script
    public static WelcomeScreenButtonTapped create() {
        return new WelcomeScreenButtonTapped(
            EventName.WELCOME_SCREEN_BUTTON_TAPPED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final WelcomeScreenButtonTapped event = new WelcomeScreenButtonTapped(
            EventName.WELCOME_SCREEN_BUTTON_TAPPED,
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
    private WelcomeScreenButtonTapped.EventName eventName;
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
    public WelcomeScreenButtonTapped() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public WelcomeScreenButtonTapped(WelcomeScreenButtonTapped.EventName eventName, Common common, User user) {
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
    public WelcomeScreenButtonTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(WelcomeScreenButtonTapped.EventName eventName) {
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

        @SerializedName("welcome_screen_button_tapped")
        WELCOME_SCREEN_BUTTON_TAPPED("welcome_screen_button_tapped");
        private final String value;
        private final static Map<String, WelcomeScreenButtonTapped.EventName> CONSTANTS = new HashMap<String, WelcomeScreenButtonTapped.EventName>();

        static {
            for (WelcomeScreenButtonTapped.EventName c: values()) {
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

        public static WelcomeScreenButtonTapped.EventName fromValue(String value) {
            WelcomeScreenButtonTapped.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
