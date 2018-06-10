package kin.ecosystem.core.bi.events;

import kin.ecosystem.core.bi.EventsStore;

public class UserProxy implements UserInterface {
    public User snapshot() {
        return new User(
            this.getDigitalServiceUserId(),
            this.getDigitalServiceName(),
            this.getBalance(),
            this.getEarnCount(),
            this.getTotalKinSpent(),
            this.getDigitalServiceId(),
            this.getTransactionCount(),
            this.getEntryPointParam(),
            this.getSpendCount(),
            this.getTotalKinEarned());
    }

    private String digitalServiceUserId;
    private EventsStore.DynamicValue<String> dynamicDigitalServiceUserId;
    public String getDigitalServiceUserId() {
        return this.digitalServiceUserId == null ? this.digitalServiceUserId : this.dynamicDigitalServiceUserId.get();
    }
    public void setDigitalServiceUserId(String digitalServiceUserId) {
        this.digitalServiceUserId = digitalServiceUserId;
    }
    public void setDigitalServiceUserId(EventsStore.DynamicValue<String> digitalServiceUserId) {
        this.dynamicDigitalServiceUserId = digitalServiceUserId;
    }

    private String digitalServiceName;
    private EventsStore.DynamicValue<String> dynamicDigitalServiceName;
    public String getDigitalServiceName() {
        return this.digitalServiceName == null ? this.digitalServiceName : this.dynamicDigitalServiceName.get();
    }
    public void setDigitalServiceName(String digitalServiceName) {
        this.digitalServiceName = digitalServiceName;
    }
    public void setDigitalServiceName(EventsStore.DynamicValue<String> digitalServiceName) {
        this.dynamicDigitalServiceName = digitalServiceName;
    }

    private Double balance;
    private EventsStore.DynamicValue<Double> dynamicBalance;
    public Double getBalance() {
        return this.balance == null ? this.balance : this.dynamicBalance.get();
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }
    public void setBalance(EventsStore.DynamicValue<Double> balance) {
        this.dynamicBalance = balance;
    }

    private Integer earnCount;
    private EventsStore.DynamicValue<Integer> dynamicEarnCount;
    public Integer getEarnCount() {
        return this.earnCount == null ? this.earnCount : this.dynamicEarnCount.get();
    }
    public void setEarnCount(Integer earnCount) {
        this.earnCount = earnCount;
    }
    public void setEarnCount(EventsStore.DynamicValue<Integer> earnCount) {
        this.dynamicEarnCount = earnCount;
    }

    private Double totalKinSpent;
    private EventsStore.DynamicValue<Double> dynamicTotalKinSpent;
    public Double getTotalKinSpent() {
        return this.totalKinSpent == null ? this.totalKinSpent : this.dynamicTotalKinSpent.get();
    }
    public void setTotalKinSpent(Double totalKinSpent) {
        this.totalKinSpent = totalKinSpent;
    }
    public void setTotalKinSpent(EventsStore.DynamicValue<Double> totalKinSpent) {
        this.dynamicTotalKinSpent = totalKinSpent;
    }

    private String digitalServiceId;
    private EventsStore.DynamicValue<String> dynamicDigitalServiceId;
    public String getDigitalServiceId() {
        return this.digitalServiceId == null ? this.digitalServiceId : this.dynamicDigitalServiceId.get();
    }
    public void setDigitalServiceId(String digitalServiceId) {
        this.digitalServiceId = digitalServiceId;
    }
    public void setDigitalServiceId(EventsStore.DynamicValue<String> digitalServiceId) {
        this.dynamicDigitalServiceId = digitalServiceId;
    }

    private Integer transactionCount;
    private EventsStore.DynamicValue<Integer> dynamicTransactionCount;
    public Integer getTransactionCount() {
        return this.transactionCount == null ? this.transactionCount : this.dynamicTransactionCount.get();
    }
    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
    public void setTransactionCount(EventsStore.DynamicValue<Integer> transactionCount) {
        this.dynamicTransactionCount = transactionCount;
    }

    private String entryPointParam;
    private EventsStore.DynamicValue<String> dynamicEntryPointParam;
    public String getEntryPointParam() {
        return this.entryPointParam == null ? this.entryPointParam : this.dynamicEntryPointParam.get();
    }
    public void setEntryPointParam(String entryPointParam) {
        this.entryPointParam = entryPointParam;
    }
    public void setEntryPointParam(EventsStore.DynamicValue<String> entryPointParam) {
        this.dynamicEntryPointParam = entryPointParam;
    }

    private Integer spendCount;
    private EventsStore.DynamicValue<Integer> dynamicSpendCount;
    public Integer getSpendCount() {
        return this.spendCount == null ? this.spendCount : this.dynamicSpendCount.get();
    }
    public void setSpendCount(Integer spendCount) {
        this.spendCount = spendCount;
    }
    public void setSpendCount(EventsStore.DynamicValue<Integer> spendCount) {
        this.dynamicSpendCount = spendCount;
    }

    private Double totalKinEarned;
    private EventsStore.DynamicValue<Double> dynamicTotalKinEarned;
    public Double getTotalKinEarned() {
        return this.totalKinEarned == null ? this.totalKinEarned : this.dynamicTotalKinEarned.get();
    }
    public void setTotalKinEarned(Double totalKinEarned) {
        this.totalKinEarned = totalKinEarned;
    }
    public void setTotalKinEarned(EventsStore.DynamicValue<Double> totalKinEarned) {
        this.dynamicTotalKinEarned = totalKinEarned;
    }

}
