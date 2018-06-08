
package kin.ecosystem.core.bi.simple.events;

// Augmented by script
import kin.ecosystem.core.bi.Event;
import kin.ecosystem.core.bi.simple.Store;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User taps on an answer in a poll
 * 
 */
public class PollAnswerButtonTapped implements Event {
    // Augmented by script
    public static PollAnswerButtonTapped create(String answerId, String questionId, String offerId, String orderId) {
        return new PollAnswerButtonTapped(EventName.POLL_ANSWER_BUTTON_TAPPED, Store.common, Store.user, answerId, questionId, offerId, orderId);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private PollAnswerButtonTapped.EventName eventName;
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
    @SerializedName("answer_id")
    @Expose
    private String answerId;
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
    public PollAnswerButtonTapped() {
    }

    /**
     * 
     * @param answerId
     * @param questionId
     * @param common
     * @param orderId
     * @param eventName
     * @param offerId
     * @param user
     */
    public PollAnswerButtonTapped(PollAnswerButtonTapped.EventName eventName, Common common, User user, String answerId, String questionId, String offerId, String orderId) {
        super();
        this.eventName = eventName;
        this.common = common;
        this.user = user;
        this.answerId = answerId;
        this.questionId = questionId;
        this.offerId = offerId;
        this.orderId = orderId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public PollAnswerButtonTapped.EventName getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(PollAnswerButtonTapped.EventName eventName) {
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
    public String getAnswerId() {
        return answerId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setAnswerId(String answerId) {
        this.answerId = answerId;
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

        @SerializedName("poll_answer_button_tapped")
        POLL_ANSWER_BUTTON_TAPPED("poll_answer_button_tapped");
        private final String value;
        private final static Map<String, PollAnswerButtonTapped.EventName> CONSTANTS = new HashMap<String, PollAnswerButtonTapped.EventName>();

        static {
            for (PollAnswerButtonTapped.EventName c: values()) {
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

        public static PollAnswerButtonTapped.EventName fromValue(String value) {
            PollAnswerButtonTapped.EventName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
