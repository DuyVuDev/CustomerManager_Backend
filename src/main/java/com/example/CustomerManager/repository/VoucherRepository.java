package com.example.CustomerManager.repository;

import com.example.CustomerManager.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    boolean existsByDiscount(Float discount);

    Voucher findByDiscount(Float discount);
}
