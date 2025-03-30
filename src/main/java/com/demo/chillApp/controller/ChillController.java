package com.demo.chillApp.controller;

import com.demo.chillApp.service.ChillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChillController {

    private final ChillService chillService;

    // 정적 변수로 카운터 관리
    private static int questionCount = 0;
    private static int guessCount = 0;
    private static boolean isBubbleRed = false; // 말풍선 색상 상태

    @Autowired
    public ChillController(ChillService chillService) {
        this.chillService = chillService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // 모델에 카운터 값과 말풍선 색상 상태 추가
        model.addAttribute("questionCount", questionCount);
        model.addAttribute("guessCount", guessCount);
        model.addAttribute("isBubblseRed", isBubbleRed);
        
        // 챗 히스토리도 모델에 추가
        model.addAttribute("history", chillService.getChatHistory());
        
        return "index";
    }

    @PostMapping("/ask")
    public String ask(@RequestParam String question, Model model) {
        // 최대 질문 수 체크 또는 정답 시도 횟수가 최대인지 체크
        if (questionCount < 20 && guessCount < 3) {
            questionCount++;
            
            // 말풍선 색상 원래대로 변경
            isBubbleRed = false;
            
            // 질문 처리 로직
            String response = chillService.processQuestion(question);
            model.addAttribute("response", response);
        } else if (guessCount >= 3) {
            // 정답 시도 횟수가 최대인 경우
            model.addAttribute("response", "정답 시도 횟수(3회)를 모두 사용했습니다. 게임이 종료되었습니다!");
            isBubbleRed = true; // 빨간색 말풍선 유지
        } else {
            // 질문 횟수가 최대인 경우
            model.addAttribute("response", "이미 20개의 질문을 모두 사용했습니다!");
        }
        
        // 모델에 카운터 값과 말풍선 색상 상태 추가
        model.addAttribute("questionCount", questionCount);
        model.addAttribute("guessCount", guessCount);
        model.addAttribute("isBubbleRed", isBubbleRed);
        
        // 챗 히스토리도 모델에 추가
        model.addAttribute("history", chillService.getChatHistory());
        
        return "index";
    }
    
    @PostMapping("/answer")
    public String answer(@RequestParam String answer, Model model) {
        // 최대 정답 시도 횟수 체크
        if (guessCount < 3) {
            guessCount++;
            
            // 말풍선 색상을 빨간색으로 변경
            isBubbleRed = true;
            
            // 정답 처리 로직
            String response = chillService.checkAnswer(answer); // 이름 바꿈
            model.addAttribute("response", response);
        } else {
            model.addAttribute("response", "이미 3번의 정답 시도를 모두 사용했습니다!");
        }
        
        // 모델에 카운터 값과 말풍선 색상 상태 추가
        model.addAttribute("questionCount", questionCount);
        model.addAttribute("guessCount", guessCount);
        model.addAttribute("isBubbleRed", isBubbleRed);
        
        // 챗 히스토리도 모델에 추가
        model.addAttribute("history", chillService.getChatHistory());
        
        return "index";
    }

    // 게임 리셋 기능 추가
    @GetMapping("/reset")
    public String resetGame(Model model) {
        // 게임 상태 초기화
        questionCount = 0;
        guessCount = 0;
        isBubbleRed = false;
        
        // 새로운 동물 선택
        chillService.resetAnimal();
        
        // 모델에 초기화된 값 추가
        model.addAttribute("questionCount", questionCount);
        model.addAttribute("guessCount", guessCount);
        model.addAttribute("isBubbleRed", isBubbleRed);
        model.addAttribute("history", chillService.getChatHistory());
        model.addAttribute("response", "게임이 리셋되었습니다! 새로운 동물을 맞춰보세요.");
        
        return "index";
    }
} 