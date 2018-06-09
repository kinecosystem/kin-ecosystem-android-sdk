
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
 * Users starts an earn offer / Client requests OrderID for earn order
 * 
 */
public class EarnOrderCreationRequested implements Event {
    // Augmented by script
    public static EarnOrderCreationRequested create(EarnOrderCreationRequested.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        return new EarnOrderCreationRequested(
            EventName.EARN_ORDER_CREATION_REQUESTED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            offerType,
            kinAmount,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(EarnOrderCreationRequested.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        final EarnOrderCreationRequested event = new EarnOrderCreationRequested(
            EventName.EARN_ORDER_CREATION_REQUESTED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            offerType,
            kinAmount,
            offerId,
            orderId);

        EventLoggerImpl.Send(event);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private EarnOrderCreationRequested.EventName eventName;
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
    @SerializedName("offer_type")
    @Expose
    private EarnOrderCreationRequested.OfferType offerType;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("kin_amount")
    @Expose
    private Double kinAmount;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("offer_id")
    @Expose
    private String offerId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("order_id")
    @Expose
    private String orderId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public EarnOrderCreationRequested() {
    }

    /**
     * 
     * @param offerType
     * @param common
     * @param orderId
     * @param eventName
     * @param offerId
     * @param kinAmount
     * @param user
     */
    public EarnOrderCreationRequested(EarnOrderCreationRequested.EventName eventName, Common common, User user, EarnOrderCreationRequested.OfferType offerType, Double kinAmount, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.offerType = offerType;
        this.kinAmount = kinAmount;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public EarnOrderCreationRequested.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(EarnOrderCreationRequested.EventName eventName) {
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
    public EarnOrderCreationRequested.OfferType getOfferType() {
        return offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferType(EarnOrderCreationRequested.OfferType offerType) {
        this.offerType = offerType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Double getKinAmount() {
        return kinAmount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setKinAmount(Double kinAmount) {
        this.kinAmount = kinAmount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public enum EventName {

        @SerializedName("earn_order_creation_requested")
        EARN_ORDER_CREATION_REQUESTED("earn_order_creation_requested");
        private final String value;
        private final static Map<String, EarnOrderCreationRequested.EventName> CONSTANTS = new HashMap<String, EarnOrderCreationRequested.EventName>();

        static {
            for (EarnOrderCreationRequested.EventName c: values()) {
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

        public static EarnOrderCreationRequested.EventName fromValue(String value) {
            EarnOrderCreationRequested.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum OfferType {

        @SerializedName("Video")
        VIDEO("Video"),
        @SerializedName("Poll")
        POLL("Poll"),
        @SerializedName("code purchase")
        CODE_PURCHASE("code purchase"),
        @SerializedName("Tutorial")
        TUTORIAL("Tutorial");
        private final String value;
        private final static Map<String, EarnOrderCreationRequested.OfferType> CONSTANTS = new HashMap<String, EarnOrderCreationRequested.OfferType>();

        static {
            for (EarnOrderCreationRequested.OfferType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private OfferType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static EarnOrderCreationRequested.OfferType fromValue(String value) {
            EarnOrderCreationRequested.OfferType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
