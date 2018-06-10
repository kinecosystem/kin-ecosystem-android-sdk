
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
 * Phase 1 - stellar account creation succeeded
 * 
 */
public class StellarAccountCreationSucceeded implements Event {
    // Augmented by script
    public static StellarAccountCreationSucceeded create() {
        return new StellarAccountCreationSucceeded(
            EventName.STELLAR_ACCOUNT_CREATION_SUCCEEDED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final StellarAccountCreationSucceeded event = new StellarAccountCreationSucceeded(
            EventName.STELLAR_ACCOUNT_CREATION_SUCCEEDED,
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
    private StellarAccountCreationSucceeded.EventName eventName;
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
    public StellarAccountCreationSucceeded() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public StellarAccountCreationSucceeded(StellarAccountCreationSucceeded.EventName eventName, Common common, User user) {
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
    public StellarAccountCreationSucceeded.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(StellarAccountCreationSucceeded.EventName eventName) {
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

        @SerializedName("stellar_account_creation_succeeded")
        STELLAR_ACCOUNT_CREATION_SUCCEEDED("stellar_account_creation_succeeded");
        private final String value;
        private final static Map<String, StellarAccountCreationSucceeded.EventName> CONSTANTS = new HashMap<String, StellarAccountCreationSucceeded.EventName>();

        static {
            for (StellarAccountCreationSucceeded.EventName c: values()) {
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

        public static StellarAccountCreationSucceeded.EventName fromValue(String value) {
            StellarAccountCreationSucceeded.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
