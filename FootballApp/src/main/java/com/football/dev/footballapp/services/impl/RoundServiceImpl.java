package com.football.dev.footballapp.services.impl;

import com.football.dev.footballapp.dto.RoundDto;
import com.football.dev.footballapp.mapper.RoundDtoMapper;
import com.football.dev.footballapp.models.Club;
import com.football.dev.footballapp.models.Match;
import com.football.dev.footballapp.models.Round;
import com.football.dev.footballapp.models.Season;
import com.football.dev.footballapp.models.enums.MatchStatus;
import com.football.dev.footballapp.repository.jparepository.RoundRepository;
import com.football.dev.footballapp.repository.jparepository.ClubRepository;
import com.football.dev.footballapp.repository.jparepository.MatchRepository;
import com.football.dev.footballapp.services.RoundService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
@Service
public class RoundServiceImpl implements RoundService {
    private final RoundRepository roundRepository;
    private final RoundDtoMapper roundDtoMapper;
    private final ClubRepository clubRepository;
    private final MatchRepository matchRepository;

  public RoundServiceImpl(RoundRepository roundRepository, RoundDtoMapper roundDtoMapper, ClubRepository clubRepository, MatchRepository matchRepository) {
    this.roundRepository = roundRepository;
    this.roundDtoMapper = roundDtoMapper;
    this.clubRepository = clubRepository;
    this.matchRepository = matchRepository;
  }

  @Override
    public void saveRound(RoundDto roundDto, Long seasonId) {

    }

    @Override
    public Page<RoundDto> retrieveRounds(Long seasonId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Round> roundPage = roundRepository.findRoundsBySeasonIdOrderByInsertDateTimeDesc(seasonId, pageRequest);
        return roundPage.map(roundDtoMapper);
    }
    @Override
    public RoundDto getRound(Long roundId,Long seasonId) {
        Optional<Round> round = roundRepository.findByIdAndSeasonId(roundId,seasonId);
        if(round.isPresent()) return roundDtoMapper.apply(round.get());
        throw new EntityNotFoundException("Round not found with ids: seasonId: " + seasonId + " roundId: " + roundId);
    }
    @Override
    public void updateRound(RoundDto roundDto, Long roundId, Long seasonId) {
        roundRepository.findByIdAndSeasonId(roundId,seasonId).ifPresent(roundDb -> {
            roundDb.setStart_date(roundDto.getStart_date());
            roundDb.setEnd_date(roundDto.getEnd_date());
            roundRepository.save(roundDb);
        });
    }
    @Override
    public void deleteRound(Long roundId, Long seasonId) {
        roundRepository.findByIdAndSeasonId(roundId,seasonId).ifPresent(roundDb -> {
            roundDb.setIsDeleted(true);
            roundRepository.save(roundDb);
        });
    }

  public List<Round> generateRoundsAndSave(List<Club> clubs, int gamesPerRound, LocalDateTime startDate, Season season) {
    List<Round> rounds = new ArrayList<>();
    Random random = new Random();
    int totalClubs = clubs.size();
    int totalMatches = (totalClubs * (totalClubs - 1));

    int numberOfRounds = totalMatches / gamesPerRound;

    // Create a rotation schedule to ensure each team plays once in each round
    List<List<Club>> rotationSchedule = new ArrayList<>();
    for (int i = 0; i < numberOfRounds; i++) {
      List<Club> roundSchedule = new ArrayList<>();
      for (int j = 0; j < totalClubs; j++) {
        roundSchedule.add(clubs.get((i + j) % totalClubs)); // Rotate the clubs for each round
      }
      rotationSchedule.add(roundSchedule);
    }

    // Generate matches for each round
    for (int i = 0; i < numberOfRounds; i++) {
      LocalDateTime roundStartDate = startDate.plusDays(i * 7L); // Assuming rounds are scheduled weekly
      LocalDateTime roundEndDate = roundStartDate.plusDays(6); // Assuming each round lasts for a week

      Round round = new Round();
      round.setName("Round " + (i + 1)); // Set round name
      round.setStart_date(roundStartDate);
      round.setEnd_date(roundEndDate);
      round.setSeason(season);


      List<Match> matches = new ArrayList<>();

      // Generate matches for this round
      for (int j = 0; j < gamesPerRound; j++) {
        Club homeClub = rotationSchedule.get(i).get(j);
        Club awayClub;

        // Ensure awayClub is not the same as homeClub
        do {
          int randomAwayClubIndex = random.nextInt(totalClubs);
          awayClub = rotationSchedule.get(i).get(randomAwayClubIndex);
        } while (awayClub.equals(homeClub));

        // Generate match date
        int randomDays = random.nextInt(3);
        LocalDateTime matchDate = roundStartDate.plusDays(randomDays);

        // Create match
        Match match = new Match();
        match.setHomeTeamId(homeClub);
        match.setAwayTeamId(awayClub);
        match.setMatchDate(matchDate);
        match.setRound(round);
        match.setAwayTeamScore(0);
        match.setHomeTeamScore(0);
        match.setMatchStatus(MatchStatus.SCHEDULED);// Set the round for the match
        matches.add(match);
      }

      roundRepository.save(round);
      // Save all matches for this round
      matchRepository.saveAll(matches);

      // Add matches to the round and save again
      round.setMatches(matches);


      rounds.add(round);
    }

    return rounds;
  }


  @Override
  public void saveRounds(List<Round> rounds) {
    roundRepository.saveAll(rounds);
  }


//    @Override
//    public Optional<RoundDto> getRoundById(Long id) {
//        return roundRepository.findById(id).map(roundDtoMapper);
//    }

//    @Override
//    public Round createRound(Long seasonId, RoundDto roundDto) {
//        Season season = seasonRepository.findById(seasonId)
//                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + seasonId));
//
//        Round round = new Round(roundDto.getStart_date(), roundDto.getEnd_date());
//        round.setSeason(season);
//        Round savedRound = roundRepository.save(round);
//        List<Match> randomMatches = generateRandomMatches(round);
//
//        randomMatches.forEach(match -> {
//            match.setRound(savedRound);
//            matchRepository.save(match);
//        });
//
//        return savedRound;
//    }


    private List<Match> generateRandomMatches(Round round) {
        List<Match> randomMatches = new ArrayList<>();
        Random random = new Random();
        List<Club> availableClubs = clubRepository.findAll();
        LocalDateTime startDate = round.getStart_date();
        LocalDateTime endDate = round.getEnd_date();

        for (int i = 0; i < 5; i++) {
            Match randomMatch = new Match();
            Club homeTeam;
            Club awayTeam;
            do {
                homeTeam = getRandomClub(availableClubs, random);
                awayTeam = getRandomClub(availableClubs, random);
            } while (homeTeam.getId().equals(awayTeam.getId())); // Ensure home team and away team are different

            availableClubs.remove(homeTeam); // Remove selected clubs from available clubs list
            availableClubs.remove(awayTeam);

            randomMatch.setHomeTeamId(homeTeam);
            randomMatch.setAwayTeamId(awayTeam);
            randomMatch.setMatchDate(getRandomDateBetween(startDate, endDate, random));
            randomMatch.setStadium(null);
            randomMatch.setResult("Pending");
            randomMatch.setHomeTeamScore(0);
            randomMatch.setAwayTeamScore(0);
            randomMatches.add(randomMatch);
        }
        return randomMatches;
    }

    private Club getRandomClub(List<Club> availableClubs, Random random) {
        int randomIndex = random.nextInt(availableClubs.size());
        return availableClubs.get(randomIndex);
    }

    private LocalDateTime getRandomDateBetween(LocalDateTime start, LocalDateTime end, Random random) {
        long startEpochDay = start.toLocalDate().toEpochDay();
        long endEpochDay = end.toLocalDate().toEpochDay();
        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay);
        return LocalDateTime.of(LocalDate.ofEpochDay(randomEpochDay), LocalTime.of(random.nextInt(24), random.nextInt(60)));
    }


//    @Override
//    public List<MatchDTO> getMatchesByRoundId(Long roundId) {
//        Optional<Round> roundOptional = roundRepository.findById(roundId);
//        if (roundOptional.isPresent()) {
//            Round round = roundOptional.get();
//            List<Match> matches = matchRepository.findByRound(round);
//            return matches.stream()
//                    .map(match -> {
//                        Club homeTeam = clubRepository.findById(match.getHomeTeamId().getId()).orElse(null);
//                        Club awayTeam = clubRepository.findById(match.getAwayTeamId().getId()).orElse(null);
//
//                        return new MatchDTO(
//                                match.getId(),
//                                new ClubDto(homeTeam != null ? homeTeam.getId() : null,
//                                        homeTeam != null ? homeTeam.getName() : null,
//                                        homeTeam != null ? homeTeam.getFoundedYear() : null,
//                                        homeTeam != null ? homeTeam.getCity() : null,
//                                        homeTeam != null ? homeTeam.getWebsite() : null),
//                                new ClubDto(awayTeam != null ? awayTeam.getId() : null,
//                                        awayTeam != null ? awayTeam.getName() : null,
//                                        awayTeam != null ? awayTeam.getFoundedYear() : null,
//                                        awayTeam != null ? awayTeam.getCity() : null,
//                                        awayTeam != null ? awayTeam.getWebsite() : null),
//                                match.getMatchDate(),
//                                match.getStadium(),
//                                match.getResult(),
//                                match.getHomeTeamScore(),
//                                match.getAwayTeamScore()
//                        );
//                    })
//                    .collect(Collectors.toList());
//        } else {
//            return Collections.emptyList();
//        }
//    }


}
