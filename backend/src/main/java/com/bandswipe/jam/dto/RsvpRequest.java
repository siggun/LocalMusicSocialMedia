package com.bandswipe.jam.dto;

import com.bandswipe.shared.enums.RsvpStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsvpRequest {

    @NotNull
    private RsvpStatus status;
}
