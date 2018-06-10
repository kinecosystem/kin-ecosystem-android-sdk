package com.kin.ecosystem.bi.events;

public interface UserReadonly {
    String getDigitalServiceUserId();

    String getDigitalServiceName();

    Double getBalance();

    Integer getEarnCount();

    Double getTotalKinSpent();

    String getDigitalServiceId();

    Integer getTransactionCount();

    String getEntryPointParam();

    Integer getSpendCount();

    Double getTotalKinEarned();

}
