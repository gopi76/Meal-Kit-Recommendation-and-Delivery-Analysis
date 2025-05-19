package com.example.mealRecommend.util;

import java.util.HashMap;
import java.util.Map;

public class frequency {
    // New method to process text directly
    public static Map<String, Integer> getWordFrequenciesFromText(String text) {
        // Process text to get word frequencies
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");

        Map<String, Integer> wordFrequencies = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
            }
        }

        return wordFrequencies;
    }
}