package com.example.CustomerManager.entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @Column(name = "discount", nullable = false)
    private Float discount;

    @Column(name = "description", length = 45)
    private String description;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL)
    private Set<CustomerVoucher> customerVouchers = new LinkedHashSet<>();

    public Voucher() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CustomerVoucher> getCustomerVouchers() {
        return customerVouchers;
    }

    public void setCustomerVouchers(Set<CustomerVoucher> customerVouchers) {
        this.customerVouchers = customerVouchers;
    }

}