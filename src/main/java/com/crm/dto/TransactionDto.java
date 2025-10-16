package com.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private BigDecimal amount;
    private String paymentType;
    private LocalDateTime transactionDate;

    public TransactionDto() {}

    public TransactionDto(Long sellerId, BigDecimal amount, String paymentType) {
        this.sellerId = sellerId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public TransactionDto(Long id, Long sellerId, String sellerName, BigDecimal amount,
                          String paymentType, LocalDateTime transactionDate) {
        this.id = id;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
    }


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getSellerName() { return sellerName; }

    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public LocalDateTime getTransactionDate() { return transactionDate; }

    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}