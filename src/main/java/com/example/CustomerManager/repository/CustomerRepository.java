package com.example.CustomerManager.repository;


import com.example.CustomerManager.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("SELECT c.id FROM Customer c WHERE c.id LIKE :yearPart%")
    Optional<List<String>> getCustomerIdInYear(@Param("yearPart") String yearPart);

    Optional<List<Customer>> findCustomersByRankId(Long rank_id);
}
