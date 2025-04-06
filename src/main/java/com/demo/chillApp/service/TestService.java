package com.demo.chillApp.service;

import com.demo.chillApp.model.Animal;
import com.demo.chillApp.model.ChatHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class TestService {
    // 1. 보안 취약점: public static 변수 노출
    public static String API_KEY = "sk_test_12345";

    // 2. 메모리 누수 가능성: 리소스 미닫힘
    public void readUserData(String fileName) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while(scanner.hasNext()) {
            // 처리 중 예외가 발생하면 scanner가 닫히지 않음
            processLine(scanner.nextLine());
        }
    }

    // 4. Null 체크 누락
    public int calculateUserScore(User user) {
        return user.getPoints() * 10;  // NullPointerException 위험
    }

    private void processLine(String s) {
    }

    // 10. 하드코딩된 설정값
    private static final String DATABASE_URL = "jdbc:mysql://production.server:3306/userdb";
}

class User {
    private String name;
    private String email;
    private int points;
    private double balance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
