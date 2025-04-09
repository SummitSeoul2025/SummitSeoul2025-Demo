package com.demo.chillApp.controller.api;

import com.demo.chillApp.service.ChillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class ChillApiController {

    private final ChillService chillService;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> ask(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        Map<String, Object> response = new HashMap<>();

        String chillicornResponse = chillService.ask(question);

        response.put("response", chillicornResponse);
        response.put("isAnswer", chillService.isAnswer());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/answer")
    public ResponseEntity<Map<String, Object>> answer(@RequestBody Map<String, String> request) {
        String answer = request.get("answer");
        Map<String, Object> response = new HashMap<>();

        response.put("isCorrect", chillService.answer(answer));
        response.put("targetAnimal", chillService.getTargetAnimal());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset")
    public ResponseEntity<Void> resetGame() {
        chillService.resetGame();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/game-state")
    public ResponseEntity<Map<String, Object>> getGameState() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("questionCount", chillService.getQuestionCount());
        response.put("guessCount", chillService.getGuessCount());
        response.put("isAnswer", chillService.isAnswer());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/chat-history")
    public ResponseEntity<Map<String, Object>> getChatHistory() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("history", chillService.getChatHistory());
        
        return ResponseEntity.ok(response);
    }
}