package com.bandswipe.profile.controller;

import com.bandswipe.profile.entity.Genre;
import com.bandswipe.profile.entity.Instrument;
import com.bandswipe.profile.repository.GenreRepository;
import com.bandswipe.profile.repository.InstrumentRepository;
import com.bandswipe.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReferenceDataController {

    private final InstrumentRepository instrumentRepository;
    private final GenreRepository genreRepository;

    @GetMapping("/instruments")
    public ResponseEntity<ApiResponse<List<Instrument>>> getAllInstruments() {
        List<Instrument> instruments = instrumentRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Instruments retrieved successfully", instruments));
    }

    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<List<Genre>>> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Genres retrieved successfully", genres));
    }
}
