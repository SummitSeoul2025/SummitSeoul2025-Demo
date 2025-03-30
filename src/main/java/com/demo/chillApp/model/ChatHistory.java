package com.demo.chillApp.model;

import java.time.LocalDateTime;

public class ChatHistory {
    private Long id;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    
    // 기본 생성자
    public ChatHistory() {
        this.timestamp = LocalDateTime.now();
    }
    
    // 파라미터가 있는 생성자
    public ChatHistory(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getter와 Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 