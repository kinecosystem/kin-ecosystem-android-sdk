
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.simple.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Phase 2 - stellar trustline setup failed
 * 
 */
public class StellarKinTrustlineSetupFailed implements Event {
    // Augmented by script
    public static StellarKinTrustlineSetupFailed create(String errorReason) {
        return new StellarKinTrustlineSetupFailed(EventName.STELLAR_KIN_TRUSTLINE_SETUP_FAILED, Store.common, Store.user, errorReason);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private StellarKinTrustlineSetupFailed.EventName eventName;
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
    public StellarKinTrustlineSetupFailed() {
    }

    /**
     * 
     * @param common
     * @param errorReason
     * @param eventName
     * @param user
     */
    public StellarKinTrustlineSetupFailed(StellarKinTrustlineSetupFailed.EventName eventName, Common common, User user, String errorReason) {
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
    public StellarKinTrustlineSetupFailed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(StellarKinTrustlineSetupFailed.EventName eventName) {
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

        @SerializedName("stellar_kin_trustline_setup_failed")
        STELLAR_KIN_TRUSTLINE_SETUP_FAILED("stellar_kin_trustline_setup_failed");
        private final String value;
        private final static Map<String, StellarKinTrustlineSetupFailed.EventName> CONSTANTS = new HashMap<String, StellarKinTrustlineSetupFailed.EventName>();

        static {
            for (StellarKinTrustlineSetupFailed.EventName c: values()) {
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

        public static StellarKinTrustlineSetupFailed.EventName fromValue(String value) {
            StellarKinTrustlineSetupFailed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
