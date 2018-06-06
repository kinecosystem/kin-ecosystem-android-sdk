package com.kin.ecosystem.data.model;

import java.math.BigDecimal;

public class BalanceUpdate {

    private BigDecimal amount;

    public BalanceUpdate() {
        this.amount = new BigDecimal(0);
    }

    public BalanceUpdate(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
