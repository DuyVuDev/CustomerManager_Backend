package com.example.CustomerManager.service;

import com.example.CustomerManager.entity.Customer;
import com.example.CustomerManager.entity.CustomerVoucher;
import com.example.CustomerManager.entity.Voucher;
import com.example.CustomerManager.repository.CustomerRepository;
import com.example.CustomerManager.repository.CustomerVoucherRepository;
import com.example.CustomerManager.repository.RankRepository;
import com.example.CustomerManager.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomerVoucherService {

    @Autowired
    CustomerVoucherRepository customerVoucherRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    RankRepository rankRepository;

    public void addVoucherToCustomer(String customerId, Long voucherId) {
        if (customerVoucherRepository.existsByCustomer_IdAndVoucher_Id(customerId,voucherId)) {
            CustomerVoucher customerVoucher =
                    customerVoucherRepository.findByCustomer_IdAndVoucher_Id(customerId,voucherId);
            customerVoucher.setQuantity(customerVoucher.getQuantity() + 1);
            customerVoucherRepository.save(customerVoucher);
        } else {
            CustomerVoucher customerVoucher = new CustomerVoucher();
            customerVoucher.setCustomer(customerRepository.findById(customerId).get());
            customerVoucher.setVoucher(voucherRepository.findById(voucherId).get());
            customerVoucher.setQuantity(1);
            customerVoucherRepository.save(customerVoucher);
        }
    }

    public void publishVoucher(Map<Long, Float> voucher) {
        voucher.forEach((rankID, discount) -> {
            if (!voucherRepository.existsByDiscount(discount)) {
                Voucher newDiscount = new Voucher();
                newDiscount.setDiscount(discount);
                voucherRepository.save(newDiscount);
            }
            List<Customer> customerList = customerRepository.findCustomersByRankId(rankID).get();
            for (Customer customer : customerList) {
                addVoucherToCustomer(customer.getId(),
                        voucherRepository.findByDiscount(discount).getId());
            }
        });
    }

}
