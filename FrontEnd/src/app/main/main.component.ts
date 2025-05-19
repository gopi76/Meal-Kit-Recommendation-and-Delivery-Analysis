import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { trigger, transition, style, animate } from '@angular/animations';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { DataService } from '../../service/data.service'; 
interface SearchHistoryItem {
  word: string;
  frequency: number;
}

export interface SearchResponse {
  recipes: any[];
  wordCount: number;
  wordFrequencies: { [key: string]: number };
}

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss'],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms', style({ opacity: 1 })),
      ]),
      transition(':leave', [
        animate('300ms', style({ opacity: 0 })),
      ]),
    ]),
  ],
})
export class MainComponent implements OnInit {

  searchForm: FormGroup;
  requirementsForm: FormGroup;
  mealKitServices: any[] = [];
  filteredServices: any[] = [];
  randomRecipes: any[] = [];
  recommendations: any[] = [];
  spellCheckResults: string[] = [];
  wordCompletions: string[] = [];
  readonly MAX_RANDOM_RECIPES = 6; 
  wordSuggested: String[] = [];
  patternMatches: any[] = [];
  frequencyCount: { [key: string]: number } = {};
  searchFrequency: { [key: string]: number } = {};

  searchHistory: SearchHistoryItem[] = [];
  searchWordCount: number = 0;
  scrapedDataFrequencies: { [key: string]: number } = {};

  searchRecipes() {
    const term = this.searchForm.get('searchTerm')?.value;
    
    this.http.post<SearchResponse>(`http://localhost:8080/search/recipes?query=${term}`, {})
      .subscribe(response => {
        this.filteredServices = response.recipes;
        this.searchWordCount = response.wordCount;
        this.scrapedDataFrequencies = response.wordFrequencies;
        this.updateSearchHistory(term);
      });
  }

  constructor(private fb: FormBuilder, private http: HttpClient,private dataService: DataService) {
    this.searchForm = this.fb.group({
      searchTerm: ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s]+$/)]],
    });

    this.requirementsForm = this.fb.group({
      maxWeeklyCost: [100, [Validators.required, Validators.min(0)]],
      minMealsPerWeek: [3, [Validators.required, Validators.min(1)]],
      minServingsPerMeal: [2, [Validators.required, Validators.min(1)]],
      deliveryFrequency: ['Weekly'],
      dietaryPreferences: this.fb.group({
        vegetarian: [false],
        vegan: [false],
        keto: [false],
        glutenFree: [false],
      }),
      ingredientQualities: this.fb.group({
        organic: [false],
        local: [false],
        sustainable: [false],
      }),
    });
  }

  ngOnInit() {
    // Fetch all data on component initialization
    this.fetchAllData();
    this.http.get<any[]>('http://localhost:8080/search/all').subscribe(
      (data) => {
        this.mealKitServices = data;
        this.filteredServices = data; // Initialize with all data
        this.randomizeRecipes();
      },
      (error) => {
        console.error('Error fetching data:', error);
      }
    );

    this.searchForm.get('searchTerm')?.valueChanges .pipe(
      debounceTime(800), // Wait for 800ms after typing stops
      distinctUntilChanged() // Ensure the term is different from the last
    ).subscribe((term) => {
      if (term) {
        this.handleSearch(term.trim());
      }
    });

    // Fetch and display search history
    //this.fetchSearchHistory();
    this.clearSearchHistory()
  }
  findPatternInRecipes(pattern: string) {
    this.http.get<any[]>(`http://localhost:8080/search/pattern?pattern=${pattern}`)
      .subscribe({
        next: (matchedRecipes) => {
          this.filteredServices = matchedRecipes;
          
          // Optional: Add some additional logging or user feedback
          if (matchedRecipes.length === 0) {
            // You could show a no results found message
            console.log('No recipes found matching the pattern');
          }
        },
        error: (error) => {
          console.error('Error finding pattern in recipes:', error);
        }
      });
  }
  highlightPattern(text: string): string {
    const searchTerm = this.searchForm.get('searchTerm')?.value;
    if (!searchTerm) return text;
    
    const regex = new RegExp(`(${searchTerm})`, 'gi');
    return text.replace(regex, '<mark>$1</mark>');
  }
  
  // Check if a service is a pattern match
  isPatternMatch(service: any): boolean {
    return this.patternMatches.some(match => match.name === service.name);
  }
  fetchAllData(): void {
    this.dataService.getAllData().subscribe(
      (data) => {
        // Store the fetched data in the recommendations array
        this.recommendations = data;
        console.log(this.recommendations)
      },
      (error) => {
        console.error('Error fetching data: ', error);
      }
    );
  }
  handleSearch(term: string) {
    // Validate the search term
    const isValidSearch = /^[a-zA-Z\s]+$/.test(term);
    if (!isValidSearch) {
      console.error('Invalid search term');
      this.filteredServices = [];
      return;
    }

    // Update search history on the backend
    this.updateSearchHistory(term);

    // Call getWordCompletions to retrieve suggestions
    this.getWordCompletions(term);

    this.getSuggestedWord(term);

    // Call backend for recipe search
    this.http.post<any[]>(`http://localhost:8080/search/recipes?query=${term}`, {}).subscribe(
      (data) => {
        this.filteredServices = data;
      },
      (error) => {
        console.error('Error during recipe search:', error);
      }
    );

  }

  // New method to update search history
  updateSearchHistory(term: string) {
  this.http.post('http://localhost:8080/search/updateSearchHistory', { query: term })
    .subscribe({
      next: () => {
        // Refresh search history after updating
        this.fetchSearchHistory();
      },
      error: (err) => {
        console.error('Error updating search history', err);
      }
    });
  }
  clearSearchHistory() {
    this.dataService.clearSearchHistory().subscribe(
      (response) => {
        console.log('Search history cleared successfully');
        // Optionally reset local data here if needed
      },
      (error) => {
        console.error('Error clearing search history:', error);
      }
    );
  }
  // New method to fetch search history
  fetchSearchHistory() {
    this.http.get<{ word: string, frequency: number }[]>('http://localhost:8080/search/searchHistory')
      .subscribe({
        next: (history) => {
          this.searchHistory = this.uniqueByWord(history); // Deduplicate history here
        },
        error: (err) => {
          console.error('Error fetching search history', err);
        }
      });
  }

  uniqueByWord(history: SearchHistoryItem[]): SearchHistoryItem[] {
    const uniqueHistory = new Map<string, SearchHistoryItem>();
    history.forEach(item => uniqueHistory.set(item.word, item));
    return Array.from(uniqueHistory.values());
  }

  getWordCompletions(term: string) {
    this.http.get<string[]>(`http://localhost:8080/search/suggestions?prefix=${term}`).subscribe(
      (data) => {
        this.wordCompletions = data;
      },
      (error) => {
        console.error('Error fetching word completions:', error);
      }
    );
  }

  getSuggestedWord(term: string) {
    this.http.get<string[]>(`http://localhost:8080/search11/suggestedwords?prefix=${term}`).subscribe(
      (data) => {
        this.wordSuggested = data;
      },
      (error) => {
        console.error('Error fetching suggested words:', error);
      }
    );
  }
  onSuggestedWordSelect(word: String): void {
    this.searchForm.get('searchTerm')?.setValue(word); // Update input field with the selected word
    this.wordSuggested = []; // Clear suggestions after selection
  }
  private randomizeRecipes() {
    if (this.mealKitServices.length > 0) {
      const shuffled = [...this.mealKitServices].sort(() => 0.5 - Math.random());
      this.randomRecipes = shuffled.slice(0, this.MAX_RANDOM_RECIPES);
    }
  }
  onSearchInput(): void {
    const searchTerm = this.searchForm.value.searchTerm;

    if (searchTerm.length > 2) {
      this.fetchWordCompletions(searchTerm);
    } else {
      this.wordCompletions = [];  // Clear completions if search term is too short
    }
  }

  // Function to call backend or generate word completions
  fetchWordCompletions(term: string): void {
    // Make an API call or process the term to find completions
    this.http.get<string[]>(`http://localhost:8080/word-completion?query=${term}`).subscribe(
      (completions) => {
        this.wordCompletions = completions;
      },
      (error) => {
        console.error('Error fetching word completions', error);
      }
    );
  }

  // Function to select a word from the dropdown
  selectWord(word: string): void {
    this.searchForm.patchValue({ searchTerm: word });
    this.wordCompletions = []; // Clear completions once word is selected
    this.searchRecipes(); // Trigger the search based on the selected word
  }
  generateRecommendations() {
    const requirements = this.requirementsForm.value;
    console.log('User Requirements:', requirements);
  
    // Filter recommendations based on user requirements
    this.filteredServices = this.recommendations.filter((service) => {
      const meetsWeeklyCostRequirement = service.parsedPrice <= requirements.maxWeeklyCost;
      const meetsMealsPerWeekRequirement = service.parsedServes >= requirements.minMealsPerWeek;
      const meetsServingsPerMealRequirement = service.parsedServes >= requirements.minServingsPerMeal;
      const meetsCookingTimeRequirement = service.parsedCookingTime <= (requirements.parsedCookingTime || Infinity);
      const meetsDietaryRequirement = this.checkDietaryOptions(service.dietaryOptions, requirements.dietaryPreferences);
  
      return (
        meetsWeeklyCostRequirement &&
        meetsMealsPerWeekRequirement &&
        meetsServingsPerMealRequirement &&
        meetsCookingTimeRequirement &&
        meetsDietaryRequirement
      );
    });
  
    console.log('Filtered Recommendations:', this.filteredServices);
  
    if (this.filteredServices.length === 0) {
      console.log('No recommendations match your criteria.');
    }
  }
  checkDietaryOptions(dietaryOptions: string[], dietaryPreferences: any): boolean {
    const dietaryPreferencesList = [
      { key: 'vegetarian', value: ['Vegetarian', 'Veg'] },
      { key: 'vegan', value: ['Vegan'] },
      { key: 'glutenFree', value: ['Gluten Free'] },
      // Add more dietary preferences as necessary
    ];
  
    for (let pref of dietaryPreferencesList) {
      if (dietaryPreferences[pref.key]) {
        // Check if the dietaryOptions array contains any of the preferred values
        if (!dietaryOptions.some(option => pref.value.includes(option))) {
          return false; // If a preference is required but not found, return false
        }
      }
    }
  
    return true; // All dietary preferences match
  }
}
