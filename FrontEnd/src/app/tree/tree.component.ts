import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnInit } from '@angular/core';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { HttpClient } from '@angular/common/http';

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
    selector: 'app-tree',
    templateUrl: './tree.component.html',
    styleUrls: ['./tree.component.css']
})


export class TreeComponent implements OnInit {
    TREE_DATA: TreeNode[];
    hasChild: any;

    private transformer = (node: TreeNode, level: number) => {
        return {
            expandable: !!node.dependsOn && node.dependsOn.length > 0,
            name: node.name,
            id: node.id,
            level: level,
        };
    }

    treeControl = new FlatTreeControl<ExampleFlatNode>(
        node => node.level, node => node.expandable);

    treeFlattener = new MatTreeFlattener(
        this.transformer, node => node.level, node => node.expandable, node => node.dependsOn);

    dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    constructor(private http: HttpClient) {

    }

    ngOnInit() {

        this.http.get('http://localhost:9080/BackEnd/app/admin/tree/').subscribe((data: any[]) => {         
            this.dataSource.data = data;
        });


        this.hasChild = (_: number, node: ExampleFlatNode) => node.expandable;
        
    }

}
