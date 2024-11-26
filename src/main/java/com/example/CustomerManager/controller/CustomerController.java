package com.example.CustomerManager.controller;

import com.example.CustomerManager.dto.requests.RequestCustomerDTO;
import com.example.CustomerManager.dto.responses.ResponseCustomerDTO;
import com.example.CustomerManager.entity.Customer;
import com.example.CustomerManager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    void createCustomer(@RequestBody RequestCustomerDTO requestCustomerDto) {
        customerService.createCustomer(requestCustomerDto);
    }

    @GetMapping
    List<ResponseCustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    ResponseCustomerDTO getCustomerById(@PathVariable("id") String id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/filter")
    List<ResponseCustomerDTO> filterAndSortCustomer(
            @RequestParam(name = "rankName") String rankName,
            @RequestParam(name = "discount") String discount,
            @RequestParam(name = "sortBy") String sortBy,
            @RequestParam(name = "sortOrder") String sortOrder) {
        return customerService.filterAndSortCustomer(rankName, discount, sortBy, sortOrder);
    }

    @PutMapping("/{id}")
    void updateCustomer(@PathVariable("id") String id, @RequestBody RequestCustomerDTO requestCustomerDto) {
        customerService.updateCustomer(id, requestCustomerDto);
    }

    @PutMapping("/randomScore")
    void increaseScoreOfAllCustomers() {
        customerService.increaseScoreOfAllCustomers();
    }

    @DeleteMapping("/{id}")
    void deleteCustomer(@PathVariable("id") String id) {
        customerService.deleteCustomerById(id);
    }

    @DeleteMapping("/deleteMultiple")
    void deleteCustomersByIds(@RequestBody List<String> ids) {
        customerService.deleteAllCustomersByIds(ids);
    }
}
