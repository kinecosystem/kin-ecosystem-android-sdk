
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.simple.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * DS initiated the SDK
 * 
 */
public class KinSdkInitiated implements Event {
    // Augmented by script
    public static KinSdkInitiated create() {
        return new KinSdkInitiated(EventName.KIN_SDK_INITIATED, Store.common, Store.user);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private KinSdkInitiated.EventName eventName;
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
    public KinSdkInitiated() {
    }

    /**
     * 
     * @param common
     * @param eventName
     * @param user
     */
    public KinSdkInitiated(KinSdkInitiated.EventName eventName, Common common, User user) {
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
    public KinSdkInitiated.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(KinSdkInitiated.EventName eventName) {
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

        @SerializedName("kin_sdk_initiated")
        KIN_SDK_INITIATED("kin_sdk_initiated");
        private final String value;
        private final static Map<String, KinSdkInitiated.EventName> CONSTANTS = new HashMap<String, KinSdkInitiated.EventName>();

        static {
            for (KinSdkInitiated.EventName c: values()) {
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

        public static KinSdkInitiated.EventName fromValue(String value) {
            KinSdkInitiated.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
