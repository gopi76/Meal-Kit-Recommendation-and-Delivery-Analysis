# Meal Kit Delivery Service Analysis System

## Overview
A comprehensive meal kit delivery service analysis system that provides recipe recommendations, search functionalities, and user interaction features. The system crawls multiple meal kit delivery websites, processes the data, and presents it through an intuitive interface.

## Features

### 🔍 Web Scraping
- Multi-site data collection from HelloFresh, Fresh Prep, Home Chef, Dinnerly, and Snap Kitchen
- Automated data extraction using Selenium WebDriver and Jsoup
- Efficient handling of dynamic content and JavaScript-heavy pages

### 📊 Advanced Search & Ranking
- **Page Ranking System**: Implements AVL tree for efficient price-based ranking
- **Inverted Indexing**: Uses Red-Black Tree for optimized search operations
- **Pattern Matching**: Implements KMP algorithm for efficient recipe pattern searching
- **Word Completion**: Utilizes Trie data structure for quick search suggestions

### 📈 Analytics & Processing
- **Frequency Count**: Tracks and analyzes word frequencies using HashMap
- **Search Frequency**: Monitors search patterns using Binary Search Tree
- **Edit Distance Algorithm**: Provides alternate word suggestions
- **Regular Expression Pattern Matching**: Advanced recipe pattern recognition

### 💡 Smart Recommendations
- Personalized recipe suggestions based on user preferences
- Collaborative and content-based filtering
- Dynamic updates based on user interactions

## Technologies Used

### Backend
- Java (Core Technology)
- Selenium WebDriver
- SpringBoot, Maven
- Jsoup HTML Parser

### FrontEnd
- Angular

### Data Structures
- AVL Trees
- Red-Black Trees
- Binary Search Trees
- Tries
- HashMaps

### Algorithms
- KMP (Knuth-Morris-Pratt) Algorithm
- Edit Distance Algorithm
- Regular Expression Pattern Matching

## Video Demo

[![Project Demo Video](https://img.youtube.com/vi/C_0x5NGA01w/maxresdefault.jpg)](https://youtu.be/C_0x5NGA01w)

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Acknowledgments
- Dr. Olena Syrotkina (Project Supervisor)
