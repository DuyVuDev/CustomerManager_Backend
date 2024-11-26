package com.example.CustomerManager.service;

import com.example.CustomerManager.dto.responses.ResponseVoucherDTO;
import com.example.CustomerManager.entity.Voucher;
import com.example.CustomerManager.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherService {

    @Autowired
    VoucherRepository voucherRepository;

    public List<ResponseVoucherDTO> getAllVouchers() {
        List<Voucher> vouchers = voucherRepository.findAll();
        List<ResponseVoucherDTO> responseVouchers = new ArrayList<>();
        for (Voucher voucher : vouchers) {
            ResponseVoucherDTO responseVoucher=new ResponseVoucherDTO();
            responseVoucher.setId(voucher.getId());
            responseVoucher.setDiscount(voucher.getDiscount());
            responseVouchers.add(responseVoucher);
        }

        return responseVouchers;
    }
}
