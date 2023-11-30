package com.uts.jwp.Domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Lecture {
    @NotBlank(message = "nip is required")
    // @Pattern(regexp = "LCT", message = "must be LCT")
    private String nip;
    @NotBlank(message = "full name is required")
    @Size(min = 3, max = 50)
    private String fullName;
    @Email(message = "email must @ and is required")
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "phone number is required")
    private String phoneNumber;

    public String getNip() {
        return nip;
    }
    public void setNip(String nip) {
        this.nip = nip;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
 
    
}
