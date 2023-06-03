package com.phonecompany.billing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonPropertyOrder({"phoneNumber", "startTime", "endTime"})
public class PhoneLog {
    public String phoneNumber;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    public Date startTime;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    public Date endTime;
}
