package com.demo.chillApp.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatHistory {
    private final String question;
    private final String answer;
    private final LocalDateTime timestamp;
    private final boolean isAnswerAttempt;
    
    public ChatHistory(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.timestamp = LocalDateTime.now();
        this.isAnswerAttempt = false;
    }
    
    public ChatHistory(String message, boolean isAnswerAttempt) {
        this.question = message;
        this.answer = "";
        this.timestamp = LocalDateTime.now();
        this.isAnswerAttempt = isAnswerAttempt;
    }
}