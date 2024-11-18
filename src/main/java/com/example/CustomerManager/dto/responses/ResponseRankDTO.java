package com.example.CustomerManager.dto.responses;

public class ResponseRankDTO {
    private Long id;
    private String name;
    private String description;
    private Long promotionScore;
    private Float reward;

    public ResponseRankDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPromotionScore() {
        return promotionScore;
    }

    public void setPromotionScore(Long promotionScore) {
        this.promotionScore = promotionScore;
    }

    public Float getReward() {
        return reward;
    }

    public void setReward(Float reward) {
        this.reward = reward;
    }
}
