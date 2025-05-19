package com.example.mealRecommend.service;

import com.example.mealRecommend.AVLTree;
import com.example.mealRecommend.model.Recipe;
import com.example.mealRecommend.trie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.mealRecommend.SearchFrequency;
import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebCrawlerService {
    private static final double PRICE_PER_SERVING = 11.00;
    private static List<Recipe> mergedRecipes = new ArrayList<>();
        private trie trie = new trie();
        private AVLTree avlTree = new AVLTree();  // AVL Tree instance
        private boolean initialized = false;
        private SearchFrequency.BinarySearchTree searchHistory = new SearchFrequency.BinarySearchTree();
        private Map<Integer, String> websiteOptions = new HashMap<>();
    
        @Autowired
        private WebScraper webScraper;
    
        @PostConstruct
        public synchronized void init() {
            if (initialized) {
                return; // Ensure scraping runs only once
            }
    
            System.out.println("Starting Web Scraping...");
            List<Recipe> allRecipes = new ArrayList<>();
    
            try {
                // Centralized scraping execution
    
                allRecipes.addAll(scrapeRecipesFromFreshPrep());
               //allRecipes.addAll(webScraper.scrapeRecipes());
                //allRecipes.addAll(VedaService.scrapeRecipes());
                //allRecipes.addAll(DinnerlyRecipeScraper.scrapeRecipes());
                //allRecipes.addAll(SnapKitchenService.scrapeRecipes());
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            // Merge recipes and remove duplicates
            mergedRecipes = mergeRecipes(allRecipes);
    
            // Populate the Trie and AVL Tree for suggestions and word storage
            populateTrie();
            populateAVLTree();
    
            initialized = true;
            System.out.println("Web Scraping Completed. Total Recipes: " + mergedRecipes.size());
        }
    
        public List<Recipe> getAllRecipes() {
            return new ArrayList<>(mergedRecipes);
        }
        public static List<Recipe> findPatternInRecipes(String pattern) {
        List<Recipe> matchingRecipes = new ArrayList<>();
    
        // Normalize pattern for case-insensitivity
        String lowerCasePattern = pattern.toLowerCase();
    
        for (Recipe recipe : mergedRecipes) {
        // Normalize recipe name
        String recipeName = recipe.getName().toLowerCase();

        // Use KMP to check if the pattern exists in the recipe name
        if (kmpSearch(recipeName, lowerCasePattern)) {
                    matchingRecipes.add(recipe);
                }
            }
        
            return matchingRecipes;
        }
        
        // KMP Search Implementation
        private static boolean kmpSearch(String text, String pattern) {
    int n = text.length();
    int m = pattern.length();

    // Preprocess the pattern to build the LPS array
    int[] lps = buildLPS(pattern);
    
        int i = 0; // index for text
        int j = 0; // index for pattern
    
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
    
            if (j == m) {
                // Found the pattern
                return true;
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
    
        return false;
    }
    
    // Build the Longest Prefix Suffix (LPS) array
    private static int[] buildLPS(String pattern) {
    int m = pattern.length();
    int[] lps = new int[m];
    int length = 0; // Length of the previous longest prefix suffix
    int i = 1;

    while (i < m) {
        if (pattern.charAt(i) == pattern.charAt(length)) {
            length++;
            lps[i] = length;
            i++;
        } else {
            if (length != 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
    }

    return lps;
}

    public List<String> getSuggestions(String prefix) {
        return trie.getCompletions(prefix.toLowerCase());
    }

    // Populating AVL Tree with words from recipe names
    private void populateAVLTree() {
        for (Recipe recipe : mergedRecipes) {
            // Split the recipe name by spaces and insert each word into the AVL Tree
            String[] words = recipe.getName().toLowerCase().split("\\s+");
            for (String word : words) {
                if (word != null && !word.isEmpty()) {
                    avlTree.insert(word);  // Insert only non-null, non-empty words
                    //System.out.println(word);
                }
            }
        }
    }

    // Method to get suggested words from the AVL Tree based on a prefix
    public List<String> getAlternatives(String prefix) {
        return trie.getSuggestedWords(prefix.toLowerCase());
    }

    /*
    public List<Recipe> searchRecipes(String query) {
        String lowerCaseQuery = query.toLowerCase();
        return mergedRecipes.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains(lowerCaseQuery) ||
                        recipe.getdietaryOptions().stream().anyMatch(option -> option.toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    } */

    public List<Recipe> searchRecipes(String query) {
        searchHistory.insertOrUpdate(query); // Record the search in the history
        String lowerCaseQuery = query.toLowerCase();

        // Use the internally managed `mergedRecipes`
        List<Recipe> recipes = new ArrayList<>(mergedRecipes);

        // Define a default sorting mechanism, e.g., by price
        String sortBy = "price";  // You can change this to "cookingTime" or any other valid value.

        // Filter recipes based on the query
        List<Recipe> filteredRecipes = recipes.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains(lowerCaseQuery) ||
                        (recipe.getdietaryOptions() != null &&
                                recipe.getdietaryOptions().stream()
                                        .anyMatch(option -> option.toLowerCase().contains(lowerCaseQuery))))
                .collect(Collectors.toList());

        // Sort recipes based on the sortBy criteria
        Comparator<Recipe> comparator = switch (sortBy) {
            case "price" -> Comparator.comparingDouble(Recipe::getParsedPrice).reversed();
            case "cookingTime" ->  // Keep "cookingTime" as it is
                    Comparator.comparingInt(Recipe::getParsedCookingTime).reversed();
            default -> (r1, r2) -> 0; // No sorting
        };

        // Ensure you match the case exactly for sorting by "price" or "cookingTime"

        filteredRecipes.sort(comparator);

        return filteredRecipes;
    }







    private List<Recipe> scrapeRecipesFromFreshPrep() {
        WebDriver driver = initializeDriver();
        List<Recipe> recipes = new ArrayList<>();

        try {
            driver.get("https://www.freshprep.ca/menu/this-week");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".row")));

            List<WebElement> recipeColumns = driver.findElements(By.xpath("//div[@class='row']//div[@class='recipe-col']"));

            for (WebElement recipeCol : recipeColumns) {
                Recipe recipe = new Recipe();
                String recipeName = recipeCol.findElement(By.xpath(".//h3")).getText();
                String imgUrl = recipeCol.findElement(By.xpath(".//img[@class='logo lazyload']")).getAttribute("src");

                List<WebElement> dietaryIcons = recipeCol.findElements(By.xpath(".//div[@class='recipe-icons']//img[contains(@src, 'dietary-icons-v-2/')]"));
                List<String> iconNames = dietaryIcons.stream()
                        .map(icon -> icon.getAttribute("src"))
                        .map(src -> src.substring(src.lastIndexOf('/') + 1, src.indexOf('.', src.lastIndexOf('/'))))
                        .collect(Collectors.toList());

                String servesCount = recipeCol.findElement(By.xpath(".//div[contains(@class, 'info-title') and text()='Serves']/following-sibling::div")).getText();
                String cookingtime = recipeCol.findElement(By.xpath(".//div[contains(@class, 'info-title') and text()='Time']/following-sibling::div")).getText();
                String recipeURL = recipeCol.findElement(By.xpath(".//a[starts-with(@href, '/product/')]")).getAttribute("href");
                recipe.setWebsiteName("FreshPrep");
                recipe.setWebsiteURL(recipeURL);
                recipe.setName(recipeName);
                recipe.setImageUrl(imgUrl);
                recipe.setdietaryOptions(extractDietaryOptions(String.join(" ", iconNames)));
                recipe.setServes(servesCount);
                recipe.setCookingTime(cookingtime);

                try {
                    int numberOfServings = Integer.parseInt(servesCount.replaceAll("[^0-9]", ""));
                    recipe.setPrice(String.format("$%.2f", numberOfServings * PRICE_PER_SERVING));
                } catch (NumberFormatException e) {
                    recipe.setPrice("N/A");
                }

                recipes.add(recipe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return recipes;
    }

    private WebDriver initializeDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }

    private void populateTrie() {
        for (Recipe recipe : mergedRecipes) {
            Arrays.stream(recipe.getName().toLowerCase().split("\\s+")).forEach(trie::insert);
            recipe.getdietaryOptions().forEach(option -> trie.insert(option.toLowerCase()));
        }
    }

    private List<Recipe> mergeRecipes(List<Recipe>... sources) {
        List<Recipe> allRecipes = new ArrayList<>();
        for (List<Recipe> source : sources) {
            allRecipes.addAll(source);
        }
        return allRecipes.stream().distinct().collect(Collectors.toList());
    }

    private List<String> extractDietaryOptions(String recipeName) {
        List<String> dietaryOptions = new ArrayList<>();
        String nameLowerCase = recipeName.toLowerCase();

        if (containsProtein(nameLowerCase, "chicken", "beef", "pork", "lamb", "fish", "turkey", "duck", "sausage", "seafood", "bacon", "poultry")) {
            dietaryOptions.add("Non-Veg");
        } else if (nameLowerCase.contains("veg") || nameLowerCase.contains("vegetarian")) {
            dietaryOptions.add("Veg");
        } else if (nameLowerCase.contains("vegan")) {
            dietaryOptions.add("Vegan");
        } else if (nameLowerCase.contains("keto")) {
            dietaryOptions.add("Keto");
        } else if (nameLowerCase.contains("gluten")) {
            dietaryOptions.add("Gluten");
        } else {
            dietaryOptions.add("Vegetarian");
        }

        return dietaryOptions;
    }

    private boolean containsProtein(String recipeName, String... proteins) {
        for (String protein : proteins) {
            if (recipeName.contains(protein)) {
                return true;
            }
        }
        return false;
    }
}
