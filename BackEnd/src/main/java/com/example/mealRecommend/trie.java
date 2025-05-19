package com.example.mealRecommend;

import com.example.mealRecommend.util.TrieNode;

import java.util.ArrayList;
import java.util.List;

public class trie {
    private TrieNode root;

    public trie() {
        root = new TrieNode();
    }

    /*
    // Edit Distance Function (Levenshtein Distance)
    private int editDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j; // If word1 is empty, we insert all characters from word2
                } else if (j == 0) {
                    dp[i][j] = i; // If word2 is empty, we remove all characters from word1
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No change needed if characters match
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])); // Find the minimum edit distance
                }
            }
        }
        return dp[word1.length()][word2.length()];
    } */

    private int editDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j; // If word1 is empty, we insert all characters from word2
                } else if (j == 0) {
                    dp[i][j] = i; // If word2 is empty, we remove all characters from word1
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No change needed if characters match
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])); // Find the minimum edit distance
                }
            }
        }
        return dp[word1.length()][word2.length()];
    }

    // Find Closest Match Function
    public String findClosestWord(String input, List<String> dictionary) {
        String closestWord = null;
        int minDistance = Integer.MAX_VALUE;

        // Preprocess input to remove excessive repeated characters
        String processedInput = removeExcessiveChars(input);

        // Compare the processed input against the dictionary
        for (String word : dictionary) {
            int distance = editDistance(processedInput, word);
            if (distance < minDistance) {
                minDistance = distance;
                closestWord = word;
            }
        }
        return closestWord;
    }

    // Preprocess Input to Remove Excessive Repeated Characters
    private String removeExcessiveChars(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            // Only add the character if it's not the same as the last character added
            if (i == 0 || input.charAt(i) != input.charAt(i - 1)) {
                sb.append(input.charAt(i));
            }
        }
        return sb.toString();
    }

    // Method to suggest alternative words based on edit distance
    public List<String> getSuggestedWords(String word) {
        List<String> suggestedwords = new ArrayList<>();
        List<String> allWords = new ArrayList<>();

        // Get all words from the Trie
        getAllWords(root, "", allWords);

        // Find words with an edit distance of 1 or 2
        for (String vocabWord : allWords) {
            int distance = editDistance(word, vocabWord);
            if (distance <= 2) { // Only suggest words that are similar
                suggestedwords.add(vocabWord);
            }
        }

        return suggestedwords;
    }

    // Helper function to get all words from the Trie
    private void getAllWords(TrieNode node, String prefix, List<String> words) {
        if (node.isEndOfWord()) {
            words.add(prefix);
        }
        for (char c : node.getChildren().keySet()) {
            getAllWords(node.getChildren().get(c), prefix + c, words);
        }
    }

    // Insert method to handle spaces in recipe names
    public void insert(String phrase) {
        String[] words = phrase.split(" "); // Split the phrase into individual words
        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node = node.getChildren().computeIfAbsent(c, k -> new TrieNode());
            }
            node.setEndOfWord(true);
        }
    }

    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.getChildren().get(c);
            if (node == null) {
                return false;
            }
        }
        return node.isEndOfWord();
    }

    public List<String> getCompletions(String prefix) {
        List<String> completions = new ArrayList<>();
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.getChildren().get(c);
            if (node == null) {
                return completions; // No completions found
            }
        }
        findAllWords(node, prefix, completions);
        return completions;
    }

    private void findAllWords(TrieNode node, String prefix, List<String> results) {
        if (node.isEndOfWord()) {
            results.add(prefix);
        }
        for (char c : node.getChildren().keySet()) {
            findAllWords(node.getChildren().get(c), prefix + c, results);
        }
    }
}
