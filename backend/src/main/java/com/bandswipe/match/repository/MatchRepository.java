package com.bandswipe.match.repository;

import com.bandswipe.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    @Query("""
            SELECT m FROM Match m
            WHERE (m.user1.id = :userId OR m.user2.id = :userId)
              AND m.isActive = true
            ORDER BY m.matchedAt DESC
            """)
    List<Match> findActiveMatchesForUser(@Param("userId") UUID userId);

    Optional<Match> findByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);
}
