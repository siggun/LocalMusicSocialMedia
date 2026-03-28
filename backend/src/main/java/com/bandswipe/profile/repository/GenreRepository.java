package com.bandswipe.profile.repository;

import com.bandswipe.profile.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    List<Genre> findByIdIn(Set<Integer> ids);
}
