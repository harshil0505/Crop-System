package com.cropsys.backend.Security.Responce;

import java.util.List;

import com.cropsys.backend.model.State;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String name;
    private String email;

    private State state;       
    private String phoneNumber;  

    private List<String> roles;
 
}
