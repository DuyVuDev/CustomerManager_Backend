package com.example.CustomerManager.controller;

import com.example.CustomerManager.dto.requests.RequestCustomerDTO;
import com.example.CustomerManager.dto.responses.ResponseCustomerDTO;
import com.example.CustomerManager.entity.Customer;
import com.example.CustomerManager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    Customer createCustomer(@RequestBody RequestCustomerDTO requestCustomerDto) {
        return customerService.createCustomer(requestCustomerDto);
    }

    @GetMapping
    List<ResponseCustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    ResponseCustomerDTO getCustomerById(@PathVariable("id") String id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    Customer updateCustomer(@PathVariable("id") String id, @RequestBody RequestCustomerDTO requestCustomerDto) {
        return customerService.updateCustomer(id, requestCustomerDto);
    }

    @DeleteMapping("/{id}")
    String deleteCustomer(@PathVariable("id") String id) {
        customerService.deleteCustomerById(id);
        return "Customer deleted";
    }

    @DeleteMapping("/deleteMultiple")
    String deleteCustomersByIds(@RequestBody List<String> ids) {
        customerService.deleteCustomersByIds(ids);
        return "Customers deleted";
    }
}
