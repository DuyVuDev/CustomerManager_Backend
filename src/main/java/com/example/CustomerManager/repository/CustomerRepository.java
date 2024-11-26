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

    Optional<List<Customer>> findCustomersByRankName(String rank_name);

    @Query("SELECT c.id FROM Customer c")
    Optional<List<String>> getAllCustomerIds();

    @Query("SELECT c FROM Customer c " +
            "LEFT JOIN c.customerVouchers cv " +
            "WHERE (:rankId IS NULL OR c.rank.id = :rankId) " +
            "AND (:voucherId IS NULL OR cv.voucher.id = :voucherId) " +
            "ORDER BY CASE " +
            "WHEN :sortBy = 'score' THEN c.score " +
            "WHEN :sortBy = 'quantity' THEN cv.quantity " +
            "END ASC")
    List<Customer> findAllBySortAndFilterAsc(
            @Param("rankId") Long rankId,
            @Param("voucherId") Long voucherId,
            @Param("sortBy") String sortBy);

    @Query("SELECT c FROM Customer c " +
            "LEFT JOIN c.customerVouchers cv " +
            "WHERE (:rankId IS NULL OR c.rank.id = :rankId) " +
            "AND (:voucherId IS NULL OR cv.voucher.id = :voucherId) " +
            "ORDER BY CASE " +
            "WHEN :sortBy = 'score' THEN c.score " +
            "WHEN :sortBy = 'quantity' THEN cv.quantity " +
            "END DESC")
    List<Customer> findAllBySortAndFilterDesc(
            @Param("rankId") Long rankId,
            @Param("voucherId") Long voucherId,
            @Param("sortBy") String sortBy);
}