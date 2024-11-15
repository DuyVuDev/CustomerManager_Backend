package com.example.CustomerManager.service;

import com.example.CustomerManager.dto.requests.RequestCustomerDTO;
import com.example.CustomerManager.dto.responses.ResponseCustomerDTO;
import com.example.CustomerManager.entity.Customer;
import com.example.CustomerManager.entity.CustomerVoucher;
import com.example.CustomerManager.entity.Rank;
import com.example.CustomerManager.entity.Voucher;
import com.example.CustomerManager.repository.CustomerRepository;
import com.example.CustomerManager.repository.CustomerVoucherRepository;
import com.example.CustomerManager.repository.RankRepository;
import com.example.CustomerManager.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.Year;
import java.util.*;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CustomerVoucherRepository customerVoucherRepository;

    private String generateCustomerId() {
        int year = Year.now().getValue() % 100;
        String yearPart = String.format("%02d", year);
        Optional<List<String>> customerIdsInYearOpt = customerRepository.getCustomerIdInYear(yearPart);
        if (customerIdsInYearOpt.isPresent()) {
            List<String> customerIdsInYear = customerIdsInYearOpt.get();
            String lastCustomerId = Collections.max(customerIdsInYear);
            String[] parts = lastCustomerId.split("\\.");
            for (String part : parts) {
                System.out.println(part);
            }
            int autoIncrement = Integer.parseInt(parts[1])+1;
            return yearPart + "." + autoIncrement;
        } else return yearPart + ".1";
    }

    public List<ResponseCustomerDTO> getAllCustomers() {
        List<ResponseCustomerDTO> responseCustomers = new ArrayList<>();
        List<Customer> customerList = customerRepository.findAll();
        for (Customer customer : customerList) {
            responseCustomers.add(getCustomerById(customer.getId()));
        }
        return responseCustomers;
    }

    public ResponseCustomerDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        ResponseCustomerDTO responseCustomerDTO = new ResponseCustomerDTO();
        responseCustomerDTO.setId(customer.getId());
        responseCustomerDTO.setName(customer.getName());
        responseCustomerDTO.setDob(customer.getDob());
        responseCustomerDTO.setEmail(customer.getEmail());
        responseCustomerDTO.setPhone(customer.getPhone());
        responseCustomerDTO.setGender(customer.getGender());
        responseCustomerDTO.setScore(customer.getScore());
        responseCustomerDTO.setRankName(customer.getRank().getName());
        List<CustomerVoucher> customerVoucherList
                = customerVoucherRepository.findAllByCustomer_Id(customer.getId());
        StringBuilder responseDiscountStr = new StringBuilder();
        for (CustomerVoucher customerVoucher : customerVoucherList) {
            int voucherQuantity = customerVoucher.getQuantity();
            while (voucherQuantity > 0) {
                responseDiscountStr.append(customerVoucher.getVoucher().getDiscount()).append(",");
                voucherQuantity--;
            }
        }
        responseCustomerDTO.setDiscountStr(responseDiscountStr.isEmpty() ? "" :
                responseDiscountStr
                        .deleteCharAt(responseDiscountStr.length() - 1)
                        .toString()
        );
        return responseCustomerDTO;
    }

    public Customer createCustomer(RequestCustomerDTO requestCustomerDTO) {
        Customer customer = new Customer();
        customer.setId(generateCustomerId());
        return handleDataFromRequestCustomerDTO(requestCustomerDTO, customer);
    }

    public Customer updateCustomer(String customer_id, RequestCustomerDTO requestCustomerDTO) {
        Customer customer = customerRepository
                .findById(customer_id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerVoucherRepository.deleteAllByCustomer_Id(customer.getId());
        return handleDataFromRequestCustomerDTO(requestCustomerDTO, customer);
    }

    private Customer handleDataFromRequestCustomerDTO(RequestCustomerDTO requestCustomerDTO, Customer customer) {
        customer.setName(requestCustomerDTO.getName());
        customer.setDob(requestCustomerDTO.getDob());
        customer.setEmail(requestCustomerDTO.getEmail());
        customer.setPhone(requestCustomerDTO.getPhone());
        customer.setGender(requestCustomerDTO.getGender());
        customer.setScore(requestCustomerDTO.getScore());

        //assign rank
        if (customer.getScore() == 0) {
            Rank defaultRank = rankRepository.findById(0L).orElse(null);
            customer.setRank(defaultRank);
        } else {
            Rank rank = rankRepository.findRankByScore(customer.getScore());
            customer.setRank(rank);
        }
        customerRepository.save(customer);

        //add customer's vouchers
        if (!Objects.equals(requestCustomerDTO.getDiscountStr(), "")) {
            SortedMap<Float, Integer> customerDiscounts = new TreeMap<>();
            StringTokenizer stringTokenizer = new StringTokenizer(requestCustomerDTO.getDiscountStr(), ",");
            while (stringTokenizer.hasMoreTokens()) {
                Float discount = Float.parseFloat(stringTokenizer.nextToken());
                if (!voucherRepository.existsByDiscount(discount)) {
                    Voucher newDiscount = new Voucher();
                    newDiscount.setDiscount(discount);
                    voucherRepository.save(newDiscount);
                }
                if (customerDiscounts.containsKey(discount)) {
                    customerDiscounts.put(discount, customerDiscounts.get(discount) + 1);
                } else customerDiscounts.put(discount, 1);
            }

            customerDiscounts.forEach((discount, quantity) -> {
                CustomerVoucher customerVoucher = new CustomerVoucher();
                customerVoucher.setCustomer(customer);
                customerVoucher.setVoucher(voucherRepository.findByDiscount(discount));
                customerVoucher.setQuantity(quantity);
                customerVoucherRepository.save(customerVoucher);
            });}


        return customer;
    }

    public void deleteCustomerById(String customer_id) {
        customerRepository.deleteById(customer_id);
    }

    public void deleteCustomersByIds(List<String> customer_ids) {
        customerRepository.deleteAllById(customer_ids);
    }
}
