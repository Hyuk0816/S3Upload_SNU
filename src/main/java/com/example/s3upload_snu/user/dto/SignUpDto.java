package com.example.s3upload_snu.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {

    private String email;
    private String password;
}
