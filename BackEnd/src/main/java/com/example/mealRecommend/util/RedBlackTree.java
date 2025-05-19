package com.example.mealRecommend.util;

public class RedBlackTree<K extends Comparable<K>, V> {
    private RBNode<K, V> root; // Root node of the tree

    // Constructor
    public RedBlackTree() {
        this.root = null;
    }

    // Public method to insert a key-value pair
    public void insert(K key, V value) {
        RBNode<K, V> newNode = new RBNode<>(key, value, RBNode.RED, null);
        root = insertNode(root, newNode);
        fixViolations(newNode);
    }

    // Helper method for insertion
    private RBNode<K, V> insertNode(RBNode<K, V> current, RBNode<K, V> newNode) {
        if (current == null) {
            return newNode;
        }

        if (newNode.getKey().compareTo(current.getKey()) < 0) {
            current.setLeft(insertNode(current.getLeft(), newNode));
            current.getLeft().setParent(current);
        } else if (newNode.getKey().compareTo(current.getKey()) > 0) {
            current.setRight(insertNode(current.getRight(), newNode));
            current.getRight().setParent(current);
        }

        return current;
    }

    // Fix violations after insertion
    private void fixViolations(RBNode<K, V> node) {
        RBNode<K, V> parent, grandParent;

        while (node != root && node.isRed() && node.getParent().isRed()) {
            parent = node.getParent();
            grandParent = parent.getParent();

            // Left Case
            if (parent == grandParent.getLeft()) {
                RBNode<K, V> uncle = grandParent.getRight();

                // Case 1: Uncle is red
                if (uncle != null && uncle.isRed()) {
                    grandParent.setColor(RBNode.RED);
                    parent.setColor(RBNode.BLACK);
                    uncle.setColor(RBNode.BLACK);
                    node = grandParent;
                } else {
                    // Case 2: Node is the right child
                    if (node == parent.getRight()) {
                        rotateLeft(parent);
                        node = parent;
                        parent = node.getParent();
                    }

                    // Case 3: Node is the left child
                    rotateRight(grandParent);
                    boolean tempColor = parent.isRed();
                    parent.setColor(grandParent.isRed());
                    grandParent.setColor(tempColor);
                    node = parent;
                }
            }
            // Right Case
            else {
                RBNode<K, V> uncle = grandParent.getLeft();

                // Case 1: Uncle is red
                if (uncle != null && uncle.isRed()) {
                    grandParent.setColor(RBNode.RED);
                    parent.setColor(RBNode.BLACK);
                    uncle.setColor(RBNode.BLACK);
                    node = grandParent;
                } else {
                    // Case 2: Node is the left child
                    if (node == parent.getLeft()) {
                        rotateRight(parent);
                        node = parent;
                        parent = node.getParent();
                    }

                    // Case 3: Node is the right child
                    rotateLeft(grandParent);
                    boolean tempColor = parent.isRed();
                    parent.setColor(grandParent.isRed());
                    grandParent.setColor(tempColor);
                    node = parent;
                }
            }
        }
        root.setColor(RBNode.BLACK);
    }

    // Left rotation
    private void rotateLeft(RBNode<K, V> node) {
        RBNode<K, V> rightChild = node.getRight();
        node.setRight(rightChild.getLeft());

        if (node.getRight() != null) {
            node.getRight().setParent(node);
        }

        rightChild.setParent(node.getParent());

        if (node.getParent() == null) {
            root = rightChild;
        } else if (node == node.getParent().getLeft()) {
            node.getParent().setLeft(rightChild);
        } else {
            node.getParent().setRight(rightChild);
        }

        rightChild.setLeft(node);
        node.setParent(rightChild);
    }

    // Right rotation
    private void rotateRight(RBNode<K, V> node) {
        RBNode<K, V> leftChild = node.getLeft();
        node.setLeft(leftChild.getRight());

        if (node.getLeft() != null) {
            node.getLeft().setParent(node);
        }

        leftChild.setParent(node.getParent());

        if (node.getParent() == null) {
            root = leftChild;
        } else if (node == node.getParent().getLeft()) {
            node.getParent().setLeft(leftChild);
        } else {
            node.getParent().setRight(leftChild);
        }

        leftChild.setRight(node);
        node.setParent(leftChild);
    }

    // Utility method to search for a key
    public V search(K key) {
        RBNode<K, V> current = root;

        while (current != null) {
            if (key.compareTo(current.getKey()) < 0) {
                current = current.getLeft();
            } else if (key.compareTo(current.getKey()) > 0) {
                current = current.getRight();
            } else {
                return current.getValue();
            }
        }
        return null;
    }
}
