package com.example.CustomerManager.entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ranking")
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "promotion_score", columnDefinition = "int UNSIGNED not null")
    private Long promotionScore;

    @Column(name = "reward", nullable = false)
    private Float reward;

    @OneToMany(mappedBy = "rank", cascade = CascadeType.ALL)
    private Set<Customer> customers = new LinkedHashSet<>();

    public Rank() {
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

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

}