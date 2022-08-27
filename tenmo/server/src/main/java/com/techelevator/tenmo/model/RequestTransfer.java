package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RequestTransfer {

    private long requestee;
    @DecimalMin("0.01")
    private BigDecimal requestedAmount;
    private String description = " ";

    public long getRequestee() {
        return requestee;
    }

    public void setRequestee(long requestee) {
        this.requestee = requestee;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
