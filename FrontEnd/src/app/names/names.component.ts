import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-names',
  templateUrl: './names.component.html',
  styleUrls: ['./names.component.css']
})
export class NamesComponent implements OnInit {

  constructor(private http: HttpClient) {}
    name: string ;
    fqn: string ;
    onNameKeyUp(event: any) {
        this.name = event.target.value;
    }

    getName() {
        this.http.get('http://localhost:9080/BackEnd/app/admin/class')
        .subscribe(
            (data: any[]) => {
                this.name = data[1].name;
                this.fqn = data[1].fqn;
                console.log(data);
            }
        );
    }


  ngOnInit() {
  }

}
