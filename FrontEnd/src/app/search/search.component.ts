import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { FlatTreeControl } from '@angular/cdk/tree';
import { stringify } from '@angular/core/src/util';

declare function DrawD3Tree(searchValue): any;
declare function RemoveTree(): any;

interface TreeNode {
    name: any;
    id: any;
    sourceFileName: any;
    fqn: any;
    fileName: any;
    children?: TreeNode[];
}

interface ExampleFlatNode {
    expandable: boolean;
    name: string;
    level: number;
}

@Component({
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit {

    //Tree
    TREE_DATA: TreeNode[];
    hasChild: any;

    private transformer = (node: TreeNode, level: number) => {
        return {
            expandable: !!node.children && node.children.length > 0,
            name: node.name,
            level: level,
        };
    }

    treeControl = new FlatTreeControl<ExampleFlatNode>(node => node.level, node => node.expandable);

    treeFlattener = new MatTreeFlattener(this.transformer, node => node.level, node => node.expandable, node => node.children);

    TreeDataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

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
    nodes: any[];
    fqn = "";
    sourceFileName = "";
    name = "";
    selectedValue = "";
    typeSlectedValue = "";
    typeSelectedRest = "";
    showUsedBy: boolean = false;
    showFullExpansion: boolean = false;
    dataSource: any;
    displayedColumns = [];

    /**
     * Pre-defined columns list for user table
     */
    columnNames =
        [{ id: "name", value: "Name" },
        { id: "fqn", value: "FQN" },
        { id: "sourceFileName", value: "SourceFileName" }];

    ngOnInit() {
        this.hasChild = (_: number, node: ExampleFlatNode) => node.expandable;
    }

    constructor(private http: HttpClient) { }

    searchClicked(newInput: string) {
        this.lastInput = newInput;
        RemoveTree();

        if (this.selectedValue == 'usedby') {

            this.GetTreeFromREST(newInput);
            this.showFullExpansion = false;
            this.showUsedBy = true;

        }

        if (this.selectedValue == 'fullexpansion') {
            this.showUsedBy = false;
            this.GetTreeFromREST(newInput);
            this.showFullExpansion = true;
        }

    }

    ViewD3Tree(newInput: string) {
        this.showUsedBy = false;
        this.showFullExpansion = false;
        DrawD3Tree("http://localhost:9080/BackEnd/app/admin/tree/" + this.selectedValue + "/" + newInput + "/"+true);

    }


    /*GetUsedByFromREST(value: string) {
        this.http.get('http://localhost:9080/BackEnd/app/admin/search/' + this.selectedValue + '/' + value).subscribe((data: any[]) => {
            this.nodes = data;
            this.dataSource = new MatTableDataSource(data);
            this.displayedColumns = this.columnNames.map(x => x.id);
            return data;
        });

    }*/

    GetTreeFromREST(value: string) {
        this.http.get('http://localhost:9080/BackEnd/app/admin/tree/' + this.selectedValue + '/' + value + '/' + false).subscribe((data: any[]) => {
            this.TreeDataSource.data = data;
        });
    }





    selected(event) {
        // alert(event.value);
        this.selectedValue = event.value;


    }
}
