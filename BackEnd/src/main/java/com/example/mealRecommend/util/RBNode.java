package com.example.mealRecommend.util;

public class RBNode<K extends Comparable<K>, V> {
    public static final boolean RED = true;
    public static final boolean BLACK = false;

    private K key; // The key of the node
    private V value; // The value associated with the key
    private RBNode<K, V> left, right, parent; // Pointers to child nodes and parent
    private boolean color; // Node color (RED or BLACK)

    // Constructor
    public RBNode(K key, V value, boolean color, RBNode<K, V> parent) {
        this.key = key;
        this.value = value;
        this.color = color;
        this.parent = parent;
    }

    // Getters and setters
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public RBNode<K, V> getLeft() {
        return left;
    }

    public void setLeft(RBNode<K, V> left) {
        this.left = left;
    }

    public RBNode<K, V> getRight() {
        return right;
    }

    public void setRight(RBNode<K, V> right) {
        this.right = right;
    }

    public RBNode<K, V> getParent() {
        return parent;
    }

    public void setParent(RBNode<K, V> parent) {
        this.parent = parent;
    }

    public boolean isRed() {
        return color == RED;
    }

    public boolean isBlack() {
        return color == BLACK;
    }

    public void setColor(boolean color) {
        this.color = color;
    }
}
