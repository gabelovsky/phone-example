package com.phonecompany.billing;

import java.math.BigDecimal;

public class PhoneBill {
    private int callCount;
    private BigDecimal charge;

    public PhoneBill(BigDecimal charge) {
        this.callCount = 1;
        this.charge = charge;
    }

    public void addCall(BigDecimal charge) {
        this.callCount++;
        this.charge = this.charge.add(charge);
    }

    public int getCallCount() {
        return callCount;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setZeroCharge() {
        this.charge = new BigDecimal(0);
    }


    @Override
    public String toString() {
        return "Call count:" + this.callCount + " charge:" + this.charge;
    }
}
