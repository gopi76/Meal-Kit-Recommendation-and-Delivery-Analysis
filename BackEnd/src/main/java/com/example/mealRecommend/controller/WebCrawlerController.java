package com.example.mealRecommend.controller;

import com.example.mealRecommend.model.Recipe;
import com.example.mealRecommend.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.mealRecommend.SearchFrequency;
import com.example.mealRecommend.util.frequency;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping({"/search", "/search11"})
public class WebCrawlerController {

    @Autowired
    private WebCrawlerService webCrawlerService;
    //private WebScraper webScraper;
    private static final SearchFrequency.BinarySearchTree searchHistory = new SearchFrequency.BinarySearchTree();
    private frequency wordFrequencyCounter = new frequency();
    // Endpoint to update search history
    @PostMapping("/updateSearchHistory")
    public void updateSearchHistory(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        if (query != null && !query.isEmpty()) {
            searchHistory.insertOrUpdate(query.trim());
        }
    }
    // Endpoint to fetch search history
    @GetMapping("/searchHistory")
    public List<Map<String, Object>> displaySearchHistory() {
        return searchHistory.getSearchHistoryAsList();
    }
    @DeleteMapping("/searchHistory")
    public ResponseEntity<Void> clearSearchHistory() {
        searchHistory.clearHistory(); // Call your service method to clear the history
        return ResponseEntity.noContent().build();
    }
    // Load and merge data from all sources at application startup
    @PostConstruct
    public void loadInitialData() {
        webCrawlerService.init(); // Ensure the initialization method aligns with the service
    }

    // Get suggestions based on the prefix (using webCrawlerService)
    @GetMapping("/suggestions")
    public List<String> getSuggestions(@RequestParam String prefix) {
        return webCrawlerService.getSuggestions(prefix);  // Ensure webCrawlerService has the correct method
    }

    // Get suggested words from the WebScraper (ensure this method aligns with functionality)
    @GetMapping("/suggestedwords")
    public List<String> getAlternatives(@RequestParam String prefix) {
        return webCrawlerService.getAlternatives(prefix);  // Ensure WebScraper's method is relevant here
    }


    // Search recipes based on a user query
    @PostMapping("/recipes")
    public Map<String, Object> searchRecipes(@RequestParam String query) {
        List<Recipe> searchResults = webCrawlerService.searchRecipes(query);
        Map<String, Object> response = new HashMap<>();
        try {
            // Calculate word frequencies
            List<Recipe> allRecipes = webCrawlerService.getAllRecipes();
            StringBuilder recipeContent = new StringBuilder();
            for (Recipe recipe : allRecipes) {
                recipeContent.append(recipe.getName()).append(" ");
                recipeContent.append(String.join(" ", recipe.getdietaryOptions())).append(" ");
            }
            Map<String, Integer> wordFrequencies = wordFrequencyCounter.getWordFrequenciesFromText(recipeContent.toString());
            String[] searchWords = query.toLowerCase().split("\\s+");
            Map<String, Integer> searchedWordFrequencies = new HashMap<>();
            for (String word : searchWords) {
                searchedWordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0));
            }
            response.put("recipes", searchResults);
            response.put("wordCount", searchWords.length);
            response.put("wordFrequencies", searchedWordFrequencies);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error processing word frequencies");
        }
        // Update search history once
        //searchHistory.insertOrUpdate(query.trim());
        return response;
    }

    // Get all recipes (consider pagination if the list is large)
    @GetMapping("/all")
    public List<Recipe> getAllRecipes() {
        return webCrawlerService.getAllRecipes();  // Ensure webCrawlerService has this method implemented
    }
}
