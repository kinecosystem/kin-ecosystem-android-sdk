
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
 * User view earn offer page
 * 
 */
public class PollAnswerPageViewed implements Event {
    // Augmented by script
    public static PollAnswerPageViewed create(String questionId, String offerId, String orderId) {
        return new PollAnswerPageViewed(
            EventName.POLL_ANSWER_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            questionId,
            offerId,
            orderId);

    }

    // Augmented by script
    public static void fire(String questionId, String offerId, String orderId) {
        final PollAnswerPageViewed event = new PollAnswerPageViewed(
            EventName.POLL_ANSWER_PAGE_VIEWED,
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            questionId,
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
    private PollAnswerPageViewed.EventName eventName;
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
    @SerializedName("question_id")
    @Expose
    private String questionId;
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
    public PollAnswerPageViewed() {
    }

    /**
     * 
     * @param questionId
     * @param common
     * @param orderId
     * @param eventName
     * @param offerId
     * @param user
     */
    public PollAnswerPageViewed(PollAnswerPageViewed.EventName eventName, Common common, User user, String questionId, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.questionId = questionId;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public PollAnswerPageViewed.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(PollAnswerPageViewed.EventName eventName) {
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
    public String getQuestionId() {
        return questionId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
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

        @SerializedName("poll_answer_page_viewed")
        POLL_ANSWER_PAGE_VIEWED("poll_answer_page_viewed");
        private final String value;
        private final static Map<String, PollAnswerPageViewed.EventName> CONSTANTS = new HashMap<String, PollAnswerPageViewed.EventName>();

        static {
            for (PollAnswerPageViewed.EventName c: values()) {
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

        public static PollAnswerPageViewed.EventName fromValue(String value) {
            PollAnswerPageViewed.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
