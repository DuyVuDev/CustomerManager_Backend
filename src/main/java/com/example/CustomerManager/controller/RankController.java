package com.example.CustomerManager.controller;

import com.example.CustomerManager.dto.requests.RequestRankDTO;
import com.example.CustomerManager.dto.responses.ResponseRankDTO;
import com.example.CustomerManager.entity.Rank;
import com.example.CustomerManager.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ranks")
public class RankController {

    @Autowired
    private RankService rankService;

    @GetMapping
    public List<ResponseRankDTO> getAllRanks() {
        return rankService.getAllRanks();
    }

    @GetMapping("/{id}")
    public ResponseRankDTO getRankById(@PathVariable Long id) {
        return rankService.getRankById(id);
    }

    @PostMapping
    public Rank createRank(@RequestBody RequestRankDTO requestRankDTO) {
        return rankService.createRank(requestRankDTO);
    }

    @PutMapping("/{id}")
    public Rank updateRank(@PathVariable Long id,@RequestBody RequestRankDTO requestRankDTO) {
        return rankService.updateRank(id, requestRankDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteRankById(@PathVariable Long id) {
        rankService.deleteRankById(id);
    }

    @DeleteMapping("/deleteMultiple")
    public void deleteAllRanksById(@RequestBody List<Long> ids) {
        rankService.deleteAllRanksByIds(ids);
    }
}
