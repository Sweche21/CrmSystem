package com.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AnalyticsDto {


    public static class TopSeller {
        private Long sellerId;
        private String sellerName;
        private BigDecimal totalAmount;
        private String period;

        public TopSeller() {}

        public TopSeller(Long sellerId, String sellerName, BigDecimal totalAmount, String period) {
            this.sellerId = sellerId;
            this.sellerName = sellerName;
            this.totalAmount = totalAmount;
            this.period = period;
        }

        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
    }

    public static class SellerWithTotal {
        private Long sellerId;
        private String sellerName;
        private BigDecimal totalAmount;

        public SellerWithTotal() {}

        public SellerWithTotal(Long sellerId, String sellerName, BigDecimal totalAmount) {
            this.sellerId = sellerId;
            this.sellerName = sellerName;
            this.totalAmount = totalAmount;
        }

        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    }

    public static class BestPeriod {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Long transactionCount;
        private BigDecimal totalAmount;

        public BestPeriod() {}

        public BestPeriod(LocalDateTime startDate, LocalDateTime endDate, Long transactionCount, BigDecimal totalAmount) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.transactionCount = transactionCount;
            this.totalAmount = totalAmount;
        }

        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

        public Long getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    }
}