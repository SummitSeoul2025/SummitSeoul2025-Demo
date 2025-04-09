package com.demo.chillApp.service;

import com.demo.chillApp.model.Animal;
import com.demo.chillApp.model.ChatHistory;
import lombok.Getter;
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

    @Value("${aws.bedrock.modelId}")
    private String modelId;

    private Animal secretAnimal;
    @Getter
    private final List<ChatHistory> chatHistory;
    private final BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper;

    @Getter
    private int questionCount;
    @Getter
    private int guessCount;
    private boolean isAnswer;

    public ChillService(BedrockRuntimeClient bedrockClient) {
        this.secretAnimal = Animal.getRandomAnimal();
        this.chatHistory = new ArrayList<>();
        this.bedrockClient = bedrockClient;
        this.objectMapper = new ObjectMapper();
        this.questionCount= 0;
        this.guessCount = 0;
        this.isAnswer = false;
        System.out.println("정답 동물: " + secretAnimal.getValue());
    }

    public String ask(String question) {
        if (getQuestionCount() < 20 && getGuessCount() < 3) {
            questionCount++;

            isAnswer = false;

            return processQuestion(question);
        } else if (getGuessCount() >= 3) {
            isAnswer = true;
        }
        return "";
    }

    public boolean answer(String answer) {
        if (getGuessCount() >= 3) {
            return false;
        }
        guessCount++;
        isAnswer = true;
        
        boolean isCorrect = checkAnswer(answer);
        // 정답 시도 기록 추가
        String attemptMessage = isCorrect ? "정답 시도(정답): " : "정답 시도(오답): ";
        chatHistory.add(new ChatHistory(attemptMessage + answer, true));
        
        return isCorrect;
    }

    public void resetGame() {
        isAnswer = false;
        questionCount = 0;
        guessCount = 0;
        resetAnimal();
    }

    public void resetAnimal() {
        this.secretAnimal = Animal.getRandomAnimal();
        chatHistory.clear(); 
    }
 
    public String processQuestion(String question) {
        String answer = generateResponse(question);

        chatHistory.add(new ChatHistory(question, answer));

        return answer;
    }

    private String generateResponse(String question) {
        try {
            String animalName = secretAnimal.getValue();

            String prompt = String.format(
                "당신은 스무고개 게임의 진행자입니다. 정답 동물은 '%s'입니다.\n\n" +
                "사용자 질문: %s\n\n" +
                "규칙:\n" +
                "1. 사용자 질문에 대해 정답 동물의 특성에 맞게 정확하게 '맞아' 또는 '아니'로 대답하세요.\n" +
                "2. 정답 동물이 갖고 있는 특성이나 사실에 대해 물어보면 '맞아~'로 대답하세요.\n" +
                "3. 정답 동물이 갖고 있지 않은 특성이나 사실에 대해 물어보면 '아니~'로 대답하세요.\n" +
                "4. 정확하게 동물의 특성을 판단하세요. 예를 들어 고양이는 육지에 살고, 고래는 물에 삽니다.\n\n" +
                "응답은 다음 중 하나만 선택하세요: '맞아~', '아니~', '응!', '아니!', '아니지~', '맞아!'\n\n" +
                "정확한 사실에 기반해 대답하세요. 예시:\n" +
                "답변:",
                animalName, question);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("anthropic_version", "bedrock-2023-05-31");
            requestBody.put("max_tokens", 5); 
            requestBody.put("temperature", 0.0);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(message);

            requestBody.put("messages", messages);

            String requestJson = objectMapper.writeValueAsString(requestBody);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .body(SdkBytes.fromUtf8String(requestJson))
                    .build();

            InvokeModelResponse response = bedrockClient.invokeModel(request);

            String responseJson = response.body().asUtf8String();
            Map<String, Object> responseMap = objectMapper.readValue(responseJson, Map.class);

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

            return "API 호출 중 오류가 발생했습니다.";
        }
    }

    public boolean checkAnswer(String answer) {
        return answer.trim().equalsIgnoreCase(secretAnimal.getValue());
    }

    public String getTargetAnimal() {
        return secretAnimal.getValue();
    }

    public boolean isAnswer() {
        return isAnswer;
    }
}