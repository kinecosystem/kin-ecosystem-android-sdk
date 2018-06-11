
package com.kin.ecosystem.bi.events;

// Augmented by script
import com.kin.ecosystem.bi.Event;
import com.kin.ecosystem.bi.EventLoggerImpl;
import com.kin.ecosystem.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User taps on an answer in a poll
 * 
 */
public class PollAnswerButtonTapped implements Event {
    public static final String EVENT_NAME = "poll_answer_button_tapped";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static void fire(String answerId, String questionId, String offerId, String orderId) {
        final PollAnswerButtonTapped event = new PollAnswerButtonTapped(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            answerId,
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
    private String eventName = EVENT_NAME;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_type")
    @Expose
    private String eventType = EVENT_TYPE;
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

     * @param offerId

     * @param user
     */
    public PollAnswerButtonTapped(Common common, User user, String answerId, String questionId, String offerId, String orderId) {
        super();
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
    public String getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
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

}