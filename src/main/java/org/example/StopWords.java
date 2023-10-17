package org.example;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StopWords {
    public static Set<String> positive = new HashSet<>();
    public static Set<String> negative = new HashSet<>();

    static{
//        negative.addAll(Arrays.asList("не советую", "не стоит", "не устранились", ""));
//        positive.addAll(Arrays.asList("советую", "стоит", ""));
    }
}
