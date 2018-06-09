
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
 * User taps on balance and goes to the order history page
 * 
 */
public class BalanceTapped implements Event {
    // Augmented by script
    public static BalanceTapped create() {
        return new BalanceTapped(
            EventName.BALANCE_TAPPED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final BalanceTapped event = new BalanceTapped(
            EventName.BALANCE_TAPPED,
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
    private BalanceTapped.EventName eventName;
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
    public BalanceTapped() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public BalanceTapped(BalanceTapped.EventName eventName, Common common, User user) {
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
    public BalanceTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(BalanceTapped.EventName eventName) {
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

        @SerializedName("balance_tapped")
        BALANCE_TAPPED("balance_tapped");
        private final String value;
        private final static Map<String, BalanceTapped.EventName> CONSTANTS = new HashMap<String, BalanceTapped.EventName>();

        static {
            for (BalanceTapped.EventName c: values()) {
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

        public static BalanceTapped.EventName fromValue(String value) {
            BalanceTapped.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
