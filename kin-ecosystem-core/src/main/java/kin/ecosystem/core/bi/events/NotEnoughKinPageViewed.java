
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
 * User views the not enough kin page
 * 
 */
public class NotEnoughKinPageViewed implements Event {
    // Augmented by script
    public static NotEnoughKinPageViewed create() {
        return new NotEnoughKinPageViewed(
            EventName.NOT_ENOUGH_KIN_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final NotEnoughKinPageViewed event = new NotEnoughKinPageViewed(
            EventName.NOT_ENOUGH_KIN_PAGE_VIEWED,
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
    private NotEnoughKinPageViewed.EventName eventName;
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
    public NotEnoughKinPageViewed() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public NotEnoughKinPageViewed(NotEnoughKinPageViewed.EventName eventName, Common common, User user) {
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
    public NotEnoughKinPageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(NotEnoughKinPageViewed.EventName eventName) {
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

        @SerializedName("not_enough_kin_page_viewed")
        NOT_ENOUGH_KIN_PAGE_VIEWED("not_enough_kin_page_viewed");
        private final String value;
        private final static Map<String, NotEnoughKinPageViewed.EventName> CONSTANTS = new HashMap<String, NotEnoughKinPageViewed.EventName>();

        static {
            for (NotEnoughKinPageViewed.EventName c: values()) {
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

        public static NotEnoughKinPageViewed.EventName fromValue(String value) {
            NotEnoughKinPageViewed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
