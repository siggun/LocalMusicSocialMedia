package com.bandswipe.swipe.repository;

import com.bandswipe.swipe.entity.SwipeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SwipeRepository extends JpaRepository<SwipeHistory, UUID> {

    boolean existsBySwiperIdAndSwipedId(UUID swiperId, UUID swipedId);

    Optional<SwipeHistory> findBySwiperIdAndSwipedId(UUID swiperId, UUID swipedId);

    @Query("""
            SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
            FROM SwipeHistory s
            WHERE s.swiper.id = :userId2
              AND s.swiped.id = :userId1
              AND s.direction = 'RIGHT'
            """)
    boolean existsMutualRightSwipe(@Param("userId1") UUID userId1,
                                   @Param("userId2") UUID userId2);
}
