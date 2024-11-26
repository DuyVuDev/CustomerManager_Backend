package com.example.CustomerManager.controller;

import com.example.CustomerManager.dto.responses.ResponseVoucherDTO;
import com.example.CustomerManager.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public List<ResponseVoucherDTO> getAllVouchers() {
        return voucherService.getAllVouchers();
    }
}
