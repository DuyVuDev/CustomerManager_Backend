package com.example.CustomerManager.controller;

import com.example.CustomerManager.dto.requests.RequestVoucherDTO;
import com.example.CustomerManager.service.CustomerVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer_voucher")
public class CustomerVoucherController {

    @Autowired
    private CustomerVoucherService customerVoucherService;

    @PostMapping("/publish")
    public void giveRewardToCustomer(@RequestBody List<RequestVoucherDTO> requestVoucherDTOs) {
        Map<Long, Float> voucher = new HashMap<>();
        requestVoucherDTOs.forEach(requestVoucherDTO -> {
            voucher.put(requestVoucherDTO.getRank_id(), requestVoucherDTO.getDiscount());
        });
        customerVoucherService.publishVoucher(voucher);
    }
}
