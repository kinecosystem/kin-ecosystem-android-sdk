
package com.kin.ecosystem.bi.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * common user properties
 * 
 */
public class User implements UserInterface {

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("digital_service_user_id")
    @Expose
    private String digitalServiceUserId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("balance")
    @Expose
    private Double balance;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("earn_count")
    @Expose
    private Integer earnCount;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("total_kin_spent")
    @Expose
    private Double totalKinSpent;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("digital_service_id")
    @Expose
    private String digitalServiceId;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("transaction_count")
    @Expose
    private Integer transactionCount;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("entry_point_param")
    @Expose
    private String entryPointParam;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("spend_count")
    @Expose
    private Integer spendCount;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("total_kin_earned")
    @Expose
    private Double totalKinEarned;

    /**
     * No args constructor for use in serialization
     * 
     */
    public User() {
    }

    /**
     * 
     * @param digitalServiceUserId
     * @param totalKinEarned
     * @param balance
     * @param earnCount
     * @param totalKinSpent
     * @param spendCount
     * @param transactionCount
     * @param digitalServiceId
     * @param entryPointParam
     */
    public User(String digitalServiceUserId, Double balance, Integer earnCount, Double totalKinSpent, String digitalServiceId, Integer transactionCount, String entryPointParam, Integer spendCount, Double totalKinEarned) {
        super();
        this.digitalServiceUserId = digitalServiceUserId;
        this.balance = balance;
        this.earnCount = earnCount;
        this.totalKinSpent = totalKinSpent;
        this.digitalServiceId = digitalServiceId;
        this.transactionCount = transactionCount;
        this.entryPointParam = entryPointParam;
        this.spendCount = spendCount;
        this.totalKinEarned = totalKinEarned;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getDigitalServiceUserId() {
        return digitalServiceUserId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setDigitalServiceUserId(String digitalServiceUserId) {
        this.digitalServiceUserId = digitalServiceUserId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Double getBalance() {
        return balance;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Integer getEarnCount() {
        return earnCount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEarnCount(Integer earnCount) {
        this.earnCount = earnCount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Double getTotalKinSpent() {
        return totalKinSpent;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTotalKinSpent(Double totalKinSpent) {
        this.totalKinSpent = totalKinSpent;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getDigitalServiceId() {
        return digitalServiceId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setDigitalServiceId(String digitalServiceId) {
        this.digitalServiceId = digitalServiceId;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Integer getTransactionCount() {
        return transactionCount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEntryPointParam() {
        return entryPointParam;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEntryPointParam(String entryPointParam) {
        this.entryPointParam = entryPointParam;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Integer getSpendCount() {
        return spendCount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setSpendCount(Integer spendCount) {
        this.spendCount = spendCount;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Double getTotalKinEarned() {
        return totalKinEarned;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTotalKinEarned(Double totalKinEarned) {
        this.totalKinEarned = totalKinEarned;
    }

}
