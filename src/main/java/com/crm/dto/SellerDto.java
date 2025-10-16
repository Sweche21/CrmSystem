package com.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class SellerDto {
    private Long id;
    private String name;
    private String contactInfo;
    private LocalDateTime registrationDate;

    public SellerDto() {}

    public SellerDto(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public SellerDto(Long id, String name, String contactInfo, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
    }


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public LocalDateTime getRegistrationDate() { return registrationDate; }

    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
}