package com.kin.ecosystem.bi.events;

public interface UserInterface extends UserReadonly {
    void setDigitalServiceUserId(String digitalServiceUserId);

    void setBalance(Double balance);

    void setEarnCount(Integer earnCount);

    void setTotalKinSpent(Double totalKinSpent);

    void setDigitalServiceId(String digitalServiceId);

    void setTransactionCount(Integer transactionCount);

    void setEntryPointParam(String entryPointParam);

    void setSpendCount(Integer spendCount);

    void setTotalKinEarned(Double totalKinEarned);

}
