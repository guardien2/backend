import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-names',
  templateUrl: './names.component.html',
  styleUrls: ['./names.component.css']
})
export class NamesComponent implements OnInit {

  constructor(private http: HttpClient) { }


    getName() {
        return this.http.get('http://localhost:9080/BackEnd/app/admin/class');
    }
    test() {
        return 'hej';
    }

  ngOnInit() {
  }

}
