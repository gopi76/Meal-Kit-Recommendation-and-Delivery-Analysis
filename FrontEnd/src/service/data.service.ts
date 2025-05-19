import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private apiUrl = 'http://localhost:8080/search/all';  // The URL from which we fetch data
  private searchHistory ="http://localhost:8080/search";

  constructor(private http: HttpClient) {}

  // Fetch data from the server
  getAllData(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }
  clearSearchHistory(): Observable<any> {
    return this.http.delete(`${this.searchHistory}/searchHistory`);
  }
}
