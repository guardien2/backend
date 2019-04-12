import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material';


@Component({
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit {

    lastInput = "";
    nodes: any[];
    fqn = "";
    sourceFileName = "";
    name = "";

    dataSource;
    displayedColumns = [];

    /**
     * Pre-defined columns list for user table
     */
    columnNames =
        [{ id: "name", value: "Name" },
        { id: "fqn", value: "FQN" },
        { id: "sourceFileName", value: "SourceFileName" }];

    ngOnInit() {
        this.displayedColumns = this.columnNames.map(x => x.id);

    }

    constructor(private http: HttpClient) { }

    searchClicked(newInput: string) {
        this.lastInput = newInput;
        this.http.get('http://localhost:9080/BackEnd/app/admin/usedby/' + newInput).subscribe((data: any[]) => {
            this.nodes = data;
            this.dataSource = new MatTableDataSource(data);

        });
    }
}
