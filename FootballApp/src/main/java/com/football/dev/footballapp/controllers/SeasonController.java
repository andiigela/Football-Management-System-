package com.football.dev.footballapp.controllers;

import com.football.dev.footballapp.dto.MatchDTO;
import com.football.dev.footballapp.dto.SeasonDto;
import com.football.dev.footballapp.models.Club;
import com.football.dev.footballapp.services.SeasonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/seasons")
public class SeasonController {
    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping
    public ResponseEntity<List<SeasonDto>> listAllSeasons() {
        List<SeasonDto> seasons = seasonService.getAllSeasons();
        return ResponseEntity.ok(seasons);
    }

    @GetMapping("{id}")
    public ResponseEntity<SeasonDto> getSeasonById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(seasonService.getSeasonById(id).get());
    }

//    @GetMapping("/{seasonId}/clubs")
//    public ResponseEntity<List<Club>> getClubsBySeasonId(@PathVariable Long seasonId) {
//        List<Club> clubs = seasonService.getClubsBySeasonId(seasonId);
//        return ResponseEntity.ok(clubs);
//    }
    @PostMapping("/save")
    public ResponseEntity<Void> createSeason(@RequestBody SeasonDto seasonDto) {
        seasonService.saveSeason(seasonDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
//    @PostMapping("/{seasonId}/matches")
//    public ResponseEntity<Void> addMatchesToSeason(@PathVariable("seasonId") Long seasonId, @RequestBody List<Long> matchIds) {
//        seasonService.addMatchesToSeason(seasonId, matchIds);
//        return ResponseEntity.ok().build();
//    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateSeason(@PathVariable("id") Long id, @RequestBody SeasonDto seasonDto) {
        seasonService.updateSeason(id, seasonDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSeason(@PathVariable("id") Long id) {
        seasonService.deleteSeason(id);
        return ResponseEntity.ok().build();
    }
//    @DeleteMapping("/{seasonId}/matches/{matchId}")
//    public ResponseEntity<Void> removeMatchFromSeason(@PathVariable Long seasonId, @PathVariable Long matchId) {
//        seasonService.removeMatchFromSeason(seasonId, matchId);
//        return ResponseEntity.ok().build();
//    }

}