package com.example.CustomerManager.repository;

import com.example.CustomerManager.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {

    @Query("SELECT r FROM Rank r WHERE r.promotionScore <= :score ORDER BY r.promotionScore DESC")
    Rank findRankByScore(@Param("score") Long score);
}
