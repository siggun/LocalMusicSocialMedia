package com.bandswipe.jam.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateJamRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @Size(max = 200)
    private String locationName;

    private Double latitude;
    private Double longitude;

    @NotNull
    @Future
    private LocalDateTime eventDate;

    private UUID bandId;
}
