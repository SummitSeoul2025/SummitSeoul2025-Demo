package com.demo.chillApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Getter
@AllArgsConstructor
public enum Animal {
    DOG("강아지"),
    ELEPHANT("코끼리"),
    GIRAFFE("기린"),
    LION("사자"),
    WHALE("고래");

    private final String value;

    public static Animal getRandomAnimal() {
        List<Animal> animals = List.of(Animal.values());
        Random random = new SecureRandom();
        return animals.get(random.nextInt(animals.size()));
    }
}