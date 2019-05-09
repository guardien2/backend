import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PageSettingsModel } from '@syncfusion/ej2-angular-treegrid';

declare function drawNodeGraph(searchValue): any;
declare function DrawD3Tree(searchValue): any;
declare function RemoveD3Tree(): any;

@Component({
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit {

    //TreeGrid stuff
    public treeGridData: object[];
    public pageSettings: PageSettingsModel;


    //Search
    value: string;
    viewValue: string;

    foods: any[] = [
        { value: 'usedby', viewValue: 'Used By' },
        { value: 'fullexpansion', viewValue: 'Full Expansion' },
        { value: 'crud', viewValue: 'CRUD' },
        { value: 'flowin', viewValue: 'Flow in' },
        { value: 'flowout', viewValue: 'Flow out' }
    ];

    types: any[] = [
        { value: 'server', viewValue: 'Server' },
        { value: 'client', viewValue: 'Client' },
        { value: 'table', viewValue: 'Table' },
    ];

    lastInput = "";
    selectedValue = "";
    treeGridDiv: boolean = true;

    ngOnInit() {
        this.pageSettings = { pageSize: 6 };
        this.treeGridData = [];
    }

    constructor(private http: HttpClient) { }

    searchClicked(newInput: string) {
        this.treeGridDiv = true;
        this.lastInput = newInput;
        RemoveD3Tree();

        if (this.selectedValue == 'usedby' || this.selectedValue == 'fullexpansion') {
            this.GetTreeFromREST(newInput);
        }
    }

    ViewD3Tree(newInput: string) {
        this.treeGridDiv = false;
        DrawD3Tree("http://localhost:9080/BackEnd/app/admin/tree/" + this.selectedValue + "/" + newInput + "/" + true);

    }

    ViewNodeRelation(newInput: any) {
        this.treeGridDiv = false;
        drawNodeGraph("http://localhost:9080/BackEnd/app/admin/NodeGraph/" + this.selectedValue + "/" + newInput);
    }
    
    GetTreeFromREST(value: string) {
        this.http.get('http://localhost:9080/BackEnd/app/admin/tree/' + this.selectedValue + '/' + value + '/' + false).subscribe((data: any[]) => {
            this.treeGridData = data;
        });
    }

    selected(event) {
        this.selectedValue = event.value;
    }
}
