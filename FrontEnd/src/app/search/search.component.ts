import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

    lastInput = "";
    constructor(private http: HttpClient) { }

    ngOnInit() {
    }

    searchClicked(newInput: string) {
        this.lastInput = newInput;
        this.http.get('http://localhost:9080/BackEnd/app/admin/usedby/' + newInput).subscribe();
    }

}
