package com.example.mealRecommend.model;

import java.util.List;

public class Recipe {
    private String websiteName;
    private String websiteURL;
    private String name;
    private String imageUrl;
    private String serves;
    private String cookingTime;
    private List<String> dietaryOptions;
    private String price; // New field for price

    // Getters and setters
    public String getWebsiteName() {return websiteName;}

    public void setWebsiteName(String websiteName) {this.websiteName = websiteName;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteURL() {return websiteURL;}

    public void setWebsiteURL(String websiteURL) {this.websiteURL = websiteURL;}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServes() {
        return serves;
    }

    public void setServes(String serves) {
        this.serves = serves;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public List<String> getdietaryOptions() {
        return dietaryOptions;
    }

    public void setdietaryOptions(List<String> dietaryOptions) {
        this.dietaryOptions = dietaryOptions;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getParsedPrice() {
        try {
            return Double.parseDouble(price.replace("$", "").trim());
        } catch (NumberFormatException e) {
            return 0.0; // Default value if parsing fails
        }
    }

    // Instance method to get parsed cooking time for sorting
    public int getParsedCookingTime() {
        return parseCookingTime(this.cookingTime);
    }

    // Static method to parse cooking time, fixing edge cases
    public int parseCookingTime(String cookingTime) {
        if (cookingTime == null || cookingTime.isEmpty()) {
            return 0;
        }

        cookingTime = cookingTime.toLowerCase().trim();
        int totalMinutes = 0;

        // Handle "hour" case
        if (cookingTime.contains("hour")) {
            String[] parts = cookingTime.split("hour");
            try {
                totalMinutes += Integer.parseInt(parts[0].trim()) * 60;
            } catch (NumberFormatException e) {
                totalMinutes += 0; // Default to 0 if parsing fails
            }
        }

        // Handle "minute" case
        if (cookingTime.contains("minute") || cookingTime.contains("minutes")) {
            String[] parts = cookingTime.split("minute|minutes");
            try {
                totalMinutes += Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                totalMinutes += 0; // Default to 0 if parsing fails
            }
        }

        return totalMinutes;
    }


    // Parse serves as an integer
    public int getParsedServes() {
        try {
            return Integer.parseInt(serves.trim());
        } catch (NumberFormatException e) {
            return 0; // Default value if parsing fails
        }
    }
}