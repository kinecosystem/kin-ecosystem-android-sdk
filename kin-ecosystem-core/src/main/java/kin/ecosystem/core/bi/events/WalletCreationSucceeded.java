
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
 * Stellar account/wallet successfully created for the user
 * 
 */
public class WalletCreationSucceeded implements Event {
    // Augmented by script
    public static WalletCreationSucceeded create() {
        return new WalletCreationSucceeded(
            EventName.WALLET_CREATION_SUCCEEDED,
            (Common) EventsStore.common(),
            (User) EventsStore.user());

    }

    // Augmented by script
    public static void fire() {
        final WalletCreationSucceeded event = new WalletCreationSucceeded(
            EventName.WALLET_CREATION_SUCCEEDED,
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
    private WalletCreationSucceeded.EventName eventName;
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
    public WalletCreationSucceeded() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public WalletCreationSucceeded(WalletCreationSucceeded.EventName eventName, Common common, User user) {
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
    public WalletCreationSucceeded.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(WalletCreationSucceeded.EventName eventName) {
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

        @SerializedName("wallet_creation_succeeded")
        WALLET_CREATION_SUCCEEDED("wallet_creation_succeeded");
        private final String value;
        private final static Map<String, WalletCreationSucceeded.EventName> CONSTANTS = new HashMap<String, WalletCreationSucceeded.EventName>();

        static {
            for (WalletCreationSucceeded.EventName c: values()) {
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

        public static WalletCreationSucceeded.EventName fromValue(String value) {
            WalletCreationSucceeded.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
