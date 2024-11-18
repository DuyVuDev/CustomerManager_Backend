package com.example.CustomerManager.service;

import com.example.CustomerManager.dto.requests.RequestRankDTO;
import com.example.CustomerManager.dto.responses.ResponseRankDTO;
import com.example.CustomerManager.entity.Customer;
import com.example.CustomerManager.entity.Rank;
import com.example.CustomerManager.repository.CustomerRepository;
import com.example.CustomerManager.repository.RankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RankService {

    @Autowired
    RankRepository rankRepository;

    @Autowired
    CustomerRepository customerRepository;

    public List<ResponseRankDTO> getAllRanks() {
        List<ResponseRankDTO> responseRanks = new ArrayList<>();
        List<Rank> rankList = rankRepository.findAll();
        for (Rank rank : rankList) {
            responseRanks.add(getRankById(rank.getId()));
        }
        return responseRanks;
    }

    public ResponseRankDTO getRankById(Long id) {
        Rank rank = rankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rank not found"));
        ResponseRankDTO rankDTO = new ResponseRankDTO();
        rankDTO.setId(rank.getId());
        rankDTO.setName(rank.getName());
        rankDTO.setDescription(rank.getDescription());
        rankDTO.setPromotionScore(rank.getPromotionScore());
        rankDTO.setReward(rank.getReward());

        return rankDTO;
    }

    public void reassignRankToCustomersWhenInsertRank(Rank rank) {
        Rank previousRank = rankRepository
                .findTopByPromotionScoreLessThanOrderByPromotionScoreDesc(rank.getPromotionScore());
        Optional<List<Customer>> customerList = customerRepository.findCustomersByRankId(previousRank.getId());
        if (customerList.isPresent()) {
            List<Customer> customers = customerList.get();
            for (Customer customer : customers) {
                if (customer.getScore() >= rank.getPromotionScore()) {
                    customer.setRank(rank);
                }
            }
            customerRepository.saveAll(customers);
        }
    }

    public void reassignRankToCustomersWhenRemoveRank(Rank rank) {
        Rank previousRank = rankRepository
                .findTopByPromotionScoreLessThanOrderByPromotionScoreDesc(rank.getPromotionScore());
        Optional<List<Customer>> customerList = customerRepository.findCustomersByRankId(rank.getId());
        if (customerList.isPresent()) {
            List<Customer> customers = customerList.get();
            for (Customer customer : customers) {
                customer.setRank(previousRank);
            }
            customerRepository.saveAll(customers);
        }
    }

    public Rank createRank(RequestRankDTO requestRankDTO) {
        Rank rank = new Rank();
        rank.setName(requestRankDTO.getName());
        rank.setDescription(requestRankDTO.getDescription());
        rank.setPromotionScore(requestRankDTO.getPromotionScore());
        rank.setReward(requestRankDTO.getReward());
        rankRepository.save(rank);

        reassignRankToCustomersWhenInsertRank(rank);

        return rank;
    }

    public Rank updateRank(Long id, RequestRankDTO requestRankDTO) {
        Rank rank = rankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rank not found"));

        reassignRankToCustomersWhenRemoveRank(rank);

        rank.setName(requestRankDTO.getName());
        rank.setDescription(requestRankDTO.getDescription());
        rank.setPromotionScore(requestRankDTO.getPromotionScore());
        rank.setReward(requestRankDTO.getReward());
        rankRepository.save(rank);

        reassignRankToCustomersWhenInsertRank(rank);

        return rank;
    }

    public void deleteRankById(Long id) {
        reassignRankToCustomersWhenRemoveRank(rankRepository.findById(id).get());
        rankRepository.deleteById(id);
    }

    public void deleteAllRanksByIds(List<Long> ids) {
        for (Long id: ids) {
            deleteRankById(id);
        }
    }
}
