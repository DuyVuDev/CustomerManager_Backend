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

    @Autowired
    private CustomerVoucherService customerVoucherService;

    private String generateCustomerId() {
        int year = Year.now().getValue() % 100;
        String yearPart = String.format("%02d", year);
        Optional<List<String>> customerIdsInYearOpt = customerRepository.getCustomerIdInYear(yearPart);
        List<Integer> autoIncrements = new ArrayList<>();
        if (customerIdsInYearOpt.isPresent() && !customerIdsInYearOpt.get().isEmpty()) {
            List<String> customerIdsInYear = customerIdsInYearOpt.get();
            for (String customerId : customerIdsInYear) {
                String[] customerIdParts = customerId.split("\\.");
                autoIncrements.add(Integer.parseInt(customerIdParts[1]));
            }
            int autoIncrement = Collections.max(autoIncrements) + 1;
            return yearPart + "." + autoIncrement;
        } else return yearPart + ".1";
    }

    public List<ResponseCustomerDTO> getAllCustomers() {
        List<ResponseCustomerDTO> responseCustomers = new ArrayList<>();
        Optional<List<String>> customerList = customerRepository.getAllCustomerIds();
        if (customerList.isPresent()) {
            for (String customerId : customerList.get()) {
                responseCustomers.add(getCustomerById(customerId));
            }
        }

        return responseCustomers;
    }

    public ResponseCustomerDTO getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        ResponseCustomerDTO responseCustomerDTO = new ResponseCustomerDTO();
        responseCustomerDTO.setId(customer.getId());
        responseCustomerDTO.setName(customer.getName());
        responseCustomerDTO.setDob(String.valueOf(customer.getDob()));
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

    public void createCustomer(RequestCustomerDTO requestCustomerDTO) {
        Customer customer = new Customer();
        customer.setId(generateCustomerId());
        handleDataFromRequestCustomerDTO(requestCustomerDTO, customer);
    }

    public void updateCustomer(String customer_id, RequestCustomerDTO requestCustomerDTO) {
        Customer customer = customerRepository
                .findById(customer_id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerVoucherRepository.deleteAllByCustomer_Id(customer.getId());
        handleDataFromRequestCustomerDTO(requestCustomerDTO, customer);
    }

    private void handleDataFromRequestCustomerDTO(RequestCustomerDTO requestCustomerDTO, Customer customer) {
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
            });
        }
    }

    public void deleteCustomerById(String customer_id) {
        customerRepository.deleteById(customer_id);
    }

    public void deleteAllCustomersByIds(List<String> customer_ids) {
        customerRepository.deleteAllById(customer_ids);
    }

    public void increaseScoreOfAllCustomers() {
        List<Customer> customerList = customerRepository.findAll();
        for (Customer customer : customerList) {
            Random random = new Random();
            customer.setScore(customer.getScore() + random.nextLong(1000));
            Rank rank = customer.getRank();
            customer.setRank(rankRepository.findRankByScore(customer.getScore()));
            customerRepository.save(customer);
            if (customer.getRank() != rank) {
                Float reward = customer.getRank().getReward();
                customerVoucherService.addVoucherToCustomer(
                        customer.getId(),
                        voucherRepository.findByDiscount(reward).getId());
            }
        }
    }

    public List<ResponseCustomerDTO> filterAndSortCustomer(String rankName, String discount, String sortBy, String sortOrder) {
        List<ResponseCustomerDTO> responseCustomerDTOs = new ArrayList<>();
        Long rankId = Objects.equals(rankName, "null") ? null :rankRepository.findByName(rankName).getId();
        Long voucherId = Objects.equals(discount, "null") ? null : voucherRepository.findByDiscount(Float.parseFloat(discount)).getId();
        List<Customer> customerList = Objects.equals(sortOrder, "asc")
                ? customerRepository.findAllBySortAndFilterAsc(rankId, voucherId, sortBy)
                : customerRepository.findAllBySortAndFilterDesc(rankId, voucherId, sortBy);
        for (Customer customer : customerList) {
            responseCustomerDTOs.add(getCustomerById(customer.getId()));
        }
        return responseCustomerDTOs;
    }
}
