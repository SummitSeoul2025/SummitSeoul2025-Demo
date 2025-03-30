package com.demo.chillApp.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Animal {
    // DOG("강아지"),
    // CAT("고양이"),
    // ELEPHANT("코끼리"),
    // GIRAFFE("기린"),
    // LION("사자"),
    // TIGER("호랑이"),
    // MONKEY("원숭이"),
    // PANDA("팬더"),
    // BEAR("곰"),
    // FOX("여우"),
    // WOLF("늑대"),
    // RABBIT("토끼"),
    // SQUIRREL("다람쥐"),
    // HIPPO("하마"),
    // KANGAROO("캥거루"),
    // KOALA("코알라"),
    // PENGUIN("펭귄"),
    // DOLPHIN("돌고래"),
    WHALE("고래");
    // SHARK("상어");

    private final String value;

    Animal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    // 랜덤 동물 선택 메서드
    public static Animal getRandomAnimal() {
        List<Animal> animals = Arrays.asList(Animal.values());
        Random random = new Random();
        return animals.get(random.nextInt(animals.size()));
    }
} 