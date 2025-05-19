package com.example.mealRecommend.util;

public class AVLNode {
    private String word;
    private AVLNode left, right, parent; // Add the parent reference
    private int height;

    // Constructor
    public AVLNode(String word) {
        this.word = word; // Correctly initialize the word
        this.left = this.right = this.parent = null; // Initialize parent as null
        this.height = 1;
    }

    // Getter and Setter methods for word, left, right, height, and parent
    public String getWord() {
        return word;
    }

    public AVLNode getLeft() {
        return left;
    }

    public void setLeft(AVLNode left) {
        this.left = left;
    }

    public AVLNode getRight() {
        return right;
    }

    public void setRight(AVLNode right) {
        this.right = right;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public AVLNode getParent() {
        return parent; // Return the parent of this node
    }

    public void setParent(AVLNode parent) {
        this.parent = parent; // Set the parent of this node
    }
}
