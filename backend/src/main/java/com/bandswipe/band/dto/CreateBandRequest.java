package com.bandswipe.band.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateBandRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 50)
    private String genre;

    @Size(max = 500)
    private String bio;

    private List<UUID> memberUserIds;
}
