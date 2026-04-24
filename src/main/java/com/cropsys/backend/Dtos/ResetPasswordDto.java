package com.cropsys.backend.Dtos;

import lombok.Data;

@Data

public class ResetPasswordDto {

    private String email;
    private String newPassword;
    private String confirmPassword;
}
