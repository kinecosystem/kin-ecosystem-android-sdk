
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.simple.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Users exists the marketplace
 * 
 */
public class CloseButtonOnMarketplacePageTapped implements Event {
    // Augmented by script
    public static CloseButtonOnMarketplacePageTapped create() {
        return new CloseButtonOnMarketplacePageTapped(EventName.CLOSE_BUTTON_ON_MARKETPLACE_PAGE_TAPPED, Store.common, Store.user);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private CloseButtonOnMarketplacePageTapped.EventName eventName;
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
    public CloseButtonOnMarketplacePageTapped() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public CloseButtonOnMarketplacePageTapped(CloseButtonOnMarketplacePageTapped.EventName eventName, Common common, User user) {
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
    public CloseButtonOnMarketplacePageTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(CloseButtonOnMarketplacePageTapped.EventName eventName) {
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

        @SerializedName("close_button_on_marketplace_page_tapped")
        CLOSE_BUTTON_ON_MARKETPLACE_PAGE_TAPPED("close_button_on_marketplace_page_tapped");
        private final String value;
        private final static Map<String, CloseButtonOnMarketplacePageTapped.EventName> CONSTANTS = new HashMap<String, CloseButtonOnMarketplacePageTapped.EventName>();

        static {
            for (CloseButtonOnMarketplacePageTapped.EventName c: values()) {
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

        public static CloseButtonOnMarketplacePageTapped.EventName fromValue(String value) {
            CloseButtonOnMarketplacePageTapped.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
