
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Phase 1 - stellar account creation failed
 * 
 */
public class StellarAccountCreationFailed implements Event {
    // Augmented by script
    public static StellarAccountCreationFailed create(String errorReason) {
        return new StellarAccountCreationFailed(EventName.STELLAR_ACCOUNT_CREATION_FAILED, Store.common, Store.user, errorReason);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private StellarAccountCreationFailed.EventName eventName;
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
    @SerializedName("error_reason")
    @Expose
    private String errorReason;

    /**
     * No args constructor for use in serialization
     * 
     */
    public StellarAccountCreationFailed() {
    }

    /**
     * 
     * @param common
     * @param errorReason
     * @param eventName
     * @param user
     */
    public StellarAccountCreationFailed(StellarAccountCreationFailed.EventName eventName, Common common, User user, String errorReason) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.errorReason = errorReason;
    }

    /**
     * 
     * (Required)
     * 
     */
    public StellarAccountCreationFailed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(StellarAccountCreationFailed.EventName eventName) {
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
    public String getErrorReason() {
        return errorReason;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public enum EventName {

        @SerializedName("stellar_account_creation_failed")
        STELLAR_ACCOUNT_CREATION_FAILED("stellar_account_creation_failed");
        private final String value;
        private final static Map<String, StellarAccountCreationFailed.EventName> CONSTANTS = new HashMap<String, StellarAccountCreationFailed.EventName>();

        static {
            for (StellarAccountCreationFailed.EventName c: values()) {
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

        public static StellarAccountCreationFailed.EventName fromValue(String value) {
            StellarAccountCreationFailed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
