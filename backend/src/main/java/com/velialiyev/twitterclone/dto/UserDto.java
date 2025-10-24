package com.velialiyev.twitterclone.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;
    private String username;
    private String bio;
    private String location;
    private String personalWebsite;
    private String birthDate;
}
