import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { FlatTreeControl } from '@angular/cdk/tree';

interface TreeNode {
    name: any;
    id: any;
    sourceFileName: any;
    fqn: any;
    fileName: any;
    dependsOn?: TreeNode[];
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
            expandable: !!node.dependsOn && node.dependsOn.length > 0,
            name: node.name,            
            level: level,
        };
    }

    treeControl = new FlatTreeControl<ExampleFlatNode>(node => node.level, node => node.expandable);

    treeFlattener = new MatTreeFlattener(this.transformer, node => node.level, node => node.expandable, node => node.dependsOn);

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
    showTable: boolean = false;
    showTree: boolean = false;
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

        if (this.selectedValue == 'usedby') {
            
            
            this.http.get('http://localhost:9080/BackEnd/app/admin/search/' + this.selectedValue + '/' + newInput).subscribe((data: any[]) => {
                this.nodes = data;
                this.dataSource = new MatTableDataSource(data);
                 this.displayedColumns = this.columnNames.map(x => x.id);
                //          console.log(this.foods.values);

            });
            
       //     this.showTable = true;
            //   this.TreeDataSource = null;
            this.showTree = false;
            this.showTable = true;
           
        }

        if (this.selectedValue == 'fullexpansion') {
            this.showTable = false;

                this.http.get('http://localhost:9080/BackEnd/app/admin/tree/' + this.selectedValue + '/').subscribe((data: any[]) => {
                this.TreeDataSource.data = data;
            });
           this.showTree = true;
        }

    }

    typeSelected(event) {

        this.typeSlectedValue = event.value;
    }

    selected(event) {

        this.selectedValue = event.value;
    }
}
