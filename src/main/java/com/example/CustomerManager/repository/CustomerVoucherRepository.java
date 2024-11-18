package com.example.CustomerManager.repository;

import com.example.CustomerManager.entity.CustomerVoucher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {

    List<CustomerVoucher> findAllByCustomer_Id(String customerId);

    @Transactional
    void deleteAllByCustomer_Id(String customerId);

    boolean existsByCustomer_IdAndVoucher_Id(String customerId, Long voucherId);

    CustomerVoucher findByCustomer_IdAndVoucher_Id(String customerId, Long voucherId);
}
