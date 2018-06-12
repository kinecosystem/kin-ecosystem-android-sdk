package com.kin.ecosystem.data.model;

import java.math.BigDecimal;

public class Balance {

    private BigDecimal amount;

    public Balance() {
        this.amount = new BigDecimal(0);
    }

    public Balance(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
