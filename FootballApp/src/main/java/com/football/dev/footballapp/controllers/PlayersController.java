package com.football.dev.footballapp.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.dev.footballapp.dto.PageResponseDto;
import com.football.dev.footballapp.dto.PlayerDto;
import com.football.dev.footballapp.models.Player;
import com.football.dev.footballapp.security.JWTGenerator;
import com.football.dev.footballapp.services.FileUploadService;
import com.football.dev.footballapp.services.PlayerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInput;
import java.io.IOException;

@RestController
@RequestMapping("/api/players")
@CrossOrigin("http://localhost:4200")
public class PlayersController {
    private final PlayerService playerService;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;


    public PlayersController(PlayerService playerService,ObjectMapper objectMapper,FileUploadService fileUploadService) {
        this.playerService = playerService;
        this.fileUploadService=fileUploadService;
        this.objectMapper=objectMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<PlayerDto> createPlayer(@RequestParam("file") MultipartFile file,
                                               @RequestParam("playerDto") String playerDto) {

        try {
            PlayerDto playerDtoMapped = objectMapper.readValue(playerDto,PlayerDto.class);
            Boolean isUploaded = fileUploadService.uploadFile(file);
            if(!isUploaded) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            playerService.savePlayer(playerDtoMapped,file);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @GetMapping("/")
    public ResponseEntity<PageResponseDto<Player>> getPlayers(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<Player> playersPage = playerService.retrievePlayers(page,size);
        PageResponseDto<Player> responseDto = new PageResponseDto<>(
                playersPage.getContent(),
                playersPage.getNumber(),
                playersPage.getSize(),
                playersPage.getTotalElements()
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getPlayer(id));
    }
    @PostMapping("/edit/{id}")
    public ResponseEntity<String> editPlayer(@RequestBody PlayerDto playerDto, @PathVariable("id") Long id) {
        playerService.updatePlayer(playerDto,id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable("id") Long id){
        playerService.deletePlayer(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}