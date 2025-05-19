package com.example.mealRecommend.controller;

import com.example.mealRecommend.model.Recipe;
import com.example.mealRecommend.service.WebCrawlerService;
import com.example.mealRecommend.util.RedBlackTree;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SearchController {
    private final RedBlackTree<String, List<Integer>> redBlackTree;

    public SearchController() {
        redBlackTree = new RedBlackTree<>();

        // Populate the red-black tree with mock vocabulary and indices
        String[] vocabulary = { "vegetarian", "vegan", "keto", "gluten-free", "organic", "local",
                "sustainable", "delivery", "weekly", "meals", "servings", "family",
                "single", "diet", "healthy", "fresh", "quick", "easy" };

        for (int i = 0; i < vocabulary.length; i++) {
            String word = vocabulary[i];
            List<Integer> indices = redBlackTree.search(word);

            if (indices == null) {
                indices = new ArrayList<>();
            }
            indices.add(i); // Mock index
            redBlackTree.insert(word, indices);
        }
    }

    // Endpoint to search for a word
    @GetMapping("/search")
    public List<Integer> searchWord(@RequestParam String word) {
        List<Integer> result = redBlackTree.search(word.toLowerCase());
        return result != null ? result : new ArrayList<>();
    }

    @GetMapping("/search/pattern")
    public List<Recipe> searchByPattern(@RequestParam String pattern) {
        return WebCrawlerService.findPatternInRecipes(pattern);
    }
}
