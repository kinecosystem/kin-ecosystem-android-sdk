
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
 * Users views the main marketplace page
 * 
 */
public class MarketplacePageViewed implements Event {
    // Augmented by script
    public static MarketplacePageViewed create() {
        return new MarketplacePageViewed(
            EventName.MARKETPLACE_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final MarketplacePageViewed event = new MarketplacePageViewed(
            EventName.MARKETPLACE_PAGE_VIEWED,
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
    private MarketplacePageViewed.EventName eventName;
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
    public MarketplacePageViewed() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public MarketplacePageViewed(MarketplacePageViewed.EventName eventName, Common common, User user) {
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
    public MarketplacePageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(MarketplacePageViewed.EventName eventName) {
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

        @SerializedName("marketplace_page_viewed")
        MARKETPLACE_PAGE_VIEWED("marketplace_page_viewed");
        private final String value;
        private final static Map<String, MarketplacePageViewed.EventName> CONSTANTS = new HashMap<String, MarketplacePageViewed.EventName>();

        static {
            for (MarketplacePageViewed.EventName c: values()) {
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

        public static MarketplacePageViewed.EventName fromValue(String value) {
            MarketplacePageViewed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
