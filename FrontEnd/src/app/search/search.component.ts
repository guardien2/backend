import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { FlatTreeControl } from '@angular/cdk/tree';
import { stringify } from '@angular/core/src/util';
import { PageSettingsModel } from '@syncfusion/ej2-angular-treegrid';
import { treeSampleData } from './treedatasource2';
import { DataManager } from '@syncfusion/ej2-data';


declare function drawNodeGraph(searchValue): any;
declare function removeNodeGraph(): any;
declare function DrawD3Tree(searchValue): any;
declare function RemoveD3Tree(): any;

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

    //TreeGrid stuff
    public treeGridData: object[];
    public pageSettings: PageSettingsModel;



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
    //  showUsedBy: boolean = false;
    treeGridDiv: boolean = true;
    //  showFullExpansion: boolean = false;
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
        // this.treeGridData = treeSampleData;
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

    GetTreeFromREST(value: string) {
        this.http.get('http://localhost:9080/BackEnd/app/admin/tree/' + this.selectedValue + '/' + value + '/' + false).subscribe((data: any[]) => {
            //this.TreeDataSource.data = data;
            this.treeGridData = data;
        });
    }

    ViewNodeRelation(newInput: any) {
        this.treeGridDiv = false;
        drawNodeGraph("http://localhost:9080/BackEnd/app/admin/NodeGraph/" + this.selectedValue + "/" + newInput);
    }

    selected(event) {
        // alert(event.value);
        this.selectedValue = event.value;
    }




}
