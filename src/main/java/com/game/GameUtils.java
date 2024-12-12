package com.game;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameUtils {
    public static String generateUniqueNumber() {
        List<Integer> digits = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        Collections.shuffle(digits);
        return digits.subList(0, 4).stream().map(String::valueOf).collect(Collectors.joining());
    }
}

