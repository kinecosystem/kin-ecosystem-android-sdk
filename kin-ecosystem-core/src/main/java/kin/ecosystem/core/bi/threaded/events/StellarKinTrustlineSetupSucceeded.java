
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
 * Phase 2 - stellar trustline setup succeeded
 * 
 */
public class StellarKinTrustlineSetupSucceeded implements Event {
    // Augmented by script
    public static StellarKinTrustlineSetupSucceeded create() {
        return new StellarKinTrustlineSetupSucceeded(
            EventName.STELLAR_KIN_TRUSTLINE_SETUP_SUCCEEDED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final StellarKinTrustlineSetupSucceeded event = new StellarKinTrustlineSetupSucceeded(
            EventName.STELLAR_KIN_TRUSTLINE_SETUP_SUCCEEDED,
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
    private StellarKinTrustlineSetupSucceeded.EventName eventName;
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
    public StellarKinTrustlineSetupSucceeded() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public StellarKinTrustlineSetupSucceeded(StellarKinTrustlineSetupSucceeded.EventName eventName, Common common, User user) {
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
    public StellarKinTrustlineSetupSucceeded.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(StellarKinTrustlineSetupSucceeded.EventName eventName) {
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

        @SerializedName("stellar_kin_trustline_setup_succeeded")
        STELLAR_KIN_TRUSTLINE_SETUP_SUCCEEDED("stellar_kin_trustline_setup_succeeded");
        private final String value;
        private final static Map<String, StellarKinTrustlineSetupSucceeded.EventName> CONSTANTS = new HashMap<String, StellarKinTrustlineSetupSucceeded.EventName>();

        static {
            for (StellarKinTrustlineSetupSucceeded.EventName c: values()) {
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

        public static StellarKinTrustlineSetupSucceeded.EventName fromValue(String value) {
            StellarKinTrustlineSetupSucceeded.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
