package com.example.CustomerManager.repository;

import com.example.CustomerManager.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {

    @Query("SELECT r FROM Rank r WHERE r.promotionScore <= :score ORDER BY r.promotionScore DESC LIMIT 1")
    Rank findRankByScore(@Param("score") Long score);

    @Query("SELECT r.id FROM Rank r")
    Optional<List<Long>> getAllRankIds();

    Rank findByName(String name);

    Rank findTopByPromotionScoreLessThanOrderByPromotionScoreDesc(Long promotionScore);
}
