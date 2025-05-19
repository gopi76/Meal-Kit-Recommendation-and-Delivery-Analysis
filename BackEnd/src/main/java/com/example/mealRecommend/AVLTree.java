package com.example.mealRecommend;

import com.example.mealRecommend.util.AVLNode;
import java.util.*;

public class AVLTree {
    private AVLNode root;

    public void insert(String word) {
        if (word != null && !word.isEmpty()) {
            // Split the word by spaces
            String[] words = word.split("\\s+");

            // Loop through each word in the string and insert into the AVL tree
            for (String w : words) {
                w = w.toLowerCase();  // Convert the word to lowercase for consistency

                // Insert into the tree (perform AVL tree insertion logic here)
                AVLNode newNode = new AVLNode(w);
                if (root == null) {
                    root = newNode;
                } else {
                    AVLNode current = root;
                    AVLNode parent = null;

                    // Standard AVL insertion loop
                    while (current != null) {
                        parent = current;
                        if (w.compareTo(current.getWord()) < 0) {
                            current = current.getLeft();
                        } else if (w.compareTo(current.getWord()) > 0) {
                            current = current.getRight();
                        } else {
                            // Word already exists, no duplicates allowed
                            return;
                        }
                    }

                    // Insert the new node as the left or right child of the parent node
                    if (w.compareTo(parent.getWord()) < 0) {
                        parent.setLeft(newNode);
                    } else {
                        parent.setRight(newNode);
                    }

                    // Update height of all ancestors of the inserted node
                    current = parent;
                    while (current != null) {
                        current.setHeight(1 + Math.max(getHeight(current.getLeft()), getHeight(current.getRight())));

                        // Get the balance factor and perform rotations if necessary
                        int balance = getBalance(current);

                        if (balance > 1 && w.compareTo(current.getLeft().getWord()) < 0) {
                            current = rotateRight(current); // Left-Left case
                        } else if (balance < -1 && w.compareTo(current.getRight().getWord()) > 0) {
                            current = rotateLeft(current); // Right-Right case
                        } else if (balance > 1 && w.compareTo(current.getLeft().getWord()) > 0) {
                            current.setLeft(rotateLeft(current.getLeft())); // Left-Right case
                            current = rotateRight(current);
                        } else if (balance < -1 && w.compareTo(current.getRight().getWord()) < 0) {
                            current.setRight(rotateRight(current.getRight())); // Right-Left case
                            current = rotateLeft(current);
                        }

                        // Move up the tree to the parent
                        current = current.getParent();
                    }
                }
            }
        } else {
            System.out.println("Cannot insert null or empty string");
        }
    }

    private int getBalance(AVLNode current) {
        // If the node is null, return 0 (no height)
        if (current == null) {
            return 0;
        }

        // Get the height of the left and right subtrees
        int leftHeight = (current.getLeft() == null) ? 0 : current.getLeft().getHeight();
        int rightHeight = (current.getRight() == null) ? 0 : current.getRight().getHeight();

        // Return the balance factor
        return leftHeight - rightHeight;
    }



    // Recursive function to insert a word into the tree
    private AVLNode insertWord(AVLNode node, String word) {
        if (node == null) {
            return new AVLNode(word); // Create a new node if the current node is null
        }

        String nodeWord = node.getWord();

        // Compare the current word with the node's word
        int comparison = word.compareTo(nodeWord);

        if (comparison < 0) {
            // Word should be inserted in the left subtree
            node.setLeft(insertWord(node.getLeft(), word));
        } else if (comparison > 0) {
            // Word should be inserted in the right subtree
            node.setRight(insertWord(node.getRight(), word));
        } else {
            return node; // Word is a duplicate, so we don't insert it again
        }

        // Update height of the current node
        node.setHeight(Math.max(getHeight(node.getLeft()), getHeight(node.getRight())) + 1);

        // Balance the node and return the balanced node
        return balance(node);
    }

    // Balance the AVL tree to ensure it remains balanced
    private AVLNode balance(AVLNode node) {
        int balanceFactor = getBalanceFactor(node);

        // Left heavy case
        if (balanceFactor > 1) {
            if (getBalanceFactor(node.getLeft()) < 0) {
                node.setLeft(rotateLeft(node.getLeft())); // Left-Right case
            }
            return rotateRight(node); // Left-Left case
        }

        // Right heavy case
        if (balanceFactor < -1) {
            if (getBalanceFactor(node.getRight()) > 0) {
                node.setRight(rotateRight(node.getRight())); // Right-Left case
            }
            return rotateLeft(node); // Right-Right case
        }

        return node; // No balancing needed
    }

    // Rotate the node to the left
    private AVLNode rotateLeft(AVLNode node) {
        AVLNode newRoot = node.getRight();
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);

        // Update the heights after rotation
        node.setHeight(Math.max(getHeight(node.getLeft()), getHeight(node.getRight())) + 1);
        newRoot.setHeight(Math.max(getHeight(newRoot.getLeft()), getHeight(newRoot.getRight())) + 1);

        return newRoot;
    }

    // Rotate the node to the right
    private AVLNode rotateRight(AVLNode node) {
        AVLNode newRoot = node.getLeft();
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);

        // Update the heights after rotation
        node.setHeight(Math.max(getHeight(node.getLeft()), getHeight(node.getRight())) + 1);
        newRoot.setHeight(Math.max(getHeight(newRoot.getLeft()), getHeight(newRoot.getRight())) + 1);

        return newRoot;
    }

    // Get the height of a node
    private int getHeight(AVLNode node) {
        return node == null ? 0 : node.getHeight();
    }

    // Calculate the balance factor of a node
    private int getBalanceFactor(AVLNode node) {
        return node == null ? 0 : getHeight(node.getLeft()) - getHeight(node.getRight());
    }


    // This function returns a list of words that start with the given prefix

    public List<String> getSuggestedWords(String prefix) {
        List<String> suggestions = new ArrayList<>();
        search(root, prefix, suggestions);
        return suggestions;
    }

    // Recursive function to search for words starting with a prefix
    private void search(AVLNode node, String prefix, List<String> suggestions) {
        if (node == null) return;

        // If the word starts with the prefix, add it to the suggestions list
        if (node.getWord().startsWith(prefix)) {
            suggestions.add(node.getWord());
        }

        // Recursively search in both left and right subtrees
        if (node.getLeft() != null) search(node.getLeft(), prefix, suggestions);
        if (node.getRight() != null) search(node.getRight(), prefix, suggestions);
    }

    /*


    // This function returns a list of words that start with the given prefix
    public List<String> getSuggestedWords(String prefix) {
        List<String> suggestions = new ArrayList<>();
        search(root, prefix, suggestions);

        // Modify the suggestions list to include the closest matches based on edit distance
        for (int i = 0; i < suggestions.size(); i++) {
            for (int j = i + 1; j < suggestions.size(); j++) {
                if (calculateEditDistance(suggestions.get(i), prefix) > calculateEditDistance(suggestions.get(j), prefix)) {
                    // Swap if the edit distance of the second word is smaller
                    String temp = suggestions.get(i);
                    suggestions.set(i, suggestions.get(j));
                    suggestions.set(j, temp);
                }
            }
        }

        return suggestions;
    }

    // Recursive function to search for words starting with a prefix
    private void search(AVLNode node, String prefix, List<String> suggestions) {
        if (node == null) return;

        // If the word starts with the prefix, add it to the suggestions list
        if (node.getWord().startsWith(prefix)) {
            suggestions.add(node.getWord());
        }

        // Recursively search in both left and right subtrees
        if (node.getLeft() != null) search(node.getLeft(), prefix, suggestions);
        if (node.getRight() != null) search(node.getRight(), prefix, suggestions);
    }

    // Calculate the edit distance (Levenshtein distance) between two strings
    private int calculateEditDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        // Create a matrix to store results of subproblems
        int[][] dp = new int[m + 1][n + 1];

        // Fill the dp table
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j; // If word1 is empty, we need to insert all characters of word2
                } else if (j == 0) {
                    dp[i][j] = i; // If word2 is empty, we need to delete all characters of word1
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // If characters are the same, no operation is needed
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]); // Minimum of delete, insert, or replace
                }
            }
        }

        return dp[m][n];
    } */



}
