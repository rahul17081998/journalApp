package com.rahul.journal_app.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRestRequest {

    private String userName;
    private String otp;
    private String updatedPassword;
}
