package com.example.mealRecommend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SearchFrequency {

    // Node class for the Binary Search Tree
    public static class Node {
        private String word;  // Keep the field private
        public int frequency;
        Node left, right;

        public Node(String word) {
            this.word = word;
            this.frequency = 0;
            this.left = this.right = null;
        }

        // Getter method for word
        public String getWord() {
            return word;
        }
    }



    // BST class for storing and managing the search data
    public static class BinarySearchTree {
        private Node root;

        // Method to insert or update a word in the BST
        public void insertOrUpdate(String word) {
            System.out.println("Inserting/updating word: " + word);  // Add this log to track what word is being processed
            root = insertOrUpdateRec(root, word.toLowerCase());
        }

        private Node insertOrUpdateRec(Node node, String word) {
            if (node == null) {
                System.out.println("Inserting new word: " + word); // Log when a new word is inserted
                return new Node(word); // Create new node if not found
            }
            if (word.compareTo(node.word) < 0) {
                node.left = insertOrUpdateRec(node.left, word);
            } else if (word.compareTo(node.word) > 0) {
                node.right = insertOrUpdateRec(node.right, word);
            } else {
                System.out.println("Updating word: " + word + ", current frequency: " + node.frequency); // Log when updating frequency
                node.frequency++; // Increment frequency if word already exists
            }
            return node;
        }

        public List<Map<String, Object>> getSearchHistoryAsList() {
            List<Map<String, Object>> searchHistory = new ArrayList<>();
            traverseInOrder(root, node -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("word", node.getWord());
                entry.put("frequency", node.frequency);
                searchHistory.add(entry);
            });
            return searchHistory;
        }
        public void clearHistory() {
            root = null;
        }
        
        private void traverseInOrder(Node node, Consumer<Node> action) {
            if (node != null) {
                traverseInOrder(node.left, action);
                action.accept(node);
                traverseInOrder(node.right, action);
            }
        }
    }
}