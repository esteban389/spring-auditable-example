package com.estebanm.auditing.person;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreatePersonDto {
    private String name;
    private String email;
}
