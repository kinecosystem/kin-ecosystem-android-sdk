
package kin.ecosystem.core.bi.events;

// Augmented by script
import kin.ecosystem.core.bi.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User taps the link of "how to redeem"
 * 
 */
public class RedeemUrlTapped {
    // Augmented by script
    public static RedeemUrlTapped create() {
        return new RedeemUrlTapped(EventName.REDEEM_URL_TAPPED, Store.common, Store.user);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private RedeemUrlTapped.EventName eventName;
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
    public RedeemUrlTapped() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public RedeemUrlTapped(RedeemUrlTapped.EventName eventName, Common common, User user) {
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
    public RedeemUrlTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(RedeemUrlTapped.EventName eventName) {
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

        @SerializedName("redeem_url_tapped")
        REDEEM_URL_TAPPED("redeem_url_tapped");
        private final String value;
        private final static Map<String, RedeemUrlTapped.EventName> CONSTANTS = new HashMap<String, RedeemUrlTapped.EventName>();

        static {
            for (RedeemUrlTapped.EventName c: values()) {
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

        public static RedeemUrlTapped.EventName fromValue(String value) {
            RedeemUrlTapped.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
