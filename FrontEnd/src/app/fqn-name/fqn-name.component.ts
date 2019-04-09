import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {CommonModule} from '@angular/common';
@Component({
    selector: 'app-fqn-name',
    templateUrl: './fqn-name.component.html',
    styleUrls: ['./fqn-name.component.css']
})
export class FqnNameComponent implements OnInit {

    constructor(private http: HttpClient) { }
    name: string;
    fqn: string;
    nodes: any[];
    onNameKeyUp(event: any) {
        this.name = event.target.value;
    }

    ngOnInit() {
        this.http.get('http://localhost:9080/BackEnd/app/admin/class')
            .subscribe(
                (nodes) => {
                   this.nodes = nodes;

                }
            );
    }

}
