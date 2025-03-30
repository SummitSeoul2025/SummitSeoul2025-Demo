package com.demo.chillApp.service;

import com.demo.chillApp.model.Animal;
import com.demo.chillApp.model.ChatHistory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.core.SdkBytes;
import java.util.Map;
import java.util.HashMap;

@Service
public class ChillService {

    private final List<ChatHistory> chatHistory = new ArrayList<>();
    private Animal secretAnimal; // 맞춰야 할 동물
    private final BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper;
    
    @Value("${aws.bedrock.modelId}")
    private String modelId;
    
    // 생성자 주입 방식으로 변경
    public ChillService(BedrockRuntimeClient bedrockClient) {
        this.secretAnimal = Animal.getRandomAnimal();
        System.out.println("정답 동물: " + secretAnimal.getValue());
        
        this.bedrockClient = bedrockClient;
        this.objectMapper = new ObjectMapper();
    }
    
    // 동물 재설정
    public void resetAnimal() {
        this.secretAnimal = Animal.getRandomAnimal();
        System.out.println("새로운 정답 동물: " + secretAnimal.getValue());
        chatHistory.clear(); // 히스토리도 초기화
    }
    
    // 질문 처리 및 히스토리에 추가
    public String processQuestion(String question) {
        String answer = generateResponse(question);
        
        // 대화 히스토리에 추가
        chatHistory.add(new ChatHistory(question, answer));
        
        return answer;
    }
    
    // 베드락 API 응답을 예/아니오로 제한
    private String generateResponse(String question) {
        try {
            // 현재 선택된 동물 정보
            String animalName = secretAnimal.getValue();
            
            // 네/아니오 형식 응답만 지시하는 프롬프트
            String prompt = String.format(
                "당신은 스무고개 게임의 진행자인 'chill guy'입니다. 정답은 %s입니다.\n\n" +
                "사용자 질문: %s\n\n" +
                "규칙:\n" +
                "1. 반드시 예시 응답안에서 응답할 것\n" +
                "2. 다른 어떤 설명이나 부가 정보도 제공하지 말 것\n" +
                "3. 정답 동물의 특성에 맞게 정확히 답변할 것\n" +
                "4. 답변은 짧게 하되, 친근하고 편안한 '칠가이' 말투로 할 것\n" +
                "5. 예시 응답: '네~', '아니오~', '응!', '아니!', '맞아yo~', '아니지~'\n" +
                "6. 'chill guy' 말투 특징:\n" +
                "   - 항상 편안하고 여유로운 톤 유지\n" +
                "   - 가끔 'yo', 'man' 같은 친근한 표현 사용\n" +
                "   - 문장 끝에 '~'를 자주 사용하여 부드러운 느낌 줌\n" +
                "   - 공식적인 말투 대신 구어체 사용\n" +
                "7. 20%% 확률로 답변 뒤에 'ㅋ', 'ㅎ' 같은 이모티콘 추가\n\n" +
                "chill guy의 응답:",
                animalName, question);
          
            // 베드락 API 요청 구성 (나머지 코드는 동일)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("anthropic_version", "bedrock-2023-05-31");
            requestBody.put("max_tokens", 10); // 단답형이므로 토큰 수 줄임
            requestBody.put("temperature", 0.0); // 결정론적 응답
            
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(message);
            
            requestBody.put("messages", messages);
            
            // JSON 변환
            String requestJson = objectMapper.writeValueAsString(requestBody);
            
            // Bedrock API 호출
            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .body(SdkBytes.fromUtf8String(requestJson))
                    .build();
            
            InvokeModelResponse response = bedrockClient.invokeModel(request);
            
            // 응답 처리
            String responseJson = response.body().asUtf8String();
            Map<String, Object> responseMap = objectMapper.readValue(responseJson, Map.class);
            
            // Claude 응답 추출
            if (responseMap.containsKey("content")) {
                List<Map<String, Object>> contentList = (List<Map<String, Object>>) responseMap.get("content");
                for (Map<String, Object> content : contentList) {
                    if (content.containsKey("text")) {
                        return (String) content.get("text").toString().trim();
                    }
                }
            }
            
            return "API 응답을 처리할 수 없습니다.";
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("베드락 API 호출 실패: " + e.getMessage());
            
            // 폴백 응답 - 간단한 네/아니오 응답
            return "API 호출 중 오류가 발생했습니다.";
        }
    }
    
    // 정답 확인 (히스토리에 추가하지 않음)
    public String checkAnswer(String answer) {
        // 정답 확인 로직
        boolean isCorrect = checkIfCorrectAnswer(answer);
        
        if (isCorrect) {
            resetAnimal(); // 정답을 맞췄으니 새 동물로 재설정
            return "축하합니다! 정답입니다! 🎉 새로운 동물이 선택되었습니다. 다시 게임을 시작하세요!";
        } else {
            return "아쉽네요, 정답이 아닙니다. 다시 시도해보세요!";
        }
    }
    
    // 정답 확인 로직
    private boolean checkIfCorrectAnswer(String answer) {
        String trimmedAnswer = answer.trim().toLowerCase();
        String animalName = secretAnimal.getValue().toLowerCase();
        
        // 한글 이름으로 비교
        return trimmedAnswer.contains(animalName);
    }
    
    // 전체 대화 히스토리 반환
    public List<ChatHistory> getChatHistory() {
        return chatHistory;
    }
} 