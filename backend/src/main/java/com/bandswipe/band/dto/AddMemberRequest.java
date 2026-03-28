package com.bandswipe.band.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddMemberRequest {

    @NotNull
    private UUID userId;

    private String role;
}
