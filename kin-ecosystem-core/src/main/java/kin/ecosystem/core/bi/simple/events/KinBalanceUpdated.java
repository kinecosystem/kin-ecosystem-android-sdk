
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Kin balance updated successfully due to a newly confirmed transaction
 * 
 */
public class KinBalanceUpdated implements Event {
    // Augmented by script
    public static KinBalanceUpdated create(Double previousBalance) {
        return new KinBalanceUpdated(EventName.KIN_BALANCE_UPDATED, Store.common, Store.user, previousBalance);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private KinBalanceUpdated.EventName eventName;
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
     * 
     * (Required)
     * 
     */
    @SerializedName("previous_balance")
    @Expose
    private Double previousBalance;

    /**
     * No args constructor for use in serialization
     * 
     */
    public KinBalanceUpdated() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     * @param previousBalance
     */
    public KinBalanceUpdated(KinBalanceUpdated.EventName eventName, Common common, User user, Double previousBalance) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.previousBalance = previousBalance;
    }

    /**
     * 
     * (Required)
     * 
     */
    public KinBalanceUpdated.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(KinBalanceUpdated.EventName eventName) {
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

    /**
     * 
     * (Required)
     * 
     */
    public Double getPreviousBalance() {
        return previousBalance;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPreviousBalance(Double previousBalance) {
        this.previousBalance = previousBalance;
    }

    public enum EventName {

        @SerializedName("kin_balance_updated")
        KIN_BALANCE_UPDATED("kin_balance_updated");
        private final String value;
        private final static Map<String, KinBalanceUpdated.EventName> CONSTANTS = new HashMap<String, KinBalanceUpdated.EventName>();

        static {
            for (KinBalanceUpdated.EventName c: values()) {
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

        public static KinBalanceUpdated.EventName fromValue(String value) {
            KinBalanceUpdated.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
